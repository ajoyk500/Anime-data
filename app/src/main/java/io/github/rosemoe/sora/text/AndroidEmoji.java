
package io.github.rosemoe.sora.text;

import android.icu.lang.UCharacter;
import android.icu.lang.UProperty;
import android.os.Build;
import androidx.annotation.RequiresApi;

public class AndroidEmoji {
    public static int COMBINING_ENCLOSING_KEYCAP = 0x20E3;
    public static int ZERO_WIDTH_JOINER = 0x200D;
    public static int VARIATION_SELECTOR_16 = 0xFE0F;
    public static int CANCEL_TAG = 0xE007F;
    public static boolean isRegionalIndicatorSymbol(int codePoint) {
        return 0x1F1E6 <= codePoint && codePoint <= 0x1F1FF;
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static boolean isEmojiModifier(int codePoint) {
        return UCharacter.hasBinaryProperty(codePoint, UProperty.EMOJI_MODIFIER);
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static boolean isEmojiModifierBase(int c) {
        if (c == 0x1F91D || c == 0x1F93C) {
            return true;
        }
        return UCharacter.hasBinaryProperty(c, UProperty.EMOJI_MODIFIER_BASE);
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static boolean isEmoji(int codePoint) {
        return UCharacter.hasBinaryProperty(codePoint, UProperty.EMOJI);
    }
    public static boolean isKeycapBase(int codePoint) {
        return ('0' <= codePoint && codePoint <= '9') || codePoint == '#' || codePoint == '*';
    }
    public static boolean isTagSpecChar(int codePoint) {
        return 0xE0020 <= codePoint && codePoint <= 0xE007E;
    }
}
