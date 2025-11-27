
package org.eclipse.tm4e.core.internal.grammar;

import static org.eclipse.tm4e.core.internal.utils.NullSafetyHelper.castNonNull;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.internal.oniguruma.OnigCaptureIndex;
import org.eclipse.tm4e.core.internal.oniguruma.OnigScannerMatch;
import org.eclipse.tm4e.core.internal.oniguruma.OnigString;
import org.eclipse.tm4e.core.internal.rule.BeginEndRule;
import org.eclipse.tm4e.core.internal.rule.BeginWhileRule;
import org.eclipse.tm4e.core.internal.rule.CaptureRule;
import org.eclipse.tm4e.core.internal.rule.CompiledRule;
import org.eclipse.tm4e.core.internal.rule.MatchRule;
import org.eclipse.tm4e.core.internal.rule.Rule;
import org.eclipse.tm4e.core.internal.rule.RuleId;
import io.github.rosemoe.sora.util.Logger;

final class LineTokenizer {
    private static final Logger LOGGER = Logger.instance(LineTokenizer.class.getName());
	private record LocalStackElement(AttributedScopeStack scopes, int endPos) {
	}
	private static class MatchResult {
		final OnigCaptureIndex[] captureIndices;
		final RuleId matchedRuleId;
		MatchResult(final RuleId matchedRuleId, final OnigCaptureIndex[] captureIndices) {
			this.matchedRuleId = matchedRuleId;
			this.captureIndices = captureIndices;
		}
	}
	private static final class MatchInjectionsResult extends MatchResult {
		final boolean isPriorityMatch;
		MatchInjectionsResult(final RuleId matchedRuleId, final OnigCaptureIndex[] captureIndices, final boolean isPriorityMatch) {
			super(matchedRuleId, captureIndices);
			this.isPriorityMatch = isPriorityMatch;
		}
	}
	@NonNullByDefault({})
	private record WhileCheckResult(
			@NonNull StateStack stack,
			int linePos,
			int anchorPosition,
			boolean isFirstLine) {
	}
	static final class TokenizeStringResult {
		public final StateStack stack;
		public final boolean stoppedEarly;
		TokenizeStringResult(final StateStack stack, final boolean stoppedEarly) {
			this.stack = stack;
			this.stoppedEarly = stoppedEarly;
		}
	}
	private final Grammar grammar;
	private final OnigString lineText;
	private boolean isFirstLine;
	private int linePos;
	private StateStack stack;
	private final LineTokens lineTokens;
	private int anchorPosition = -1;
	private boolean stop;
	private LineTokenizer(final Grammar grammar, final OnigString lineText, final boolean isFirstLine, final int linePos,
			final StateStack stack, final LineTokens lineTokens) {
		this.grammar = grammar;
		this.lineText = lineText;
		this.isFirstLine = isFirstLine;
		this.linePos = linePos;
		this.stack = stack;
		this.lineTokens = lineTokens;
	}
	private TokenizeStringResult scan(final boolean checkWhileConditions, final long timeLimit) {
		stop = false;
		if (checkWhileConditions) {
			final var whileCheckResult = checkWhileConditions(grammar, lineText, isFirstLine, linePos, stack, lineTokens);
			stack = whileCheckResult.stack;
			linePos = whileCheckResult.linePos;
			isFirstLine = whileCheckResult.isFirstLine;
			anchorPosition = whileCheckResult.anchorPosition;
		}
		final var startTime = System.currentTimeMillis();
		while (!stop) {
			if (timeLimit > 0) {
				final var elapsedTime = System.currentTimeMillis() - startTime;
				if (elapsedTime > timeLimit) {
					return new TokenizeStringResult(stack, true);
				}
			}
			scanNext(); 
		}
		return new TokenizeStringResult(stack, false);
	}
    private void scanNext() {
		final MatchResult r = matchRuleOrInjections(grammar, lineText, isFirstLine, linePos, stack, anchorPosition);
        if (r == null) {
            lineTokens.produce(stack, lineText.content.length());
            stop = true;
            return;
        }
		final OnigCaptureIndex[] captureIndices = r.captureIndices;
		final RuleId matchedRuleId = r.matchedRuleId;
		final boolean hasAdvanced = captureIndices.length > 0 && captureIndices[0].end > linePos;
		if (matchedRuleId.equals(RuleId.END_RULE)) {
			final BeginEndRule poppedRule = (BeginEndRule) stack.getRule(grammar);
			lineTokens.produce(stack, captureIndices[0].start);
			stack = stack.withContentNameScopesList(stack.nameScopesList);
			handleCaptures(grammar, lineText, isFirstLine, stack, lineTokens, poppedRule.endCaptures, captureIndices);
			lineTokens.produce(stack, captureIndices[0].end);
			final var popped = stack;
			stack = castNonNull(stack.pop());
			anchorPosition = popped.getAnchorPos();
            if (!hasAdvanced && popped.getEnterPos() == linePos) {
                stack = popped;
				lineTokens.produce(stack, lineText.content.length());
				stop = true;
				return;
			}
		} else if (captureIndices.length > 0) {
			final Rule rule = grammar.getRule(matchedRuleId);
			lineTokens.produce(stack, captureIndices[0].start);
			final StateStack beforePush = stack;
			final var scopeName = rule.getName(lineText.content, captureIndices);
			final var nameScopesList = castNonNull(stack.contentNameScopesList).pushAttributed(scopeName, grammar);
			stack = stack.push(
					matchedRuleId,
					linePos,
					anchorPosition,
					captureIndices[0].end == lineText.content.length(),
					null,
					nameScopesList,
					nameScopesList);
			if (rule instanceof final BeginEndRule pushedRule) {
				handleCaptures(
						grammar,
						lineText,
						isFirstLine,
						stack,
						lineTokens,
						pushedRule.beginCaptures,
						captureIndices);
				lineTokens.produce(stack, captureIndices[0].end);
				anchorPosition = captureIndices[0].end;
				final var contentName = pushedRule.getContentName(lineText.content, captureIndices);
				final var contentNameScopesList = nameScopesList.pushAttributed(contentName, grammar);
				stack = stack.withContentNameScopesList(contentNameScopesList);
				if (pushedRule.endHasBackReferences) {
					stack = stack.withEndRule(
							pushedRule.getEndWithResolvedBackReferences(
									lineText.content,
									captureIndices));
				}
				if (!hasAdvanced && beforePush.hasSameRuleAs(stack)) {
					stack = castNonNull(stack.pop());
					lineTokens.produce(stack, lineText.content.length());
					stop = true;
					return;
				}
			} else if (rule instanceof final BeginWhileRule pushedRule) {
				handleCaptures(
						grammar,
						lineText,
						isFirstLine,
						stack,
						lineTokens,
						pushedRule.beginCaptures,
						captureIndices);
				lineTokens.produce(stack, captureIndices[0].end);
				anchorPosition = captureIndices[0].end;
				final var contentName = pushedRule.getContentName(lineText.content, captureIndices);
				final var contentNameScopesList = nameScopesList.pushAttributed(contentName, grammar);
				stack = stack.withContentNameScopesList(contentNameScopesList);
				if (pushedRule.whileHasBackReferences) {
					stack = stack.withEndRule(
							pushedRule.getWhileWithResolvedBackReferences(
									lineText.content,
									captureIndices));
				}
				if (!hasAdvanced && beforePush.hasSameRuleAs(stack)) {
					stack = castNonNull(stack.pop());
					lineTokens.produce(stack, lineText.content.length());
					stop = true;
					return;
				}
			} else {
				final MatchRule matchingRule = (MatchRule) rule;
				handleCaptures(
						grammar,
						lineText,
						isFirstLine,
						stack,
						lineTokens,
						matchingRule.captures,
						captureIndices);
				lineTokens.produce(stack, captureIndices[0].end);
				stack = castNonNull(stack.pop());
				if (!hasAdvanced) {
					stack = stack.safePop();
					lineTokens.produce(stack, lineText.content.length());
					stop = true;
					return;
				}
			}
		}
		if (captureIndices.length > 0 && captureIndices[0].end > linePos) {
			linePos = captureIndices[0].end;
			isFirstLine = false;
		}
	}
	@Nullable
	private MatchResult matchRule(final Grammar grammar, final OnigString lineText, final boolean isFirstLine, final int linePos,
			final StateStack stack, final int anchorPosition) {
		final var rule = stack.getRule(grammar);
		final var ruleScanner = rule.compileAG(grammar, stack.endRule, isFirstLine, linePos == anchorPosition);
		final OnigScannerMatch r = ruleScanner.scanner.findNextMatch(lineText, linePos);
		if (r != null) {
			return new MatchResult(ruleScanner.rules[r.index], r.getCaptureIndices());
		}
		return null;
	}
	@Nullable
	private MatchResult matchRuleOrInjections(final Grammar grammar, final OnigString lineText, final boolean isFirstLine,
			final int linePos, final StateStack stack, final int anchorPosition) {
		final MatchResult matchResult = matchRule(grammar, lineText, isFirstLine, linePos, stack, anchorPosition);
		final List<Injection> injections = grammar.getInjections();
		if (injections.isEmpty()) {
			return matchResult;
		}
		final var injectionResult = matchInjections(injections, grammar, lineText, isFirstLine, linePos, stack, anchorPosition);
		if (injectionResult == null) {
			return matchResult;
		}
		if (matchResult == null) {
			return injectionResult;
		}
		final int matchResultScore = matchResult.captureIndices[0].start;
		final int injectionResultScore = injectionResult.captureIndices[0].start;
		if (injectionResultScore < matchResultScore || injectionResult.isPriorityMatch && injectionResultScore == matchResultScore) {
			return injectionResult;
		}
		return matchResult;
	}
	@Nullable
	private MatchInjectionsResult matchInjections(final List<Injection> injections, final Grammar grammar, final OnigString lineText,
			final boolean isFirstLine, final int linePos, final StateStack stack, final int anchorPosition) {
		var bestMatchRating = Integer.MAX_VALUE;
		OnigCaptureIndex[] bestMatchCaptureIndices = null;
		var bestMatchRuleId = RuleId.END_RULE;
		var bestMatchResultPriority = 0;
		final List<String> scopes = stack.contentNameScopesList != null ? stack.contentNameScopesList.getScopeNames()
				: Collections.emptyList();
		for (int i = 0, len = injections.size(); i < len; i++) {
			final var injection = injections.get(i);
			if (!injection.matches(scopes)) {
				continue;
			}
			final var rule = grammar.getRule(injection.ruleId);
			final var ruleScanner = rule.compileAG(grammar, null, isFirstLine, linePos == anchorPosition);
			final var matchResult = ruleScanner.scanner.findNextMatch(lineText, linePos);
			if (matchResult == null) {
				continue;
			}
            final int matchRating = matchResult.getCaptureIndices()[0].start;
            if (matchRating > bestMatchRating) {
                continue;
            }
			bestMatchRating = matchRating;
			bestMatchCaptureIndices = matchResult.getCaptureIndices();
			bestMatchRuleId = ruleScanner.rules[matchResult.index];
			bestMatchResultPriority = injection.priority;
			if (bestMatchRating == linePos) {
				break;
			}
		}
		if (bestMatchCaptureIndices != null) {
			return new MatchInjectionsResult(
					bestMatchRuleId,
					bestMatchCaptureIndices,
					bestMatchResultPriority == -1);
		}
		return null;
	}
	private void handleCaptures(final Grammar grammar, final OnigString lineText, final boolean isFirstLine, final StateStack stack,
			final LineTokens lineTokens, final List<@Nullable CaptureRule> captures, final OnigCaptureIndex[] captureIndices) {
		if (captures.isEmpty()) {
			return;
		}
		final var lineTextContent = lineText.content;
		final int len = Math.min(captures.size(), captureIndices.length);
		final var localStack = new ArrayDeque<LocalStackElement>();
		final int maxEnd = captureIndices[0].end;
		for (int i = 0; i < len; i++) {
			final var captureRule = captures.get(i);
			if (captureRule == null) {
				continue;
			}
			final var captureIndex = captureIndices[i];
			if (captureIndex.getLength() == 0) {
				continue;
			}
			if (captureIndex.start > maxEnd) {
				break;
			}
			while (!localStack.isEmpty() && localStack.getLast().endPos <= captureIndex.start) {
				final var lastElem = localStack.removeLast();
				lineTokens.produceFromScopes(lastElem.scopes, lastElem.endPos);
			}
			if (!localStack.isEmpty()) {
				lineTokens.produceFromScopes(localStack.getLast().scopes, captureIndex.start);
			} else {
				lineTokens.produce(stack, captureIndex.start);
			}
			final var retokenizeCapturedWithRuleId = captureRule.retokenizeCapturedWithRuleId;
			if (retokenizeCapturedWithRuleId.notEquals(RuleId.NO_RULE)) {
				final var scopeName = captureRule.getName(lineTextContent, captureIndices);
				final var nameScopesList = castNonNull(stack.contentNameScopesList).pushAttributed(scopeName, grammar);
				final var contentName = captureRule.getContentName(lineTextContent, captureIndices);
				final var contentNameScopesList = nameScopesList.pushAttributed(contentName, grammar);
				final var stackClone = stack.push(retokenizeCapturedWithRuleId, captureIndex.start, -1, false, null, nameScopesList,
						contentNameScopesList);
				final var onigSubStr = OnigString.of(lineTextContent.substring(0, captureIndex.end));
				tokenizeString(grammar, onigSubStr, isFirstLine && captureIndex.start == 0, captureIndex.start, stackClone, lineTokens,
						false, Duration.ZERO );
				continue;
			}
			final var captureRuleScopeName = captureRule.getName(lineTextContent, captureIndices);
			if (captureRuleScopeName != null) {
				final var base = localStack.isEmpty() ? stack.contentNameScopesList : localStack.getLast().scopes;
				final var captureRuleScopesList = castNonNull(base).pushAttributed(captureRuleScopeName, grammar);
				localStack.add(new LocalStackElement(captureRuleScopesList, captureIndex.end));
			}
		}
		while (!localStack.isEmpty()) {
			final var lastElem = localStack.removeLast();
			lineTokens.produceFromScopes(lastElem.scopes, lastElem.endPos);
		}
	}
	private WhileCheckResult checkWhileConditions(final Grammar grammar, final OnigString lineText, boolean isFirstLine, int linePos,
			StateStack stack, final LineTokens lineTokens) {
		int anchorPosition = stack.beginRuleCapturedEOL ? 0 : -1;
		final class WhileStack {
			final StateStack stack;
			final BeginWhileRule rule;
			WhileStack(final StateStack stack, final BeginWhileRule rule) {
				this.stack = stack;
				this.rule = rule;
			}
		}
		final var whileRules = new ArrayList<WhileStack>();
		for (StateStack node = stack; node != null; node = node.pop()) {
			final Rule nodeRule = node.getRule(grammar);
			if (nodeRule instanceof final BeginWhileRule beginWhileRule) {
				whileRules.add(new WhileStack(node, beginWhileRule));
			}
		}
		for (int i = whileRules.size() - 1; i >= 0; i--) {
			final var whileRule = whileRules.get(i);
			final var ruleScanner = whileRule.rule.compileWhileAG(whileRule.stack.endRule, isFirstLine, anchorPosition == linePos);
			final var r = ruleScanner.scanner.findNextMatch(lineText, linePos);
			if (r != null) {
				final RuleId matchedRuleId = ruleScanner.rules[r.index];
				if (RuleId.WHILE_RULE.notEquals(matchedRuleId)) {
					stack = castNonNull(whileRule.stack.pop());
					break;
				}
				if (r.getCaptureIndices().length > 0) {
					lineTokens.produce(whileRule.stack, r.getCaptureIndices()[0].start);
					handleCaptures(grammar, lineText, isFirstLine, whileRule.stack, lineTokens, whileRule.rule.whileCaptures,
							r.getCaptureIndices());
					lineTokens.produce(whileRule.stack, r.getCaptureIndices()[0].end);
					anchorPosition = r.getCaptureIndices()[0].end;
					if (r.getCaptureIndices()[0].end > linePos) {
						linePos = r.getCaptureIndices()[0].end;
						isFirstLine = false;
					}
				}
			} else {
				stack = castNonNull(whileRule.stack.pop());
				break;
			}
		}
		return new WhileCheckResult(stack, linePos, anchorPosition, isFirstLine);
	}
	static TokenizeStringResult tokenizeString(final Grammar grammar, final OnigString lineText, final boolean isFirstLine,
			final int linePos, final StateStack stack, final LineTokens lineTokens, final boolean checkWhileConditions,
			final Duration timeLimit) {
		return new LineTokenizer(grammar, lineText, isFirstLine, linePos, stack, lineTokens)
				.scan(checkWhileConditions, timeLimit.toMillis());
	}
	static String debugCompiledRuleToString(final CompiledRule ruleScanner) {
		final var r = new ArrayList<String>(ruleScanner.rules.length);
		for (int i = 0, l = ruleScanner.rules.length; i < l; i++) {
			r.add("   - " + ruleScanner.rules[i] + ": " + ruleScanner.debugRegExps.get(i));
		}
		return String.join(System.lineSeparator(), r);
	}
}
