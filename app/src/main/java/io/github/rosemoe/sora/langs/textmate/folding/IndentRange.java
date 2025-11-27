
package io.github.rosemoe.sora.langs.textmate.folding;

import java.util.ArrayList;
import java.util.List;
import io.github.rosemoe.sora.lang.analysis.AsyncIncrementalAnalyzeManager;
import io.github.rosemoe.sora.text.Content;
import org.eclipse.tm4e.core.internal.oniguruma.OnigRegExp;
import org.eclipse.tm4e.core.internal.oniguruma.OnigResult;

public class IndentRange {
    public static final int MAX_LINE_NUMBER = 0xFFFFFF;
    public static final int MAX_FOLDING_REGIONS = 0xFFFF;
    public static final int MASK_INDENT = 0xFF000000;
    public static int computeStartColumn(char[] line, int len, int tabSize) {
        int column = 0;
        int i = 0;
        while (i < len) {
            char chCode = line[i];
            if (chCode == ' ') {
                column++;
            } else if (chCode == '\t') {
                column += tabSize;
            } else {
                break;
            }
            i++;
        }
        if (i == len) {
            return -1;
        }
        return column;
    }
    public static int computeIndentLevel(char[] line, int len, int tabSize) {
        int indent = 0;
        int i = 0;
        while (i < len) {
            char chCode = line[i];
            if (chCode == ' ') {
                indent++;
            } else if (chCode == '\t') {
                indent = indent - indent % tabSize + tabSize;
            } else {
                break;
            }
            i++;
        }
        if (i == len) {
            return -1;
        }
        return indent;
    }
    public static FoldingRegions computeRanges(Content model, int tabSize, boolean offSide, FoldingHelper helper, OnigRegExp pattern, AsyncIncrementalAnalyzeManager<?, ?>.CodeBlockAnalyzeDelegate delegate) throws Exception {
        RangesCollector result = new RangesCollector();
        List<PreviousRegion> previousRegions = new ArrayList<>();
        int line = model.getLineCount() + 1;
        previousRegions.add(new PreviousRegion(-1, line, line));
        for (line = model.getLineCount() - 1; line >= 0 && delegate.isNotCancelled(); line--) {
            int indent = helper.getIndentFor(line);
            PreviousRegion previous = previousRegions.get(previousRegions.size() - 1);
            if (indent == -1) {
                if (offSide) {
                    previous.endAbove = line;
                }
                continue; 
            }
            OnigResult m;
            if (pattern != null && (m = helper.getResultFor(line)) != null) {
                if (m.count() >= 2) { 
                    int i = previousRegions.size() - 1;
                    while (i > 0 && previousRegions.get(i).indent != -2) {
                        i--;
                    }
                    if (i > 0) {
                        previous = previousRegions.get(i);
                        result.insertFirst(line, previous.line, indent);
                        previous.line = line;
                        previous.indent = indent;
                        previous.endAbove = line;
                        continue;
                    } else {
                    }
                } else { 
                    previousRegions.add(new PreviousRegion(-2, line, line));
                    continue;
                }
            }
            if (previous.indent > indent) {
                do {
                    previousRegions.remove(previousRegions.size() - 1);
                    previous = previousRegions.get(previousRegions.size() - 1);
                } while (previous.indent > indent);
                int endLineNumber = previous.endAbove - 1;
                if (endLineNumber - line >= 1) { 
                    result.insertFirst(line, endLineNumber, indent);
                }
            }
            if (previous.indent == indent) {
                previous.endAbove = line;
            } else { 
                previousRegions.add(new PreviousRegion(indent, line, line));
            }
        }
        return result.toIndentRanges(model);
    }
}
