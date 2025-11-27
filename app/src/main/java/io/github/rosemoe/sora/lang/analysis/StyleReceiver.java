
package io.github.rosemoe.sora.lang.analysis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.github.rosemoe.sora.lang.brackets.BracketsProvider;
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticsContainer;
import io.github.rosemoe.sora.lang.styling.Styles;

public interface StyleReceiver {
    void setStyles(@NonNull AnalyzeManager sourceManager, @Nullable Styles styles);
    void setStyles(@NonNull AnalyzeManager sourceManager, @Nullable Styles styles, @Nullable Runnable action);
    default void updateStyles(@NonNull AnalyzeManager sourceManager, @NonNull Styles styles, @NonNull StyleUpdateRange range) {
        setStyles(sourceManager, styles);
    }
    void setDiagnostics(@NonNull AnalyzeManager sourceManager, @Nullable DiagnosticsContainer diagnostics);
    void updateBracketProvider(@NonNull AnalyzeManager sourceManager, @Nullable BracketsProvider provider);
}
