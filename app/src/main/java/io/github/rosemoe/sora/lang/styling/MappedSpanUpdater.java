
package io.github.rosemoe.sora.lang.styling;

import java.util.ArrayList;
import java.util.List;
import io.github.rosemoe.sora.lang.styling.span.SpanExtAttrs;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class MappedSpanUpdater {
    public static void shiftSpansOnMultiLineDelete(List<List<Span>> map, int startLine, int startColumn, int endLine, int endColumn) {
        int lineCount = endLine - startLine - 1;
        while (lineCount > 0) {
            SpanFactory.recycleAll(map.remove(startLine + 1));
            lineCount--;
        }
        List<Span> startLineSpans = map.get(startLine);
        int index = startLineSpans.size() - 1;
        while (index > 0) {
            if (startLineSpans.get(index).getColumn() >= startColumn) {
                startLineSpans.remove(index).recycle();
                index--;
            } else {
                break;
            }
        }
        List<Span> endLineSpans = map.remove(startLine + 1);
        for (int i = 0; i < endLineSpans.size(); i++) {
            endLineSpans.get(i).shiftColumnBy(startColumn - endColumn);
        }
        while (endLineSpans.size() > 1) {
            if (endLineSpans.get(0).getColumn() <= startColumn && endLineSpans.get(1).getColumn() <= startColumn) {
                endLineSpans.remove(0).recycle();
            } else {
                break;
            }
        }
        if (endLineSpans.get(0).getColumn() <= startColumn) {
            endLineSpans.get(0).setColumn(startColumn);
        }
        startLineSpans.addAll(endLineSpans);
    }
    public static void shiftSpansOnSingleLineDelete(List<List<Span>> map, int line, int startCol, int endCol) {
        if (map == null || map.isEmpty()) {
            return;
        }
        List<Span> spanList = map.get(line);
        int startIndex = findSpanIndexFor(spanList, 0, startCol);
        if (startIndex == -1) {
            return;
        }
        int endIndex = findSpanIndexFor(spanList, startIndex, endCol);
        if (endIndex == -1) {
            endIndex = spanList.size();
        }
        int removeCount = endIndex - startIndex;
        for (int i = 0; i < removeCount; i++) {
            spanList.remove(startIndex).recycle();
        }
        int delta = endCol - startCol;
        while (startIndex < spanList.size()) {
            spanList.get(startIndex).shiftColumnBy(-delta);
            startIndex++;
        }
        if (spanList.isEmpty() || spanList.get(0).getColumn() != 0) {
            spanList.add(0, SpanFactory.obtain(0, EditorColorScheme.TEXT_NORMAL));
        }
        for (int i = 0; i + 1 < spanList.size(); i++) {
            if (spanList.get(i).getColumn() >= spanList.get(i + 1).getColumn()) {
                spanList.remove(i).recycle();
                i--;
            }
        }
    }
    public static void shiftSpansOnSingleLineInsert(List<List<Span>> map, int line, int startCol, int endCol) {
        if (map == null || map.isEmpty()) {
            return;
        }
        List<Span> spanList = map.get(line);
        int index = findSpanIndexFor(spanList, 0, startCol);
        if (index == -1) {
            return;
        }
        int originIndex = index;
        int delta = endCol - startCol;
        while (index < spanList.size()) {
            spanList.get(index++).shiftColumnBy(delta);
        }
        if (originIndex == 0) {
            Span first = spanList.get(0);
            if (first.getColumn() == EditorColorScheme.TEXT_NORMAL && first.hasSpanExt(SpanExtAttrs.EXT_UNDERLINE_COLOR)) {
                first.setColumn(0);
            } else {
                spanList.add(0, SpanFactory.obtain(0, EditorColorScheme.TEXT_NORMAL));
            }
        }
    }
    public static void shiftSpansOnMultiLineInsert(List<List<Span>> map, int startLine, int startColumn, int endLine, int endColumn) {
        List<Span> startLineSpans = map.get(startLine);
        int extendedSpanIndex = findSpanIndexFor(startLineSpans, 0, startColumn);
        if (extendedSpanIndex == -1) {
            extendedSpanIndex = startLineSpans.size() - 1;
        }
        if (startLineSpans.get(extendedSpanIndex).getColumn() > startColumn) {
            extendedSpanIndex--;
        }
        Span extendedSpan;
        if (extendedSpanIndex < 0 || extendedSpanIndex >= startLineSpans.size()) {
            extendedSpan = SpanFactory.obtain(0, EditorColorScheme.TEXT_NORMAL);
        } else {
            extendedSpan = startLineSpans.get(extendedSpanIndex);
        }
        for (int i = 0; i < endLine - startLine; i++) {
            List<Span> list = new ArrayList<>();
            var newSpan = extendedSpan.copy();
            newSpan.setColumn(0);
            list.add(newSpan);
            map.add(startLine + 1, list);
        }
        List<Span> endLineSpans = map.get(endLine);
        int idx = extendedSpanIndex;
        while (idx < startLineSpans.size()) {
            Span span = startLineSpans.get(idx++);
            Span newSpan = span.copy();
            newSpan.setColumn(Math.max(0, span.getColumn() - startColumn + endColumn));
            endLineSpans.add(newSpan);
        }
        while (extendedSpanIndex + 1 < startLineSpans.size()) {
            startLineSpans.remove(startLineSpans.size() - 1).recycle();
        }
        if (endLineSpans.size() > 1 && endLineSpans.get(0).getColumn() == 0 && endLineSpans.get(1).getColumn() == 0) {
            endLineSpans.remove(0).recycle();
        }
    }
    private static int findSpanIndexFor(List<Span> spans, int initialPosition, int targetCol) {
        for (int i = initialPosition; i < spans.size(); i++) {
            if (spans.get(i).getColumn() >= targetCol) {
                return i;
            }
        }
        return -1;
    }
}
