
package io.github.rosemoe.sora.lang.smartEnter;


public class NewlineHandleResult {
    public final CharSequence text;
    public final int shiftLeft;
    public NewlineHandleResult(CharSequence text, int shiftLeft) {
        this.text = text;
        this.shiftLeft = shiftLeft;
        if (shiftLeft < 0 || shiftLeft > text.length()) {
            throw new IllegalArgumentException("invalid shiftLeft");
        }
    }
}
