
package io.github.rosemoe.sora.lang.analysis;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentReference;

public interface AnalyzeManager {
    @Deprecated
    void setReceiver(@Nullable StyleReceiver receiver);
    void reset(@NonNull ContentReference content, @NonNull Bundle extraArguments, @Nullable StyleReceiver receiver);
    void insert(@NonNull CharPosition start, @NonNull CharPosition end, @NonNull CharSequence insertedContent, @Nullable StyleReceiver receiver);
    void delete(@NonNull CharPosition start, @NonNull CharPosition end, @NonNull CharSequence deletedContent, @Nullable StyleReceiver receiver);
    void rerun(@Nullable StyleReceiver receiver);
    void destroy();
}
