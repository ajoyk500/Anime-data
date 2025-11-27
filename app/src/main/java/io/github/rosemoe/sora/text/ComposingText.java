
package io.github.rosemoe.sora.text;

import io.github.rosemoe.sora.annotations.UnsupportedUserUsage;

@UnsupportedUserUsage
public class ComposingText {
    public int startIndex, endIndex;
    public boolean preSetComposing;
    public void set(int start, int end) {
        this.startIndex = start;
        this.endIndex = end;
    }
    public void adjustLength(int length) {
        this.endIndex = startIndex + length;
    }
    public void reset() {
        this.startIndex = this.endIndex = -1;
    }
    public boolean isComposing() {
        var r = preSetComposing || startIndex >= 0 && endIndex >= 0;
        preSetComposing = false;
        return r;
    }
    public void shiftOnInsert(int insertStart, int insertEnd) {
        var length = insertEnd - insertStart;
        if (startIndex <= insertStart && endIndex >= insertStart) {
            endIndex += length;
        }
        if (startIndex > insertStart) {
            startIndex += length;
            endIndex += length;
        }
    }
    public void shiftOnDelete(int deleteStart, int deleteEnd) {
        var length = deleteEnd - deleteStart;
        var sharedStart = Math.max(deleteStart, startIndex);
        var sharedEnd = Math.min(deleteEnd, endIndex);
        if (sharedEnd <= sharedStart) {
            if (startIndex >= deleteEnd) {
                startIndex -= length;
                endIndex -= length;
            }
        } else {
            var sharedLength = sharedEnd - sharedStart;
            endIndex -= sharedLength;
            if (startIndex > deleteStart) {
                var shiftLeftCount = startIndex - deleteStart;
                startIndex -= shiftLeftCount;
                endIndex -= shiftLeftCount;
            }
        }
    }
}
