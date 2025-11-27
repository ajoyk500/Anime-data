
package io.github.rosemoe.sora.text.bidi;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import java.text.Bidi;
import io.github.rosemoe.sora.util.IntPair;
import io.github.rosemoe.sora.util.TemporaryCharBuffer;

public class TextBidi {
    @NonNull
    public static Directions getDirections(@NonNull CharSequence text) {
        var len = text.length();
        if (doesNotNeedBidi(text)) {
            return new Directions(new long[]{IntPair.pack(0, 0)}, len);
        }
        var chars = TemporaryCharBuffer.obtain(len);
        TextUtils.getChars(text, 0, len, chars, 0);
        var bidi = new Bidi(chars, 0, null, 0, text.length(), Bidi.DIRECTION_LEFT_TO_RIGHT);
        var runs = new long[bidi.getRunCount()];
        for (int i = 0; i < runs.length; i++) {
            runs[i] = IntPair.pack(bidi.getRunStart(i), bidi.getRunLevel(i));
        }
        TemporaryCharBuffer.recycle(chars);
        return new Directions(runs, len);
    }
    public static boolean couldAffectRtl(char c) {
        return (0x0590 <= c && c <= 0x08FF) ||  
                c == 0x200E ||  
                c == 0x200F ||  
                (0x202A <= c && c <= 0x202E) ||  
                (0x2066 <= c && c <= 0x2069) ||  
                (0xD800 <= c && c <= 0xDFFF) ||  
                (0xFB1D <= c && c <= 0xFDFF) ||  
                (0xFE70 <= c && c <= 0xFEFE);  
    }
    public static boolean doesNotNeedBidi(@NonNull CharSequence text) {
        if (text instanceof BidiRequirementChecker) {
            return !((BidiRequirementChecker) text).mayNeedBidi();
        }
        final var len = text.length();
        for (int i = 0; i < len; i++) {
            if (couldAffectRtl(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
