
package io.github.rosemoe.sora.text;

import androidx.annotation.NonNull;
import io.github.rosemoe.sora.util.IntPair;

public final class Cursor {
    public final static int DIRECTION_NONE = 0;
    public final static int DIRECTION_LTR = 1;
    public final static int DIRECTION_RTL = 2;
    private final Content content;
    private final CachedIndexer indexer;
    private CharPosition leftSel, rightSel;
    private CharPosition cache0, cache1, cache2;
    private int selDirection = DIRECTION_NONE;
    public Cursor(@NonNull Content content) {
        this.content = content;
        indexer = new CachedIndexer(content);
        leftSel = new CharPosition().toBOF();
        rightSel = new CharPosition().toBOF();
    }
    private static boolean isWhitespace(char c) {
        return (c == '\t' || c == ' ');
    }
    public void set(int line, int column) {
        setLeft(line, column);
        setRight(line, column);
    }
    public void setLeft(int line, int column) {
        leftSel = indexer.getCharPosition(line, column).fromThis();
    }
    public void setRight(int line, int column) {
        rightSel = indexer.getCharPosition(line, column).fromThis();
    }
    public int getLeftLine() {
        return leftSel.getLine();
    }
    public int getLeftColumn() {
        return leftSel.getColumn();
    }
    public int getRightLine() {
        return rightSel.getLine();
    }
    public int getRightColumn() {
        return rightSel.getColumn();
    }
    public boolean isInSelectedRegion(int line, int column) {
        if (line >= getLeftLine() && line <= getRightLine()) {
            boolean yes = true;
            if (line == getLeftLine()) {
                yes = column >= getLeftColumn();
            }
            if (line == getRightLine()) {
                yes = yes && column < getRightColumn();
            }
            return yes;
        }
        return false;
    }
    public int getLeft() {
        return leftSel.index;
    }
    public int getRight() {
        return rightSel.index;
    }
    public void updateCache(int line) {
        indexer.getCharIndex(line, 0);
    }
    public CachedIndexer getIndexer() {
        return indexer;
    }
    public boolean isSelected() {
        return leftSel.index != rightSel.index;
    }
    public void setSelectionDirection(int selDirection) {
        this.selDirection = selDirection;
    }
    public int getSelectionDirection() {
        return selDirection;
    }
    public long getLeftOf(long position) {
        int line = IntPair.getFirst(position);
        int column = IntPair.getSecond(position);
        int n_column = TextLayoutHelper.get().getCurPosLeft(column, content.getLine(line));
        if (n_column == column && column == 0) {
            if (line == 0) {
                return 0;
            } else {
                int c_column = content.getColumnCount(line - 1);
                return IntPair.pack(line - 1, c_column);
            }
        } else {
            return IntPair.pack(line, n_column);
        }
    }
    public long getRightOf(long position) {
        int line = IntPair.getFirst(position);
        int column = IntPair.getSecond(position);
        int c_column = content.getColumnCount(line);
        int n_column = TextLayoutHelper.get().getCurPosRight(column, content.getLine(line));
        if (n_column == c_column && column == n_column) {
            if (line + 1 == content.getLineCount()) {
                return IntPair.pack(line, c_column);
            } else {
                return IntPair.pack(line + 1, 0);
            }
        } else {
            return IntPair.pack(line, n_column);
        }
    }
    @NonNull
    public CharPosition left() {
        return leftSel.fromThis();
    }
    @NonNull
    public CharPosition right() {
        return rightSel.fromThis();
    }
    public TextRange getRange() {
        return new TextRange(left(), right());
    }
    void beforeInsert(int startLine, int startColumn) {
        cache0 = indexer.getCharPosition(startLine, startColumn).fromThis();
    }
    void beforeDelete(int startLine, int startColumn, int endLine, int endColumn) {
        cache1 = indexer.getCharPosition(startLine, startColumn).fromThis();
        cache2 = indexer.getCharPosition(endLine, endColumn).fromThis();
    }
    void beforeReplace() {
        indexer.beforeReplace(content);
    }
    void afterInsert(int startLine, int startColumn, int endLine, int endColumn,
                     CharSequence insertedContent) {
        indexer.afterInsert(content, startLine, startColumn, endLine, endColumn, insertedContent);
        int beginIdx = cache0.getIndex();
        if (getLeft() >= beginIdx) {
            leftSel = indexer.getCharPosition(getLeft() + insertedContent.length()).fromThis();
        }
        if (getRight() >= beginIdx) {
            rightSel = indexer.getCharPosition(getRight() + insertedContent.length()).fromThis();
        }
    }
    void afterDelete(int startLine, int startColumn, int endLine, int endColumn,
                     CharSequence deletedContent) {
        indexer.afterDelete(content, startLine, startColumn, endLine, endColumn, deletedContent);
        int beginIdx = cache1.getIndex();
        int endIdx = cache2.getIndex();
        int left = getLeft();
        int right = getRight();
        if (beginIdx > right) {
            return;
        }
        left = left - Math.max(0, Math.min(left - beginIdx, endIdx - beginIdx));
        right = right - Math.max(0, Math.min(right - beginIdx, endIdx - beginIdx));
        leftSel = indexer.getCharPosition(left).fromThis();
        rightSel = indexer.getCharPosition(right).fromThis();
    }
}
