
package io.github.rosemoe.sora.lang.styling;

import androidx.annotation.NonNull;
import java.util.Collection;
import io.github.rosemoe.sora.lang.styling.span.internal.SpanImpl;

public class SpanFactory {
    private SpanFactory() {
    }
    @NonNull
    public static Span obtain(int column, long style) {
        return SpanImpl.obtain(column, style);
    }
    public static void recycleAll(@NonNull Collection<Span> spans) {
        for (Span span : spans) {
            if (!span.recycle()) {
                return;
            }
        }
    }
}
