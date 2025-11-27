
package io.github.rosemoe.sora.lang.styling;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class MappedSpans implements Spans {
    private final List<List<Span>> spanMap;
    private MappedSpans(@NonNull List<List<Span>> spanMap) {
        this.spanMap = spanMap;
    }
    @Override
    public Spans copy() {
        ArrayList<List<Span>> newSpanMap = new ArrayList<>(spanMap.size());
        for(List<Span> embedList : spanMap) {
            ArrayList<Span> newEmbedList = new ArrayList<>(embedList.size());
            for (Span span : embedList) {
                newEmbedList.add(span.copy());
            }
            newSpanMap.add(newEmbedList);
        }
        return new MappedSpans(newSpanMap);
    }
    @Override
    public void adjustOnInsert(CharPosition start, CharPosition end) {
        var startLine = start.line;
        var endLine = end.line;
        var startColumn = start.column;
        var endColumn = end.column;
        if (startLine == endLine) {
            MappedSpanUpdater.shiftSpansOnSingleLineInsert(spanMap, startLine, startColumn, endColumn);
        } else {
            MappedSpanUpdater.shiftSpansOnMultiLineInsert(spanMap, startLine, startColumn, endLine, endColumn);
        }
    }
    @Override
    public void adjustOnDelete(CharPosition start, CharPosition end) {
        var startLine = start.line;
        var endLine = end.line;
        var startColumn = start.column;
        var endColumn = end.column;
        if (startLine == endLine) {
            MappedSpanUpdater.shiftSpansOnSingleLineDelete(spanMap, startLine, startColumn, endColumn);
        } else {
            MappedSpanUpdater.shiftSpansOnMultiLineDelete(spanMap, startLine, startColumn, endLine, endColumn);
        }
    }
    @Override
    public Reader read() {
        return new MappedSpansAccessor();
    }
    @Override
    public boolean supportsModify() {
        return true;
    }
    @Override
    public Modifier modify() {
        return new MappedSpansAccessor();
    }
    @Override
    public int getLineCount() {
        return spanMap.size();
    }
    public static class Builder {
        private final List<List<Span>> spans;
        private Span last;
        public Builder() {
            this(128);
        }
        public Builder(int lineCapacity) {
            spans = new ArrayList<>(lineCapacity);
        }
        public void addIfNeeded(int spanLine, int column, long style) {
            if (last != null && last.getStyle() == style) {
                return;
            }
            add(spanLine, SpanFactory.obtain(column, style));
        }
        public void add(int spanLine, Span span) {
            int mapLine = spans.size() - 1;
            if (spanLine == mapLine) {
                spans.get(spanLine).add(span);
            } else if (spanLine > mapLine) {
                Span extendedSpan = last;
                if (extendedSpan == null) {
                    extendedSpan = SpanFactory.obtain(0, EditorColorScheme.TEXT_NORMAL);
                }
                while (mapLine < spanLine) {
                    List<Span> lineSpans = new ArrayList<>();
                    lineSpans.add(copyAndSetColumn(extendedSpan, 0));
                    spans.add(lineSpans);
                    mapLine++;
                }
                List<Span> lineSpans = spans.get(spanLine);
                if (span.getColumn() == 0) {
                    lineSpans.clear();
                }
                lineSpans.add(span);
            } else {
                throw new IllegalStateException("Invalid position");
            }
            last = span;
        }
        public void determine(int line) {
            int mapLine = spans.size() - 1;
            var extendedSpan = last;
            if (extendedSpan == null) {
                extendedSpan = SpanFactory.obtain(0, EditorColorScheme.TEXT_NORMAL);
            }
            while (mapLine < line) {
                List<Span> lineSpans = new ArrayList<>();
                lineSpans.add(copyAndSetColumn(extendedSpan, 0));
                spans.add(lineSpans);
                mapLine++;
            }
        }
        public void addNormalIfNull() {
            if (spans.isEmpty()) {
                List<Span> spanList = new ArrayList<>();
                spanList.add(SpanFactory.obtain(0, EditorColorScheme.TEXT_NORMAL));
                spans.add(spanList);
            }
        }
        public MappedSpans build() {
            return new MappedSpans(spans);
        }
    }
    private class MappedSpansAccessor implements Reader, Modifier {
        private List<Span> span;
        private void checkLine() {
            if (span == null) {
                throw new IllegalStateException("line must be set first");
            }
        }
        @Override
        public void moveToLine(int line) {
            if (line == -1) {
                span = null;
                return;
            }
            span = spanMap.get(line);
        }
        @Override
        public int getSpanCount() {
            checkLine();
            return span.size();
        }
        @Override
        public Span getSpanAt(int index) {
            checkLine();
            return span.get(index);
        }
        @Override
        public List<Span> getSpansOnLine(int line) {
            return Collections.unmodifiableList(spanMap.get(line));
        }
        @Override
        public void setSpansOnLine(int line, List<Span> spans) {
            var last = spanMap.get(spanMap.size() - 1);
            var extend = last.get(last.size() - 1);
            while (spanMap.size() <= line) {
                var list = new ArrayList<Span>();
                list.add(copyAndSetColumn(extend, 0));
                spanMap.add(list);
            }
            spanMap.set(line, spans);
        }
        @Override
        public void addLineAt(int line, List<Span> spans) {
            spanMap.add(line, spans);
        }
        @Override
        public void deleteLineAt(int line) {
            spanMap.remove(line);
        }
    }
    private static Span copyAndSetColumn(Span s, int column) {
        var span = s.copy();
        span.setColumn(column);
        return span;
    }
}
