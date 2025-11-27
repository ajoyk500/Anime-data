
package io.github.rosemoe.sora.lang.styling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Collection;
import io.github.rosemoe.sora.lang.styling.color.ConstColor;
import io.github.rosemoe.sora.lang.styling.color.ResolvableColor;
import io.github.rosemoe.sora.lang.styling.span.SpanExt;
import io.github.rosemoe.sora.lang.styling.span.SpanExtAttrs;

public interface Span {
    void setColumn(int column);
    int getColumn();
    default void shiftColumnBy(int deltaColumn) {
        setColumn(getColumn() + deltaColumn);
    }
    void setStyle(long style);
    long getStyle();
    default int getForegroundColorId() {
        return TextStyle.getForegroundColorId(getStyle());
    }
    default int getBackgroundColorId() {
        return TextStyle.getBackgroundColorId(getStyle());
    }
    default long getStyleBits() {
        return TextStyle.getStyleBits(getStyle());
    }
    default void setUnderlineColor(int color) {
        if (color == 0) {
            setUnderlineColor(null);
            return;
        }
        setUnderlineColor(new ConstColor(color));
    }
    void setUnderlineColor(@Nullable ResolvableColor color);
    @Nullable
    ResolvableColor getUnderlineColor();
    void setExtra(Object extraData);
    Object getExtra();
    void setSpanExt(int extType, @Nullable SpanExt ext);
    boolean hasSpanExt(int extType);
    @Nullable
    <T> T getSpanExt(int extType);
    void removeAllSpanExt();
    void reset();
    @NonNull
    Span copy();
    boolean recycle();
    @NonNull
    static Span obtain(int column, long style) {
        return SpanFactory.obtain(column, style);
    }
    static void recycleAll(@NonNull Collection<Span> spans) {
        SpanFactory.recycleAll(spans);
    }
}
