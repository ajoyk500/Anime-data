
package io.github.rosemoe.sora.langs.textmate.folding;

import android.util.SparseIntArray;
import io.github.rosemoe.sora.text.Content;

public class RangesCollector {
    private final SparseIntArray _startIndexes;
    private final SparseIntArray _endIndexes;
    private int _length;
    public RangesCollector() {
        this._startIndexes = new SparseIntArray();
        this._endIndexes = new SparseIntArray();
        this._length = 0;
    }
    public void insertFirst(int startLineNumber, int endLineNumber, int indent) {
        if (startLineNumber > IndentRange.MAX_LINE_NUMBER || endLineNumber > IndentRange.MAX_LINE_NUMBER) {
            return;
        }
        int index = this._length;
        this._startIndexes.put(index, startLineNumber);
        this._endIndexes.put(index, endLineNumber);
        this._length++;
    }
    public FoldingRegions toIndentRanges(Content model) throws Exception {
        return new FoldingRegions(_startIndexes, _endIndexes);
    }
}
