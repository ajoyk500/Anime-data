
package io.github.rosemoe.sora.text;

import androidx.annotation.NonNull;
import io.github.rosemoe.sora.annotations.UnsupportedUserUsage;

public interface ContentListener {
    void beforeReplace(@NonNull Content content);
    void afterInsert(@NonNull Content content, int startLine, int startColumn, int endLine, int endColumn, @NonNull CharSequence insertedContent);
    void afterDelete(@NonNull Content content, int startLine, int startColumn, int endLine, int endColumn, @NonNull CharSequence deletedContent);
    @UnsupportedUserUsage
    default void beforeModification(@NonNull Content content) {
    }
}
