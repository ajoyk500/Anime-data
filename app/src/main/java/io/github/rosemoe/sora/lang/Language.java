
package io.github.rosemoe.sora.lang;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.completion.CompletionCancelledException;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.format.Formatter;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.widget.SymbolPairMatch;

public interface Language {
    int INTERRUPTION_LEVEL_STRONG = 0;
    int INTERRUPTION_LEVEL_SLIGHT = 1;
    int INTERRUPTION_LEVEL_NONE = 2;
    @NonNull
    AnalyzeManager getAnalyzeManager();
    int getInterruptionLevel();
    @WorkerThread
    void requireAutoComplete(@NonNull ContentReference content, @NonNull CharPosition position,
                             @NonNull CompletionPublisher publisher,
                             @NonNull Bundle extraArguments) throws CompletionCancelledException;
    @UiThread
    int getIndentAdvance(@NonNull ContentReference content, int line, int column);
    @UiThread
    default int getIndentAdvance(
      @NonNull ContentReference content,
      int line,
      int column,
      int spaceCountOnLine,
      int tabCountOnLine
    ) {
        return getIndentAdvance(content, line, column);
    }
    @UiThread
    boolean useTab();
    @UiThread
    @NonNull
    Formatter getFormatter();
    @UiThread
    SymbolPairMatch getSymbolPairs();
    @UiThread
    @Nullable
    NewlineHandler[] getNewlineHandlers();
    @UiThread
    @Nullable
    default QuickQuoteHandler getQuickQuoteHandler() {
        return null;
    }
    @UiThread
    void destroy();
}
