
package io.github.rosemoe.sora.lang.brackets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.github.rosemoe.sora.text.Content;

public interface BracketsProvider {
    @Nullable
    PairedBracket getPairedBracketAt(@NonNull Content text, int index);
}
