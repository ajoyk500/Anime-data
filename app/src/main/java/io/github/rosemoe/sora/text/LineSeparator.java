
package io.github.rosemoe.sora.text;

import androidx.annotation.NonNull;
import java.util.Objects;

public enum LineSeparator {
    NONE(""),
    LF("\n"),
    CR("\r"),
    CRLF("\r\n");
    private final String str;
    private final int length;
    private final char[] chars;
    LineSeparator(String str) {
        this.str = str;
        this.length = str.length();
        chars = str.toCharArray();
    }
    public String getContent() {
        return str;
    }
    public int getLength() {
        return length;
    }
    public char[] getChars() {
        return chars;
    }
    public static LineSeparator fromSeparatorString(String str) {
        Objects.requireNonNull(str, "text must not be null");
        switch (str) {
            case "\r":
                return CR;
            case "\n":
                return LF;
            case "\r\n":
                return CRLF;
            case "":
                return NONE;
            default:
                throw new IllegalArgumentException("unknown line separator type");
        }
    }
    public static LineSeparator fromSeparatorString(@NonNull CharSequence text, int start, int end) {
        Objects.requireNonNull(text, "text must not be null");
        if (end == start) {
            return NONE;
        }
        if (end - start == 1) {
            var ch = text.charAt(start);
            if (ch == '\r') return CR;
            if (ch == '\n') return LF;
        }
        if (end - start == 2 && text.charAt(start) == '\r' && text.charAt(start + 1) == '\n') {
            return CRLF;
        }
        throw new IllegalArgumentException("unknown line separator type");
    }
}
