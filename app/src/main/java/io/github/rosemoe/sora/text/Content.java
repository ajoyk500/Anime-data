
package io.github.rosemoe.sora.text;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import io.github.rosemoe.sora.text.bidi.ContentBidi;
import io.github.rosemoe.sora.text.bidi.Directions;

public class Content implements CharSequence {
    public final static int DEFAULT_MAX_UNDO_STACK_SIZE = 500;
    public final static int DEFAULT_LIST_CAPACITY = 1000;
    private static int sInitialListCapacity;
    static {
        setInitialLineCapacity(DEFAULT_LIST_CAPACITY);
    }
    private final List<ContentLine> lines;
    private final List<ContentListener> contentListeners;
    private final ReadWriteLock lock;
    private int textLength;
    private int nestedBatchEdit;
    private final AtomicLong documentVersion = new AtomicLong(1L);
    private final Indexer indexer;
    private final ContentBidi bidi;
    private UndoManager undoManager;
    private Cursor cursor;
    public Content() {
        this(null);
    }
    public Content(CharSequence src) {
        this(src, true);
    }
    public Content(CharSequence src, boolean threadSafe) {
        if (src == null) {
            src = "";
        }
        if (threadSafe) {
            lock = new ReentrantReadWriteLock();
        } else {
            lock = null;
        }
        textLength = 0;
        nestedBatchEdit = 0;
        lines = new ArrayList<>(getInitialLineCapacity());
        lines.add(new ContentLine());
        contentListeners = new ArrayList<>();
        bidi = new ContentBidi(this);
        undoManager = new UndoManager();
        setMaxUndoStackSize(Content.DEFAULT_MAX_UNDO_STACK_SIZE);
        indexer = new CachedIndexer(this);
        if (src.length() == 0) {
            setUndoEnabled(true);
            return;
        }
        setUndoEnabled(false);
        insert(0, 0, src);
        setUndoEnabled(true);
    }
    public static int getInitialLineCapacity() {
        return Content.sInitialListCapacity;
    }
    public static void setInitialLineCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity can not be negative or zero");
        }
        sInitialListCapacity = capacity;
    }
    private static boolean textEquals(@NonNull ContentLine a, @NonNull ContentLine b) {
        if (a.length() != b.length()) {
            return false;
        }
        if (a == b) {
            return true;
        }
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    public boolean isThreadSafe() {
        return lock != null;
    }
    protected void lock(boolean write) {
        if (lock == null) {
            return;
        }
        (write ? lock.writeLock() : lock.readLock()).lock();
    }
    protected void unlock(boolean write) {
        if (lock == null) {
            return;
        }
        (write ? lock.writeLock() : lock.readLock()).unlock();
    }
    @Override
    public char charAt(int index) {
        checkIndex(index);
        lock(false);
        try {
            var p = getIndexer().getCharPosition(index);
            return lines.get(p.line).charAt(p.column);
        } finally {
            unlock(false);
        }
    }
    public char charAt(int line, int column) {
        lock(false);
        try {
            checkLineAndColumn(line, column);
            return lines.get(line).charAt(column);
        } finally {
            unlock(false);
        }
    }
    @Override
    public int length() {
        return textLength;
    }
    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        if (start > end) {
            throw new StringIndexOutOfBoundsException("start > end");
        }
        lock(false);
        try {
            var s = getIndexer().getCharPosition(start);
            var e = getIndexer().getCharPosition(end);
            return subContentInternal(s.getLine(), s.getColumn(), e.getLine(), e.getColumn());
        } finally {
            unlock(false);
        }
    }
    public String substring(int start, int end) {
        if (start > end) {
            throw new StringIndexOutOfBoundsException("start > end");
        }
        lock(false);
        try {
            var s = getIndexer().getCharPosition(start);
            var e = getIndexer().getCharPosition(end);
            return subStringBuilder(s.getLine(), s.getColumn(), e.getLine(), e.getColumn(), end - start + 1).toString();
        } finally {
            unlock(false);
        }
    }
    public ContentLine getLine(int line) {
        lock(false);
        try {
            return lines.get(line);
        } finally {
            unlock(false);
        }
    }
    public int getLineCount() {
        return lines.size();
    }
    public int getColumnCount(int line) {
        return getLine(line).length();
    }
    public String getLineString(int line) {
        lock(false);
        try {
            checkLine(line);
            return lines.get(line).toString();
        } finally {
            unlock(false);
        }
    }
    public void getRegionOnLine(int line, int start, int end, char[] dest, int offset) {
        lock(false);
        try {
            lines.get(line).getChars(start, end, dest, offset);
        } finally {
            unlock(false);
        }
    }
    public void getLineChars(int line, char[] dest) {
        getRegionOnLine(line, 0, getColumnCount(line), dest, 0);
    }
    public int getCharIndex(int line, int column) {
        lock(false);
        try {
            return getIndexer().getCharIndex(line, column);
        } finally {
            unlock(false);
        }
    }
    public boolean isValidPosition(@Nullable CharPosition position) {
        if (position == null) {
            return false;
        }
        int line = position.line, column = position.column, index = position.index;
        lock(false);
        try {
            if (line < 0 || line >= getLineCount()) {
                return false;
            }
            ContentLine text = getLine(line);
            if (column > text.length() + text.getLineSeparator().getLength() || column < 0) {
                return false;
            }
            return getIndexer().getCharIndex(line, column) == index;
        } finally {
            unlock(false);
        }
    }
    public void insert(int line, int column, CharSequence text) {
        lock(true);
        documentVersion.getAndIncrement();
        try {
            insertInternal(line, column, text);
        } finally {
            unlock(true);
        }
    }
    private void insertInternal(int line, int column, CharSequence text) {
        checkLineAndColumn(line, column);
        if (text == null) {
            throw new IllegalArgumentException("text can not be null");
        }
        if (column > lines.get(line).length()) {
            column = lines.get(line).length();
        }
        if (cursor != null)
            cursor.beforeInsert(line, column);
        dispatchBeforeModification();
        int workLine = line;
        int workIndex = column;
        var currLine = makeLineMutable(workLine);
        var helper = InsertTextHelper.forInsertion(text);
        int type, peekType = InsertTextHelper.TYPE_EOF;
        boolean fromPeek = false;
        var newLines = new LinkedList<ContentLine>();
        var startSeparator = currLine.getLineSeparator();
        while (true) {
            type = fromPeek ? peekType : helper.forward();
            fromPeek = false;
            if (type == InsertTextHelper.TYPE_EOF) {
                break;
            }
            if (type == InsertTextHelper.TYPE_LINE_CONTENT) {
                currLine.insert(workIndex, text, helper.getIndex(), helper.getIndexNext());
                workIndex += helper.getIndexNext() - helper.getIndex();
            } else {
                var separator = LineSeparator.fromSeparatorString(text, helper.getIndex(), helper.getIndexNext());
                currLine.setLineSeparator(separator);
                peekType = helper.forward();
                fromPeek = true;
                var newLine = new ContentLine(currLine.length() - workIndex + helper.getIndexNext() - helper.getIndex() + 10);
                newLine.insert(0, currLine, workIndex, currLine.length());
                currLine.delete(workIndex, currLine.length());
                workIndex = 0;
                currLine = newLine;
                newLines.add(newLine);
                workLine++;
            }
        }
        currLine.setLineSeparator(startSeparator);
        lines.addAll(line + 1, newLines);
        helper.recycle();
        textLength += text.length();
        this.dispatchAfterInsert(line, column, workLine, workIndex, text);
    }
    public void delete(int start, int end) {
        lock(true);
        checkIndex(start);
        checkIndex(end);
        documentVersion.getAndIncrement();
        try {
            CharPosition startPos = getIndexer().getCharPosition(start);
            CharPosition endPos = getIndexer().getCharPosition(end);
            if (start != end) {
                deleteInternal(startPos.line, startPos.column, endPos.line, endPos.column);
            }
        } finally {
            unlock(true);
        }
    }
    public void delete(int startLine, int columnOnStartLine, int endLine, int columnOnEndLine) {
        lock(true);
        documentVersion.getAndIncrement();
        try {
            deleteInternal(startLine, columnOnStartLine, endLine, columnOnEndLine);
        } finally {
            unlock(true);
        }
    }
    private void deleteInternal(int startLine, int columnOnStartLine, int endLine, int columnOnEndLine) {
        checkLineAndColumn(endLine, columnOnEndLine);
        checkLineAndColumn(startLine, columnOnStartLine);
        if (startLine == endLine && columnOnStartLine == columnOnEndLine) {
            return;
        }
        var endLineObj = lines.get(endLine);
        if (columnOnEndLine > endLineObj.length() && endLine + 1 < getLineCount()) {
            deleteInternal(startLine, columnOnStartLine, endLine + 1, 0);
            return;
        }
        var startLineObj = lines.get(startLine);
        if (columnOnStartLine > startLineObj.length()) {
            deleteInternal(startLine, startLineObj.length(), endLine, columnOnEndLine);
            return;
        }
        var changedContent = new StringBuilder();
        if (startLine == endLine) {
            var curr = makeLineMutable(startLine);
            int len = curr.length();
            if (columnOnStartLine < 0 || columnOnEndLine > len || columnOnStartLine > columnOnEndLine) {
                throw new StringIndexOutOfBoundsException("invalid bounds");
            }
            if (cursor != null) {
                cursor.beforeDelete(startLine, columnOnStartLine, endLine, columnOnEndLine);
            }
            dispatchBeforeModification();
            changedContent.append(curr, columnOnStartLine, columnOnEndLine);
            curr.delete(columnOnStartLine, columnOnEndLine);
            textLength -= columnOnEndLine - columnOnStartLine;
        } else if (startLine < endLine) {
            if (cursor != null)
                cursor.beforeDelete(startLine, columnOnStartLine, endLine, columnOnEndLine);
            dispatchBeforeModification();
            for (int i = startLine + 1; i <= endLine - 1; i++) {
                var line = lines.get(i);
                var separator = lines.get(i).getLineSeparator();
                textLength -= line.length() + separator.getLength();
                line.appendTo(changedContent);
                changedContent.append(separator.getContent());
                line.release();
            }
            if (endLine > startLine + 1) {
                lines.subList(startLine + 1, endLine).clear();
            }
            int currEnd = startLine + 1;
            var start = makeLineMutable(startLine);
            var end = lines.get(currEnd);
            textLength -= start.length() - columnOnStartLine;
            changedContent.insert(0, start, columnOnStartLine, start.length())
                    .insert(start.length() - columnOnStartLine, start.getLineSeparator().getContent());
            start.delete(columnOnStartLine, start.length());
            textLength -= columnOnEndLine;
            changedContent.append(end, 0, columnOnEndLine);
            textLength -= start.getLineSeparator().getLength();
            lines.remove(currEnd);
            start.append(new TextReference(end, columnOnEndLine, end.length()));
            start.setLineSeparator(end.getLineSeparator());
            end.release();
        } else {
            throw new IllegalArgumentException("start line > end line");
        }
        this.dispatchAfterDelete(startLine, columnOnStartLine, endLine, columnOnEndLine, changedContent);
    }
    private ContentLine makeLineMutable(int line) {
        var data = lines.get(line);
        var mut = data.toMutable();
        if (mut != data) {
            lines.set(line, mut);
            data.release();
        }
        return mut;
    }
    public void replace(int startLine, int columnOnStartLine, int endLine, int columnOnEndLine, CharSequence text) {
        if (text == null) {
            throw new IllegalArgumentException("text can not be null");
        }
        lock(true);
        documentVersion.getAndIncrement();
        try {
            this.dispatchBeforeReplace();
            deleteInternal(startLine, columnOnStartLine, endLine, columnOnEndLine);
            insertInternal(startLine, columnOnStartLine, text);
        } finally {
            unlock(true);
        }
    }
    public void replace(int startIndex, int endIndex, @NonNull CharSequence text) {
        var start = getIndexer().getCharPosition(startIndex);
        var end = getIndexer().getCharPosition(endIndex);
        replace(start.line, start.column, end.line, end.column, text);
    }
    public long getDocumentVersion() {
        return documentVersion.get();
    }
    public TextRange undo() {
        return undoManager.undo(this);
    }
    public void redo() {
        undoManager.redo(this);
    }
    public boolean isUndoManagerWorking() {
        return undoManager.isModifyingContent();
    }
    public boolean canUndo() {
        return undoManager.canUndo();
    }
    public boolean canRedo() {
        return undoManager.canRedo();
    }
    public boolean isUndoEnabled() {
        return undoManager.isUndoEnabled();
    }
    public void setUndoEnabled(boolean enabled) {
        undoManager.setUndoEnabled(enabled);
    }
    public int getMaxUndoStackSize() {
        return undoManager.getMaxUndoStackSize();
    }
    public void setMaxUndoStackSize(int maxSize) {
        undoManager.setMaxUndoStackSize(maxSize);
    }
    public boolean beginBatchEdit() {
        nestedBatchEdit++;
        return isInBatchEdit();
    }
    public boolean endBatchEdit() {
        nestedBatchEdit--;
        if (nestedBatchEdit == 0) {
            undoManager.onExitBatchEdit();
        }
        if (nestedBatchEdit < 0) {
            nestedBatchEdit = 0;
        }
        return isInBatchEdit();
    }
    public int getNestedBatchEdit() {
        return nestedBatchEdit;
    }
    public void resetBatchEdit() {
        nestedBatchEdit = 0;
    }
    public boolean isInBatchEdit() {
        return nestedBatchEdit > 0;
    }
    public void addContentListener(ContentListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener can not be null");
        }
        if (listener instanceof Indexer) {
            throw new IllegalArgumentException("Permission denied");
        }
        if (!contentListeners.contains(listener)) {
            contentListeners.add(listener);
        }
    }
    public void removeContentListener(ContentListener listener) {
        if (listener instanceof Indexer) {
            throw new IllegalArgumentException("Permission denied");
        }
        contentListeners.remove(listener);
    }
    public Indexer getIndexer() {
        if (cursor != null) {
            return cursor.getIndexer();
        }
        return indexer;
    }
    public Content subContent(int startLine, int startColumn, int endLine, int endColumn) {
        lock(false);
        try {
            return subContentInternal(startLine, startColumn, endLine, endColumn);
        } finally {
            unlock(false);
        }
    }
    private Content subContentInternal(int startLine, int startColumn, int endLine, int endColumn) {
        var c = new Content();
        c.setUndoEnabled(false);
        if (startLine == endLine) {
            var line = lines.get(startLine);
            if (endColumn == line.length() + 1 && line.getLineSeparator() == LineSeparator.CRLF) {
                if (startColumn < endColumn) {
                    c.insert(0, 0, line.subSequence(startColumn, line.length()));
                    c.lines.get(0).setLineSeparator(LineSeparator.CR);
                    c.textLength++;
                    c.lines.add(new ContentLine());
                }
            } else {
                c.insert(0, 0, line.subSequence(startColumn, endColumn));
            }
        } else if (startLine < endLine) {
            var firstLine = lines.get(startLine);
            if (firstLine.getLineSeparator() == LineSeparator.CRLF) {
                if (startColumn <= firstLine.length()) {
                    c.insert(0, 0, firstLine.subSequence(startColumn, firstLine.length()));
                    c.lines.get(0).setLineSeparator(firstLine.getLineSeparator());
                    c.textLength += firstLine.getLineSeparator().getLength();
                } else if (startColumn == firstLine.length() + 1) {
                    c.lines.get(0).setLineSeparator(LineSeparator.LF);
                    c.textLength += LineSeparator.LF.getLength();
                } else {
                    throw new IndexOutOfBoundsException();
                }
            } else {
                c.insert(0, 0, firstLine.subSequence(startColumn, firstLine.length()));
                c.lines.get(0).setLineSeparator(firstLine.getLineSeparator());
                c.textLength += firstLine.getLineSeparator().getLength();
            }
            for (int i = startLine + 1; i < endLine; i++) {
                var line = lines.get(i);
                c.lines.add(new ContentLine(line));
                c.textLength += line.length() + line.getLineSeparator().getLength();
            }
            var end = lines.get(endLine);
            if (endColumn == end.length() + 1 && end.getLineSeparator() == LineSeparator.CRLF) {
                var newLine = new ContentLine().insert(0, end, 0, endColumn - 1);
                c.lines.add(newLine);
                newLine.setLineSeparator(LineSeparator.CR);
                c.textLength += endColumn + 1;
            } else {
                c.lines.add(new ContentLine().insert(0, end, 0, endColumn));
                c.textLength += endColumn;
            }
        } else {
            throw new StringIndexOutOfBoundsException("start > end");
        }
        c.setUndoEnabled(true);
        return c;
    }
    private StringBuilder subStringBuilder(int startLine, int startColumn, int endLine, int endColumn, int length) {
        var sb = new StringBuilder(length);
        if (startLine == endLine) {
            var line = lines.get(startLine);
            if (endColumn == line.length() + 1 && line.getLineSeparator() == LineSeparator.CRLF) {
                if (startColumn < endColumn) {
                    sb.append(lines.get(startLine), startColumn, line.length())
                            .append(LineSeparator.CR.getContent());
                }
            } else {
                sb.append(lines.get(startLine), startColumn, endColumn);
            }
        } else if (startLine < endLine) {
            var firstLine = lines.get(startLine);
            if (firstLine.getLineSeparator() == LineSeparator.CRLF) {
                if (startColumn <= firstLine.length()) {
                    sb.append(firstLine, startColumn, firstLine.length());
                    sb.append(firstLine.getLineSeparator().getContent());
                } else if (startColumn == firstLine.length() + 1) {
                    sb.append(LineSeparator.LF.getContent());
                } else {
                    throw new IndexOutOfBoundsException();
                }
            } else {
                sb.append(firstLine, startColumn, firstLine.length());
                sb.append(firstLine.getLineSeparator().getContent());
            }
            for (int i = startLine + 1; i < endLine; i++) {
                var line = lines.get(i);
                sb.append(line)
                        .append(line.getLineSeparator().getContent());
            }
            var end = lines.get(endLine);
            if (endColumn == end.length() + 1 && end.getLineSeparator() == LineSeparator.CRLF) {
                sb.append(end, 0, endColumn)
                        .append(LineSeparator.CR.getContent());
            } else {
                sb.append(end, 0, endColumn);
            }
        } else {
            throw new StringIndexOutOfBoundsException("start > end");
        }
        return sb;
    }
    @NonNull
    public Directions getLineDirections(int line) {
        lock(false);
        try {
            return bidi.getLineDirections(line);
        } finally {
            unlock(false);
        }
    }
    public void setBidiEnabled(boolean enabled) {
        bidi.setEnabled(enabled);
    }
    public boolean isBidiEnabled() {
        return bidi.isEnabled();
    }
    public boolean isRtlAt(int line, int column) {
        var dirs = getLineDirections(line);
        for (int i = 0; i < dirs.getRunCount(); i++) {
            if (column >= dirs.getRunStart(i) && column < dirs.getRunEnd(i)) {
                return dirs.isRunRtl(i);
            }
        }
        return false;
    }
    @Override
    public boolean equals(Object anotherObject) {
        if (anotherObject instanceof Content content) {
            if (content.length() != this.length()) {
                return false;
            }
            for (int i = 0; i < this.getLineCount(); i++) {
                if (!textEquals(lines.get(i), content.lines.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
    @Override
    public int hashCode() {
        return Objects.hash(lines, textLength);
    }
    @NonNull
    @Override
    public String toString() {
        return toStringBuilder().toString();
    }
    public StringBuilder toStringBuilder() {
        var sb = new StringBuilder();
        appendToStringBuilder(sb);
        return sb;
    }
    public UndoManager getUndoManager() {
        return undoManager;
    }
    public void setUndoManager(UndoManager manager) {
        this.undoManager = manager;
    }
    public void appendToStringBuilder(StringBuilder sb) {
        sb.ensureCapacity(sb.length() + length());
        lock(false);
        try {
            final int lines = getLineCount();
            for (int i = 0; i < lines; i++) {
                var line = this.lines.get(i);
                line.appendTo(sb);
                sb.append(line.getLineSeparator().getContent());
            }
        } finally {
            unlock(false);
        }
    }
    public Cursor getCursor() {
        if (cursor == null) {
            cursor = new Cursor(this);
        }
        return cursor;
    }
    public boolean isCursorCreated() {
        return cursor != null;
    }
    private void dispatchBeforeReplace() {
        undoManager.beforeReplace(this);
        if (cursor != null)
            cursor.beforeReplace();
        if (indexer instanceof ContentListener) {
            ((ContentListener) indexer).beforeReplace(this);
        }
        for (ContentListener lis : contentListeners) {
            lis.beforeReplace(this);
        }
    }
    private void dispatchAfterDelete(int a, int b, int c, int d, @NonNull CharSequence e) {
        undoManager.afterDelete(this, a, b, c, d, e);
        if (cursor != null)
            cursor.afterDelete(a, b, c, d, e);
        if (indexer instanceof ContentListener) {
            ((ContentListener) indexer).afterDelete(this, a, b, c, d, e);
        }
        for (ContentListener lis : contentListeners) {
            lis.afterDelete(this, a, b, c, d, e);
        }
    }
    private void dispatchBeforeModification() {
        undoManager.beforeModification(this);
        for (ContentListener lis : contentListeners) {
            lis.beforeModification(this);
        }
    }
    private void dispatchAfterInsert(int a, int b, int c, int d, @NonNull CharSequence e) {
        undoManager.afterInsert(this, a, b, c, d, e);
        if (cursor != null)
            cursor.afterInsert(a, b, c, d, e);
        if (indexer instanceof ContentListener) {
            ((ContentListener) indexer).afterInsert(this, a, b, c, d, e);
        }
        for (ContentListener lis : contentListeners) {
            lis.afterInsert(this, a, b, c, d, e);
        }
    }
    protected void checkIndex(int index) {
        if (index > length() || index < 0) {
            throw new StringIndexOutOfBoundsException("Index " + index + " out of bounds. length:" + length());
        }
    }
    protected void checkLine(int line) {
        if (line >= getLineCount() || line < 0) {
            throw new StringIndexOutOfBoundsException("Line " + line + " out of bounds. line count:" + getLineCount());
        }
    }
    protected void checkLineAndColumn(int line, int column) {
        checkLine(line);
        var text = lines.get(line);
        int len = text.length() + text.getLineSeparator().getLength();
        if (column > len || column < 0) {
            throw new StringIndexOutOfBoundsException(
                    "Column " + column + " out of bounds. line: " + line + " , column count (line separator included):" + len);
        }
    }
    public Content copyText() {
        return copyText(true);
    }
    public Content copyText(boolean newContentThreadSafe) {
        return copyText(newContentThreadSafe, false);
    }
    public Content copyText(boolean newContentThreadSafe, boolean shallow) {
        lock(false);
        try {
            var n = new Content(null, newContentThreadSafe);
            n.lines.remove(0);
            ((ArrayList<ContentLine>) n.lines).ensureCapacity(getLineCount());
            if (shallow) {
                for (ContentLine line : lines) {
                    line.retain();
                }
                n.lines.addAll(lines);
            } else {
                for (ContentLine line : lines) {
                    n.lines.add(new ContentLine(line));
                }
            }
            n.textLength = textLength;
            return n;
        } finally {
            unlock(false);
        }
    }
    public Content copyTextShallow() {
        return copyTextShallow(false);
    }
    public Content copyTextShallow(boolean newContentThreadSafe) {
        return copyText(newContentThreadSafe, true);
    }
    public void release() {
        lock(true);
        try {
            for (ContentLine line : lines) {
                line.release();
            }
            lines.clear();
            textLength = 0;
            this.cursor = null;
            this.bidi.destroy();
        } finally {
            unlock(true);
        }
    }
    protected int getColumnCountUnsafe(int line) {
        return lines.get(line).length();
    }
    @NonNull
    protected LineSeparator getLineSeparatorUnsafe(int line) {
        return lines.get(line).getLineSeparator();
    }
    public void runReadActionsOnLines(int startLine, int endLine, @NonNull ContentLineConsumer consumer) {
        lock(false);
        try {
            for (int i = startLine; i <= endLine; i++) {
                consumer.accept(i, lines.get(i), bidi.getLineDirections(i));
            }
        } finally {
            unlock(false);
        }
    }
    public void runReadActionsOnLines(int startLine, int endLine, @NonNull ContentLineConsumer2 consumer) {
        lock(false);
        try {
            var flag = new ContentLineConsumer2.AbortFlag();
            for (int i = startLine; i <= endLine && !flag.set; i++) {
                consumer.accept(i, lines.get(i), flag);
            }
        } finally {
            unlock(false);
        }
    }
    public interface ContentLineConsumer {
        void accept(int lineIndex, @NonNull ContentLine line, @NonNull Directions dirs);
    }
    public interface ContentLineConsumer2 {
        void accept(int lineIndex, @NonNull ContentLine line, @NonNull AbortFlag flag);
        class AbortFlag {
            public boolean set = false;
        }
    }
}
