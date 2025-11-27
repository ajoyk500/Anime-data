
package org.eclipse.tm4e.languageconfiguration.internal.supports;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.languageconfiguration.internal.model.CharacterPair;
import org.eclipse.tm4e.languageconfiguration.internal.model.EnterAction;
import org.eclipse.tm4e.languageconfiguration.internal.model.EnterAction.IndentAction;
import org.eclipse.tm4e.languageconfiguration.internal.model.OnEnterRule;
import org.eclipse.tm4e.languageconfiguration.internal.utils.RegExpUtils;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OnEnterSupport {
    private static final List<CharacterPair> DEFAULT_BRACKETS = List.of(
            new CharacterPair("(", ")"), 
            new CharacterPair("{", "}"), 
            new CharacterPair("[", "]")); 
    private final List<ProcessedBracketPair> brackets;
    private final List<OnEnterRule> regExpRules;
    public OnEnterSupport(@Nullable final List<CharacterPair> brackets, @Nullable final List<OnEnterRule> regExpRules) {
        this.brackets = (brackets != null ? brackets : DEFAULT_BRACKETS)
                .stream()
                .filter(t -> t != null)
                .map(ProcessedBracketPair::new)
                .collect(Collectors.toList());
        this.regExpRules = regExpRules != null ? regExpRules : Collections.emptyList();
    }
    public @Nullable EnterAction onEnter(
            final String previousLineText,
            final String beforeEnterText,
            final String afterEnterText) {
        for (final OnEnterRule rule : regExpRules) {
            if (!rule.beforeText.matchesPartially(beforeEnterText))
                continue;
            final var afterTextPattern = rule.afterText;
            if (afterTextPattern != null && !afterTextPattern.matchesPartially(afterEnterText))
                continue;
            final var previousLinePattern = rule.previousLineText;
            if (previousLinePattern != null && !previousLinePattern.matchesPartially(previousLineText))
                continue;
            return rule.action;
        }
        if (!beforeEnterText.isEmpty() && !afterEnterText.isEmpty()) {
            for (final ProcessedBracketPair bracket : brackets) {
                if (bracket.matchOpen(beforeEnterText) && bracket.matchClose(afterEnterText)) {
                    return new EnterAction(IndentAction.IndentOutdent);
                }
            }
        }
        if (!beforeEnterText.isEmpty()) {
            for (final ProcessedBracketPair bracket : brackets) {
                if (bracket.matchOpen(beforeEnterText)) {
                    return new EnterAction(IndentAction.Indent);
                }
            }
        }
        return null;
    }
    private static final class ProcessedBracketPair {
        private static final Pattern B_REGEXP = Pattern.compile("\\B"); 
        private final @Nullable Pattern openRegExp;
        private final @Nullable Pattern closeRegExp;
        private ProcessedBracketPair(final CharacterPair charPair) {
            openRegExp = createOpenBracketRegExp(charPair.open);
            closeRegExp = createCloseBracketRegExp(charPair.close);
        }
        private boolean matchOpen(final String beforeEnterText) {
            return openRegExp != null && openRegExp.matcher(beforeEnterText).find();
        }
        private boolean matchClose(final String afterEnterText) {
            return closeRegExp != null && closeRegExp.matcher(afterEnterText).find();
        }
        private static @Nullable Pattern createOpenBracketRegExp(final String bracket) {
            final var str = new StringBuilder(RegExpUtils.escapeRegExpCharacters(bracket));
            final var c = String.valueOf(str.charAt(0));
            if (!B_REGEXP.matcher(c).find()) {
                str.insert(0, "\\b"); 
            }
            str.append("\\s*$"); 
            return RegExpUtils.create(str.toString());
        }
        private static @Nullable Pattern createCloseBracketRegExp(final String bracket) {
            final var str = new StringBuilder(RegExpUtils.escapeRegExpCharacters(bracket));
            final var c = String.valueOf(str.charAt(str.length() - 1));
            if (!B_REGEXP.matcher(c).find()) {
                str.append("\\b"); 
            }
            str.insert(0, "^\\s*"); 
            return RegExpUtils.create(str.toString());
        }
    }
}
