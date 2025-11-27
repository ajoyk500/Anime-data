
package io.github.rosemoe.sora.text;

import androidx.annotation.NonNull;

public class LineNumberCalculator {
    private final CharSequence target;
    private final int length;
    private int offset;
    private int line;
    private int column;
    public LineNumberCalculator(@NonNull CharSequence target) {
        this.target = target;
        offset = line = column = 0;
        length = this.target.length();
    }
    public void update(int length) {
        for (int i = 0; i < length; i++) {
            if (offset + i == this.length) {
                break;
            }
            if (target.charAt(offset + i) == '\n') {
                line++;
                column = 0;
            } else {
                column++;
            }
        }
        offset = offset + length;
    }
    public int findLineStart() {
        return offset - column;
    }
    public int findLineEnd() {
        int i = 0;
        for (; i + offset < length; i++) {
            if (target.charAt(offset + i) == '\n') {
                break;
            }
        }
        return offset + i;
    }
    public int getLine() {
        return line;
    }
    public int getColumn() {
        return column;
    }
}
