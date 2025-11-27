
package io.github.rosemoe.sora.lang.diagnostic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiagnosticsContainer {
    private final List<DiagnosticRegion> regions = new ArrayList<>();
    private final boolean shiftEnabled;
    public DiagnosticsContainer() {
        this(true);
    }
    public DiagnosticsContainer(boolean shiftEnabled) {
        this.shiftEnabled = shiftEnabled;
    }
    public synchronized void addDiagnostics(Collection<DiagnosticRegion> regions) {
        this.regions.addAll(regions);
    }
    public synchronized void addDiagnostic(DiagnosticRegion diagnostic) {
        regions.add(diagnostic);
    }
    public synchronized void queryInRegion(List<DiagnosticRegion> result, int startIndex, int endIndex) {
        for (var region : regions) {
            if (region.endIndex > startIndex && region.startIndex <= endIndex) {
                result.add(region);
            }
        }
    }
    public synchronized void shiftOnInsert(int insertStart, int insertEnd) {
        if (!shiftEnabled) {
            return;
        }
        var length = insertEnd - insertStart;
        for (var region : regions) {
            if (region.startIndex <= insertStart && region.endIndex >= insertStart) {
                region.endIndex += length;
            }
            if (region.startIndex > insertStart) {
                region.startIndex += length;
                region.endIndex += length;
            }
        }
    }
    public synchronized void shiftOnDelete(int deleteStart, int deleteEnd) {
        if (!shiftEnabled) {
            return;
        }
        var length = deleteEnd - deleteStart;
        var garbage = new ArrayList<DiagnosticRegion>();
        for (var region : regions) {
            var sharedStart = Math.max(deleteStart, region.startIndex);
            var sharedEnd = Math.min(deleteEnd, region.endIndex);
            if (sharedEnd <= sharedStart) {
                if (region.startIndex >= deleteEnd) {
                    region.startIndex -= length;
                    region.endIndex -= length;
                }
            } else {
                var sharedLength = sharedEnd - sharedStart;
                region.endIndex -= sharedLength;
                if (region.startIndex > deleteStart) {
                    var shiftLeftCount = region.startIndex - deleteStart;
                    region.startIndex -= shiftLeftCount;
                    region.endIndex -= shiftLeftCount;
                }
                if (region.startIndex == region.endIndex) {
                    garbage.add(region);
                }
            }
        }
        regions.removeAll(garbage);
    }
    public synchronized void reset() {
        regions.clear();
    }
}
