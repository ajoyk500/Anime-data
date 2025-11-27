
package io.github.rosemoe.sora.lang;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.TextRange;

public interface QuickQuoteHandler {
    @NonNull
    HandleResult onHandleTyping(@NonNull String candidateCharacter, @NonNull Content text, @NonNull TextRange cursor, @Nullable Styles style);
    class HandleResult {
        public final static HandleResult NOT_CONSUMED = new HandleResult(false, null);
        private boolean consumed;
        private TextRange newCursorRange;
        public HandleResult(boolean consumed, TextRange newCursorRange) {
            this.consumed = consumed;
            this.newCursorRange = newCursorRange;
        }
        public boolean isConsumed() {
            return consumed;
        }
        public void setConsumed(boolean consumed) {
            this.consumed = consumed;
        }
        public TextRange getNewCursorRange() {
            return newCursorRange;
        }
        public void setNewCursorRange(TextRange newCursorRange) {
            this.newCursorRange = newCursorRange;
        }
    }
}
