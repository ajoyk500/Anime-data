
package io.github.rosemoe.sora.text;

import androidx.annotation.NonNull;
import java.io.IOException;
import java.io.Reader;

public class ContentReference extends TextReference {
    private final Content content;
    public ContentReference(@NonNull Content ref) {
        super(ref);
        this.content = ref;
    }
    @Override
    public char charAt(int index) {
        validateAccess();
        return content.charAt(index);
    }
    public char charAt(int line, int column) {
        validateAccess();
        return content.charAt(line, column);
    }
    public int getCharIndex(int line, int column) {
        validateAccess();
        return content.getCharIndex(line, column);
    }
    public CharPosition getCharPosition(int line, int column) {
        validateAccess();
        return content.getIndexer().getCharPosition(line, column);
    }
    public CharPosition getCharPosition(int index) {
        validateAccess();
        return content.getIndexer().getCharPosition(index);
    }
    public int getLineCount() {
        validateAccess();
        return content.getLineCount();
    }
    public int getColumnCount(int line) {
        validateAccess();
        return content.getColumnCount(line);
    }
    public String getLineSeparator(int line) {
        validateAccess();
        return content.getLineSeparatorUnsafe(line).getContent();
    }
    public String getLine(int line) {
        validateAccess();
        return content.getLineString(line);
    }
    public void getLineChars(int line, char[] dest) {
        validateAccess();
        content.getLineChars(line, dest);
    }
    public void appendLineTo(StringBuilder sb, int line) {
        validateAccess();
        content.getLine(line).appendTo(sb);
    }
    public long getDocumentVersion() {
        validateAccess();
        return content.getDocumentVersion();
    }
    public Reader createReader() {
        return new RefReader();
    }
    @NonNull
    @Override
    public Content getReference() {
        return (Content) super.getReference();
    }
    @Override
    public ContentReference setValidator(Validator validator) {
        super.setValidator(validator);
        return this;
    }
    private class RefReader extends Reader {
        private int markedLine, markedColumn;
        private int line;
        private int column;
        @Override
        public int read(char[] chars, int offset, int length) {
            if (chars.length < offset + length) {
                throw new IllegalArgumentException("size not enough");
            }
            int read = 0;
            while (read < length && line < getLineCount()) {
                var targetLine = content.getLine(line);
                var separatorLength = targetLine.getLineSeparator().getLength();
                var columnCount = targetLine.length();
                int toRead = Math.min(columnCount - column, length - read);
                toRead = Math.max(0, toRead);
                if (toRead > 0) {
                    content.getRegionOnLine(line, column, column + toRead, chars, offset + read);
                }
                column += toRead;
                read += toRead;
                while (read < length && columnCount <= column && column < columnCount + separatorLength) {
                    chars[offset + read] = targetLine.getLineSeparator().getContent().charAt(column - columnCount);
                    read++;
                    column++;
                }
                if (column >= columnCount + separatorLength) {
                    line++;
                    column = 0;
                }
            }
            if (read == 0) {
                return -1;
            }
            return read;
        }
        @Override
        public void close() {
            line = Integer.MAX_VALUE;
            column = Integer.MAX_VALUE;
        }
        @Override
        public boolean markSupported() {
            return true;
        }
        @Override
        public void mark(int readAheadLimit) throws IOException {
            markedLine = line;
            markedColumn = column;
        }
        @Override
        public void reset() throws IOException {
            line = markedLine;
            column = markedColumn;
        }
    }
}
