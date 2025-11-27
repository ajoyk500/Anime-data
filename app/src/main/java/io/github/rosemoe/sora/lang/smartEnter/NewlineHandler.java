
package io.github.rosemoe.sora.lang.smartEnter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;

public interface NewlineHandler {
    boolean matchesRequirement(@NonNull Content text, @NonNull CharPosition position, @Nullable Styles style);
    @NonNull
    NewlineHandleResult handleNewline(@NonNull Content text, @NonNull CharPosition position, @Nullable Styles style, int tabSize);
}
