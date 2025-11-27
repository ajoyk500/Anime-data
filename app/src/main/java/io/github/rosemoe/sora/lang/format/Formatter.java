
package io.github.rosemoe.sora.lang.format;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.TextRange;

public interface Formatter {
    void format(@NonNull Content text, @NonNull TextRange cursorRange);
    void formatRegion(@NonNull Content text, @NonNull TextRange rangeToFormat, @NonNull TextRange cursorRange);
    void setReceiver(@Nullable FormatResultReceiver receiver);
    boolean isRunning();
    void destroy();
    default void cancel() {
    }
    interface FormatResultReceiver {
        void onFormatSucceed(@NonNull CharSequence applyContent, @Nullable TextRange cursorRange);
        void onFormatFail(@Nullable Throwable throwable);
    }
}
