
package io.github.rosemoe.sora.lang.analysis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;
import io.github.rosemoe.sora.lang.styling.Span;

public interface IncrementalAnalyzeManager<S, T> extends AnalyzeManager {
    S getInitialState();
    LineTokenizeResult<S, T> getState(int line);
    boolean stateEquals(S state, S another);
    LineTokenizeResult<S, T> tokenizeLine(CharSequence line, S state, int lineIndex);
    List<Span> generateSpansForLine(LineTokenizeResult<S, T> tokens);
    void onAbandonState(S state);
    void onAddState(S state);
    class LineTokenizeResult<S_, T_> {
        public S_ state;
        public List<T_> tokens;
        public List<Span> spans;
        public LineTokenizeResult(@NonNull S_ state, @Nullable List<T_> tokens) {
            this.state = state;
            this.tokens = tokens;
        }
        public LineTokenizeResult(@NonNull S_ state, @Nullable List<T_> tokens, @Nullable List<Span> spans) {
            this.state = state;
            this.tokens = tokens;
            this.spans = spans;
        }
        protected LineTokenizeResult<S_, T_> clearSpans() {
            spans = null;
            return this;
        }
    }
}
