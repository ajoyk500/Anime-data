
package io.github.rosemoe.sora.lang.styling;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.github.rosemoe.sora.text.CharPosition;

public class StylesUtils {
    private final static String LOG_TAG = "StylesUtils";
    public static boolean checkNoCompletion(@Nullable Styles styles, @NonNull CharPosition pos) {
        var span = getSpanForPosition(styles, pos);
        return span == null || TextStyle.isNoCompletion(span.getStyle());
    }
    public static Span getSpanForPosition(@Nullable Styles styles, @NonNull CharPosition pos) {
        return getSpanForPositionImpl(styles, pos, 0);
    }
    public static Span getFollowingSpanForPosition(@Nullable Styles styles, @NonNull CharPosition pos) {
        return getSpanForPositionImpl(styles, pos, 1);
    }
    @Nullable
    private static Span getSpanForPositionImpl(@Nullable Styles styles, @NonNull CharPosition pos, int spanIndexOffset) {
        var line = pos.line;
        var column = pos.column;
        Spans spans;
        if (styles == null || (spans = styles.spans) == null) {
            return null;
        }
        Exception ex = null;
        var reader = spans.read();
        try {
            reader.moveToLine(line);
            int index = reader.getSpanCount() - 1;
            if (index == -1) {
                return null;
            }
            for (int i = 0; i < reader.getSpanCount(); i++) {
                if (reader.getSpanAt(i).getColumn() > column) {
                    index = i - 1;
                    break;
                }
            }
            index = index + spanIndexOffset;
            if (index < 0 || index >= reader.getSpanCount()) {
                return null;
            }
            return reader.getSpanAt(index);
        } catch (Exception e) {
            ex = e;
            return null;
        } finally {
            try {
                reader.moveToLine(-1);
            } catch (Exception e1) {
                if (ex != null) {
                    ex.addSuppressed(e1);
                } else {
                    Log.e(LOG_TAG, "failed to close " + reader, e1);
                }
            }
            if (ex != null)
                Log.e(LOG_TAG, "failed to get spans from " + reader + " at " + pos, ex);
        }
    }
}
