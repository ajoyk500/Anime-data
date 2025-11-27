
package io.github.rosemoe.sora.lang.util;

import io.github.rosemoe.sora.lang.styling.EmptyReader;
import io.github.rosemoe.sora.lang.styling.Spans;
import io.github.rosemoe.sora.text.CharPosition;

public class PlainTextSpans implements Spans {
    private int lineCount;
    public PlainTextSpans(int lineCount) {
        this.lineCount = lineCount;
    }
    @Override
    public Spans copy() {
        return new PlainTextSpans(lineCount);
    }
    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }
    @Override
    public void adjustOnInsert(CharPosition start, CharPosition end) {
        lineCount += end.line - start.line;
    }
    @Override
    public void adjustOnDelete(CharPosition start, CharPosition end) {
        lineCount -= end.line - start.line;
    }
    @Override
    public Reader read() {
        return EmptyReader.getInstance();
    }
    @Override
    public boolean supportsModify() {
        return false;
    }
    @Override
    public Modifier modify() {
        throw new UnsupportedOperationException();
    }
    @Override
    public int getLineCount() {
        return lineCount;
    }
}
