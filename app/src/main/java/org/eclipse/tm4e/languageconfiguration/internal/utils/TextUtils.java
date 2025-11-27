
package org.eclipse.tm4e.languageconfiguration.internal.utils;

import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.util.IntPair;

public final class TextUtils {
    public static String normalizeIndentation(final String text, final int tabSize, final boolean insertSpaces) {
        int firstNonWhitespaceIndex = firstNonWhitespaceIndex(text);
        if (firstNonWhitespaceIndex == -1) {
            firstNonWhitespaceIndex = text.length();
        }
        return normalizeIndentationFromWhitespace(text.substring(0, firstNonWhitespaceIndex), tabSize, insertSpaces)
                + text.substring(firstNonWhitespaceIndex);
    }
    private static String normalizeIndentationFromWhitespace(final String text, final int tabSize, final boolean insertSpaces) {
        int spacesCnt = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\t') {
                spacesCnt += tabSize;
            } else {
                spacesCnt++;
            }
        }
        final var result = new StringBuilder();
        if (!insertSpaces) {
            final long tabsCnt = spacesCnt / tabSize;
            spacesCnt = spacesCnt % tabSize;
            for (int i = 0; i < tabsCnt; i++) {
                result.append('\t');
            }
        }
        for (int i = 0; i < spacesCnt; i++) {
            result.append(' ');
        }
        return result.toString();
    }
    public static int startIndexOfOffsetTouchingString(final String text, final int offset, final String string) {
        int start = offset - string.length();
        start = start < 0 ? 0 : start;
        int end = offset + string.length();
        end = end >= text.length() ? text.length() : end;
        try {
            final int indexInSubtext = text.substring(start, end).indexOf(string);
            return indexInSubtext == -1 ? -1 : start + indexInSubtext;
        } catch (final IndexOutOfBoundsException e) {
            return -1;
        }
    }
    private static int firstNonWhitespaceIndex(final String text) {
        for (int i = 0, len = text.length(); i < len; i++) {
            final char c = text.charAt(i);
            if (c != ' ' && c != '\t') {
                return i;
            }
        }
        return -1;
    }
    public static String getIndentationFromWhitespace(final String whitespace, final int tabSize, final boolean insertSpaces) {
        final var tab = "\t"; 
        int indentOffset = 0;
        boolean startsWithTab = true;
        boolean startsWithSpaces = true;
        final String spaces = insertSpaces
                ? " ".repeat(tabSize)
                : "";
        while (startsWithTab || startsWithSpaces) {
            startsWithTab = whitespace.startsWith(tab, indentOffset);
            startsWithSpaces = insertSpaces && whitespace.startsWith(spaces, indentOffset);
            if (startsWithTab) {
                indentOffset += tab.length();
            }
            if (startsWithSpaces) {
                indentOffset += spaces.length();
            }
        }
        return whitespace.substring(0, indentOffset);
    }
    public static String getIndentationAtPosition(final Content doc, final int offset) {
        try {
            final int lineStartOffset = doc.getIndexer().getCharPosition(offset).index;
            final int indentationEndOffset = findEndOfWhiteSpace(doc, lineStartOffset, offset);
            return doc.substring(lineStartOffset, indentationEndOffset - lineStartOffset);
        } catch (final Exception excp) {
        }
        return ""; 
    }
    private static int findEndOfWhiteSpace(final Content doc, int startAt, final int endAt) {
        while (startAt < endAt) {
            final char c = doc.charAt(startAt);
            if (c != ' ' && c != '\t') {
                return startAt;
            }
            startAt++;
        }
        return endAt;
    }
    public static boolean isBlankLine(final Content doc, final int line) {
        try {
            int offset = doc.charAt(line, 0);
            final int lineEnd = offset + doc.getLine(line).length();
            while (offset < lineEnd) {
                if (!Character.isWhitespace(doc.charAt(offset))) {
                    return false;
                }
                offset++;
            }
        } catch (final Exception e) {
        }
        return true;
    }
    public static String getLeadingWhitespace(String str, int start, int end) {
        for (var i = start; i < end; i++) {
            var chCode = str.charAt(i);
            if (chCode != 32  && chCode != 9) {
                return str.substring(start, i);
            }
        }
        return str.substring(start, end);
    }
    public static String getLinePrefixingWhitespaceAtPosition(final Content d, final CharPosition position) {
        var line = d.getLine(position.line);
        var startIndex = IntPair.getFirst(io.github.rosemoe.sora.text.TextUtils.findLeadingAndTrailingWhitespacePos(
                line
        ));
        return line.subSequence(0, startIndex).toString();
    }
    public static String getLeadingWhitespace(String str) {
        return getLeadingWhitespace(str, 0, str.length());
    }
    private TextUtils() {
    }
}
