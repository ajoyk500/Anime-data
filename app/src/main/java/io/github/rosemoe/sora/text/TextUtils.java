
package io.github.rosemoe.sora.text;

import androidx.annotation.NonNull;
import java.util.Objects;
import io.github.rosemoe.sora.util.IntPair;

public class TextUtils {
    public static long countLeadingSpacesAndTabs(@NonNull CharSequence text) {
        Objects.requireNonNull(text);
        int p = 0, spaces = 0, tabs = 0;
        char c;
        while (p < text.length() && isWhitespace((c = text.charAt(p)))) {
            if (c == '\t') {
                tabs += 1;
            } else {
                spaces += 1;
            }
            ++p;
        }
        return IntPair.pack(spaces, tabs);
    }
    public static int countLeadingSpaceCount(@NonNull CharSequence text, int tabWidth) {
        final var result = countLeadingSpacesAndTabs(text);
        return IntPair.getFirst(result) + (tabWidth * IntPair.getSecond(result));
    }
    public static String createIndent(int indentSize, int tabWidth, boolean useTab) {
        indentSize = Math.max(0, indentSize);
        int tab = 0;
        int space;
        if (useTab) {
            tab = indentSize / tabWidth;
            space = indentSize % tabWidth;
        } else {
            space = indentSize;
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < tab; i++) {
            s.append('\t');
        }
        for (int i = 0; i < space; i++) {
            s.append(' ');
        }
        return s.toString();
    }
    public static int indexOf(@NonNull CharSequence text, @NonNull CharSequence pattern, boolean ignoreCase, int fromIndex) {
        var max = text.length() - pattern.length();
        var len = pattern.length();
        label:
        for (int i = fromIndex; i <= max; i++) {
            for (int j = 0; j < len; j++) {
                char s = text.charAt(i + j);
                char p = pattern.charAt(j);
                if (!(s == p || (ignoreCase && Character.toLowerCase(s) == Character.toLowerCase(p)))) {
                    continue label;
                }
            }
            return i;
        }
        return -1;
    }
    public static int lastIndexOf(@NonNull CharSequence text, @NonNull CharSequence pattern, boolean ignoreCase, int fromIndex) {
        var len = pattern.length();
        fromIndex = Math.min(fromIndex, text.length() - len);
        label:
        for (int i = fromIndex; i >= 0; i--) {
            for (int j = 0; j < len; j++) {
                char s = text.charAt(i + j);
                char p = pattern.charAt(j);
                if (!(s == p || (ignoreCase && Character.toLowerCase(s) == Character.toLowerCase(p)))) {
                    continue label;
                }
            }
            return i;
        }
        return -1;
    }
    public static boolean startsWith(@NonNull CharSequence text, @NonNull CharSequence pattern, boolean ignoreCase) {
        if (text.length() < pattern.length()) {
            return false;
        }
        var len = pattern.length();
        for (int i = 0; i < len; i++) {
            char s = text.charAt(i);
            char p = pattern.charAt(i);
            if (!((s == p) || (ignoreCase && Character.toLowerCase(s) == Character.toLowerCase(p)))) {
                return false;
            }
        }
        return true;
    }
    private static boolean isWhitespace(char ch) {
        return ch == '\t' || ch == ' ';
    }
    public static String padStart(String src, char padChar, int length) {
        if (src.length() >= length) {
            return src;
        }
        var sb = new StringBuilder(length);
        for (int i = 0; i < length - src.length(); i++) {
            sb.append(padChar);
        }
        sb.append(src);
        return sb.toString();
    }
    public static long findLeadingAndTrailingWhitespacePos(ContentLine line) {
        var buffer = line.getBackingCharArray();
        int column = line.length();
        int leading = 0;
        int trailing = column;
        while (leading < column && isWhitespace(buffer[leading])) {
            leading++;
        }
        if (leading != column) {
            while (trailing > 0 && isWhitespace(buffer[trailing - 1])) {
                trailing--;
            }
        }
        return IntPair.pack(leading, trailing);
    }
}
