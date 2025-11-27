
package io.github.rosemoe.sora.langs.textmate;

import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.eclipse.tm4e.languageconfiguration.internal.model.CompleteEnterAction;
import org.eclipse.tm4e.languageconfiguration.internal.model.EnterAction;
import org.eclipse.tm4e.languageconfiguration.internal.model.LanguageConfiguration;
import org.eclipse.tm4e.languageconfiguration.internal.supports.IndentRulesSupport;
import org.eclipse.tm4e.languageconfiguration.internal.supports.OnEnterSupport;
import org.eclipse.tm4e.languageconfiguration.internal.utils.TextUtils;
import java.util.Arrays;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandleResult;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;

public class TextMateNewlineHandler implements NewlineHandler {
    private OnEnterSupport enterSupport = null;
    private IndentRulesSupport indentRulesSupport = null;
    private final TextMateLanguage language;
    private CompleteEnterAction enterAction;
    private Pair<String, String> indentForEnter;
    private boolean isEnabled = true;
    private LanguageConfiguration languageConfiguration;
    public TextMateNewlineHandler(TextMateLanguage language) {
        this.language = language;
        var languageConfiguration = language.languageConfiguration;
        this.languageConfiguration = languageConfiguration;
        if (languageConfiguration == null) {
            return;
        }
        var enterRules = languageConfiguration.getOnEnterRules();
        var brackets = languageConfiguration.getBrackets();
        var indentationsRules = languageConfiguration.getIndentationRules();
        if (enterRules != null) {
            enterSupport = new OnEnterSupport(brackets, enterRules);
        }
        if (indentationsRules != null) {
            indentRulesSupport = new IndentRulesSupport(indentationsRules);
        }
    }
    @Override
    public boolean matchesRequirement(@NonNull Content text, @NonNull CharPosition position, @Nullable Styles style) {
        if (!isEnabled) {
            return false;
        }
        enterAction = getEnterAction(text, position);
        indentForEnter = null;
        if (enterAction == null) {
            indentForEnter = getIndentForEnter(text, position);
        }
        return enterAction != null || indentForEnter != null;
    }
    @NonNull
    @Override
    public NewlineHandleResult handleNewline(@NonNull Content text, @NonNull CharPosition position, @Nullable Styles style, int tabSize) {
        var delim = "\n"; 
        if (indentForEnter != null) {
            var normalIndent = normalizeIndentation(indentForEnter.second);
            var typeText = delim + normalIndent;
            return new NewlineHandleResult(typeText, 0);
        }
        switch (enterAction.indentAction) {
            case None:
            case Indent: {
                final var increasedIndent = normalizeIndentation(enterAction.indentation + enterAction.appendText);
                final var typeText = delim + increasedIndent;
                return new NewlineHandleResult(typeText, 0);
            }
            case IndentOutdent: {
                final var normalIndent = normalizeIndentation(enterAction.indentation);
                final var increasedIndent = normalizeIndentation(enterAction.indentation + enterAction.appendText);
                final var typeText = delim + increasedIndent + delim + normalIndent;
                var caretOffset = normalIndent.length() + 1;
                return new NewlineHandleResult(typeText, caretOffset);
            }
            case Outdent:
                final var indentation = TextUtils.getIndentationFromWhitespace(enterAction.indentation, language.getTabSize(), language.useTab());
                final var outdentedText = outdentString(normalizeIndentation(indentation + enterAction.appendText));
                var caretOffset = outdentedText.length() + 1;
                return new NewlineHandleResult(outdentedText, caretOffset);
        }
        return new NewlineHandleResult("", 0);
    }
    protected Pair<String, String> getIndentForEnter(Content text, CharPosition position) {
        var currentLineText = text.getLineString(position.line);
        var beforeEnterText = currentLineText.substring(0, position.column);
        var afterEnterText = currentLineText.substring(position.column);
        if (indentRulesSupport == null) {
            return null;
        }
        var beforeEnterIndent = TextUtils.getLeadingWhitespace(beforeEnterText, 0, beforeEnterText.length());
        var afterEnterAction = getInheritIndentForLine(new WrapperContentImp(text, position.line, beforeEnterText), true, position.line + 1);
        if (afterEnterAction == null) {
            return new Pair<>(beforeEnterIndent, beforeEnterIndent);
        }
        var afterEnterIndent = afterEnterAction.indentation;
        var indent = "";
        if (language.useTab()) {
            indent = "\t";
        } else {
            indent = " ".repeat(language.getTabSize());
        }
        if (afterEnterAction.action == EnterAction.IndentAction.Indent) {
            afterEnterIndent = beforeEnterIndent + indent;
        }
        if (indentRulesSupport.shouldDecrease(afterEnterText)) {
            afterEnterIndent = beforeEnterIndent.substring(0, Math.max(Math.max(0, beforeEnterIndent.length() - 1), beforeEnterIndent.length() - indent.length() ));
        }
        return new Pair<>(beforeEnterIndent, afterEnterIndent);
    }
    @Nullable
    private InheritIndentResult getInheritIndentForLine(WrapperContent wrapperContent,
                                                        boolean honorIntentialIndent, int line) {
        if (line <= 0) {
            return new InheritIndentResult("", null);
        }
        var precedingUnIgnoredLine = getPrecedingValidLine(wrapperContent, line);
        if (precedingUnIgnoredLine <= -1) {
            return null;
        } 
        var precedingUnIgnoredLineContent = wrapperContent.getLineContent(precedingUnIgnoredLine);
        if (indentRulesSupport.shouldIncrease(precedingUnIgnoredLineContent) || indentRulesSupport.shouldIndentNextLine(precedingUnIgnoredLineContent)) {
            return new InheritIndentResult(TextUtils.getLeadingWhitespace(precedingUnIgnoredLineContent, 0, precedingUnIgnoredLineContent.length()), EnterAction.IndentAction.Indent, precedingUnIgnoredLine);
        } else if (indentRulesSupport.shouldDecrease(precedingUnIgnoredLineContent)) {
            return new InheritIndentResult(TextUtils.getLeadingWhitespace(precedingUnIgnoredLineContent, 0, precedingUnIgnoredLineContent.length()), null, precedingUnIgnoredLine);
        } else {
            if (precedingUnIgnoredLine == 0) {
                return new InheritIndentResult(TextUtils.getLeadingWhitespace(wrapperContent.getLineContent(precedingUnIgnoredLine)), null, precedingUnIgnoredLine);
            }
            var previousLine = precedingUnIgnoredLine - 1;
            var previousLineIndentMetadata = indentRulesSupport.getIndentMetadata(wrapperContent.getLineContent(previousLine));
            if (((previousLineIndentMetadata & (IndentRulesSupport.IndentConsts.INCREASE_MASK | IndentRulesSupport.IndentConsts.DECREASE_MASK)) == 0) && (previousLineIndentMetadata & IndentRulesSupport.IndentConsts.INDENT_NEXTLINE_MASK) == 0 && previousLineIndentMetadata > 0) {
                var stopLine = 0;
                for (var i = previousLine - 1; i > 0; i--) {
                    if (indentRulesSupport.shouldIndentNextLine(wrapperContent.getLineContent((i)))) {
                        continue;
                    }
                    stopLine = i;
                    break;
                }
                return new InheritIndentResult(TextUtils.getLeadingWhitespace(wrapperContent.getLineContent(stopLine + 1)), null, stopLine + 1);
            }
            if (honorIntentialIndent) {
                return new InheritIndentResult(
                        TextUtils.getLeadingWhitespace(wrapperContent.getLineContent(precedingUnIgnoredLine)), null, precedingUnIgnoredLine);
            }
            for (var i = precedingUnIgnoredLine; i > 0; i--) {
                var lineContent = wrapperContent.getLineContent(i);
                if (indentRulesSupport.shouldIncrease(lineContent)) {
                    return new InheritIndentResult(TextUtils.getLeadingWhitespace(lineContent), EnterAction.IndentAction.Indent, i);
                } else if (indentRulesSupport.shouldIndentNextLine(lineContent)) {
                    var stopLine = 0;
                    for (var j = i - 1; j > 0; j--) {
                        if (indentRulesSupport.shouldIndentNextLine(wrapperContent.getLineContent(i))) {
                            continue;
                        }
                        stopLine = j;
                        break;
                    }
                    return new InheritIndentResult(TextUtils.getLeadingWhitespace(wrapperContent.getLineContent(stopLine + 1)), null, stopLine + 1);
                } else if (indentRulesSupport.shouldDecrease(lineContent)) {
                    return new InheritIndentResult(TextUtils.getLeadingWhitespace(lineContent), null, i);
                }
            }
            return new InheritIndentResult(TextUtils.getLeadingWhitespace(wrapperContent.getLineContent(1)), null, 1);
        }
    }
    public int getPrecedingValidLine(WrapperContent content, int lineNumber) {
        if (lineNumber > 0) {
            int lastLineNumber;
            for (lastLineNumber = lineNumber - 1; lastLineNumber >= 0; lastLineNumber--) {
                var text = content.getLineContent(lastLineNumber);
                if (indentRulesSupport.shouldIgnore(text)  || text.isEmpty()) {
                    continue;
                }
                return lastLineNumber;
            }
        }
        return -1;
    }
    @Nullable
    public CompleteEnterAction getEnterAction(final Content content, final CharPosition position) {
        String indentation = TextUtils.getLinePrefixingWhitespaceAtPosition(content, position);
        final var onEnterSupport = this.enterSupport;
        if (onEnterSupport == null) {
            return null;
        }
        var scopedLineText = content.getLineString(position.line);
        var beforeEnterText = scopedLineText.substring(0, position.column   );
        var afterEnterText = scopedLineText.substring(position.column );
        var previousLineText = "";
        if (position.line > 1 ) {
            previousLineText = content.getLineString(position.line - 1);
        }
        EnterAction enterResult = null;
        try {
            enterResult = onEnterSupport.onEnter(previousLineText, beforeEnterText, afterEnterText);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        if (enterResult == null) {
            return null;
        }
        final EnterAction.IndentAction indentAction = enterResult.indentAction;
        String appendText = enterResult.appendText;
        final Integer removeText = enterResult.removeText;
        if (appendText == null) {
            if (indentAction == EnterAction.IndentAction.Indent
                    || indentAction == EnterAction.IndentAction.IndentOutdent) {
                appendText = "\t";
            } else {
                appendText = "";
            }
        } else if (indentAction == EnterAction.IndentAction.Indent) {
            appendText = "\t" + appendText;
        }
        if (removeText != null) {
            indentation = indentation.substring(0, indentation.length() - removeText);
        }
        return new CompleteEnterAction(indentAction, appendText, removeText, indentation);
    }
    private String outdentString(final String str) {
        if (str.startsWith("\t")) { 
            return str.substring(1);
        }
        if (language.useTab()) {
            final char[] chars = new char[language.getTabSize()];
            Arrays.fill(chars, ' ');
            final String spaces = new String(chars);
            if (str.startsWith(spaces)) {
                return str.substring(spaces.length());
            }
        }
        return str;
    }
    private String normalizeIndentation(final String str) {
        return TextUtils.normalizeIndentation(str, language.getTabSize(), !language.useTab());
    }
    public boolean isEnabled() {
        return isEnabled;
    }
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
    static class InheritIndentResult {
        String indentation;
        EnterAction.IndentAction action;
        int line;
        public InheritIndentResult(String indentation, EnterAction.IndentAction action, int line) {
            this.indentation = indentation;
            this.action = action;
            this.line = line;
        }
        public InheritIndentResult(String indentation, EnterAction.IndentAction action) {
            this.indentation = indentation;
            this.action = action;
        }
    }
    private static class WrapperContentImp implements WrapperContent {
        private final Content content;
        private final int line;
        private final String currentLineContent;
        protected WrapperContentImp(Content content, int line, String currentLineContent) {
            this.content = content;
            this.line = line;
            this.currentLineContent = currentLineContent;
        }
        @Override
        public Content getOrigin() {
            return content;
        }
        @Override
        public String getLineContent(int line) {
            if (line == this.line) {
                return currentLineContent;
            } else {
                return content.getLineString(line);
            }
        }
    }
    private interface WrapperContent {
        Content getOrigin();
        String getLineContent(int line);
    }
}
