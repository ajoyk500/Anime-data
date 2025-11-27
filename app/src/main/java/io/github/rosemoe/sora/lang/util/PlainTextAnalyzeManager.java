
package io.github.rosemoe.sora.lang.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.github.rosemoe.sora.lang.analysis.StyleReceiver;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.text.CharPosition;

public final class PlainTextAnalyzeManager extends BaseAnalyzeManager {
    @Override
    public void insert(@NonNull CharPosition start, @NonNull CharPosition end, @NonNull CharSequence insertedContent, @Nullable StyleReceiver receiver) {
    }
    @Override
    public void delete(@NonNull CharPosition start, @NonNull CharPosition end, @NonNull CharSequence deletedContent, @Nullable StyleReceiver receiver) {
    }
    @Override
    public void rerun(@Nullable StyleReceiver receiver) {
        final var styleReceiver = (receiver == null) ? getReceiver() : receiver;
        final var ref = getContentRef();
        if (styleReceiver != null && ref != null) {
            var style = new Styles();
            style.spans = new PlainTextSpans(ref.getLineCount());
            styleReceiver.setStyles(this, style);
        } else if (styleReceiver != null) {
            styleReceiver.setStyles(this, null);
        }
    }
}
