
package io.github.rosemoe.sora.text;

import android.icu.text.BreakIterator;
import android.os.Build;
import androidx.annotation.NonNull;
import io.github.rosemoe.sora.util.IntPair;
import io.github.rosemoe.sora.util.MyCharacter;

public class ICUUtils {
    public static long getWordRange(@NonNull CharSequence text, int offset, boolean useIcu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && useIcu) {
            var itr = BreakIterator.getWordInstance();
            itr.setText(new CharSequenceIterator(text));
            int end = itr.following(offset);
            int start = itr.previous();
            if (offset >= start && offset <= end) {
                return IntPair.pack(start, end);
            } else {
                return getWordRangeFallback(text, offset);
            }
        } else {
            return getWordRangeFallback(text, offset);
        }
    }
    public static long getWordRangeFallback(@NonNull CharSequence text, int offset) {
        int start = offset;
        int end = offset;
        while (end < text.length() && MyCharacter.isJavaIdentifierPart(text.charAt(end))) {
            end++;
        }
        if (end > offset) {
            while (start > 0 && MyCharacter.isJavaIdentifierPart(text.charAt(start - 1))) {
                start--;
            }
        }
        return IntPair.pack(start, end);
    }
}
