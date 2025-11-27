
package io.github.rosemoe.sora.text;

import android.icu.lang.UCharacter;
import android.icu.lang.UProperty;
import android.os.Build;
import androidx.annotation.RequiresApi;

public class TextUtilsP {
    private static final int LINE_FEED = 0x0A;
    private static final int CARRIAGE_RETURN = 0x0D;
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static boolean isVariationSelector(int codepoint) {
        return UCharacter.hasBinaryProperty(codepoint, UProperty.VARIATION_SELECTOR);
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static int getOffsetForBackspaceKey(CharSequence text, int offset) {
        if (offset <= 1) {
            return 0;
        }
        final int STATE_START = 0;
        final int STATE_LF = 1;
        final int STATE_BEFORE_KEYCAP = 2;
        final int STATE_BEFORE_VS_AND_KEYCAP = 3;
        final int STATE_BEFORE_EMOJI_MODIFIER = 4;
        final int STATE_BEFORE_VS_AND_EMOJI_MODIFIER = 5;
        final int STATE_BEFORE_VS = 6;
        final int STATE_BEFORE_EMOJI = 7;
        final int STATE_BEFORE_ZWJ = 8;
        final int STATE_BEFORE_VS_AND_ZWJ = 9;
        final int STATE_ODD_NUMBERED_RIS = 10;
        final int STATE_EVEN_NUMBERED_RIS = 11;
        final int STATE_IN_TAG_SEQUENCE = 12;
        final int STATE_FINISHED = 13;
        int deleteCharCount = 0;  
        int lastSeenVSCharCount = 0;  
        int state = STATE_START;
        int tmpOffset = offset;
        do {
            final int codePoint = Character.codePointBefore(text, tmpOffset);
            tmpOffset -= Character.charCount(codePoint);
            switch (state) {
                case STATE_START:
                    deleteCharCount = Character.charCount(codePoint);
                    if (codePoint == LINE_FEED) {
                        state = STATE_LF;
                    } else if (isVariationSelector(codePoint)) {
                        state = STATE_BEFORE_VS;
                    } else if (AndroidEmoji.isRegionalIndicatorSymbol(codePoint)) {
                        state = STATE_ODD_NUMBERED_RIS;
                    } else if (AndroidEmoji.isEmojiModifier(codePoint)) {
                        state = STATE_BEFORE_EMOJI_MODIFIER;
                    } else if (codePoint == AndroidEmoji.COMBINING_ENCLOSING_KEYCAP) {
                        state = STATE_BEFORE_KEYCAP;
                    } else if (AndroidEmoji.isEmoji(codePoint)) {
                        state = STATE_BEFORE_EMOJI;
                    } else if (codePoint == AndroidEmoji.CANCEL_TAG) {
                        state = STATE_IN_TAG_SEQUENCE;
                    } else {
                        state = STATE_FINISHED;
                    }
                    break;
                case STATE_LF:
                    if (codePoint == CARRIAGE_RETURN) {
                        ++deleteCharCount;
                    }
                    state = STATE_FINISHED;
                    break;
                case STATE_ODD_NUMBERED_RIS:
                    if (AndroidEmoji.isRegionalIndicatorSymbol(codePoint)) {
                        deleteCharCount += 2; 
                        state = STATE_EVEN_NUMBERED_RIS;
                    } else {
                        state = STATE_FINISHED;
                    }
                    break;
                case STATE_EVEN_NUMBERED_RIS:
                    if (AndroidEmoji.isRegionalIndicatorSymbol(codePoint)) {
                        deleteCharCount -= 2; 
                        state = STATE_ODD_NUMBERED_RIS;
                    } else {
                        state = STATE_FINISHED;
                    }
                    break;
                case STATE_BEFORE_KEYCAP:
                    if (isVariationSelector(codePoint)) {
                        lastSeenVSCharCount = Character.charCount(codePoint);
                        state = STATE_BEFORE_VS_AND_KEYCAP;
                        break;
                    }
                    if (AndroidEmoji.isKeycapBase(codePoint)) {
                        deleteCharCount += Character.charCount(codePoint);
                    }
                    state = STATE_FINISHED;
                    break;
                case STATE_BEFORE_VS_AND_KEYCAP:
                    if (AndroidEmoji.isKeycapBase(codePoint)) {
                        deleteCharCount += lastSeenVSCharCount + Character.charCount(codePoint);
                    }
                    state = STATE_FINISHED;
                    break;
                case STATE_BEFORE_EMOJI_MODIFIER:
                    if (isVariationSelector(codePoint)) {
                        lastSeenVSCharCount = Character.charCount(codePoint);
                        state = STATE_BEFORE_VS_AND_EMOJI_MODIFIER;
                        break;
                    } else if (AndroidEmoji.isEmojiModifierBase(codePoint)) {
                        deleteCharCount += Character.charCount(codePoint);
                        state = STATE_BEFORE_EMOJI;
                        break;
                    }
                    state = STATE_FINISHED;
                    break;
                case STATE_BEFORE_VS_AND_EMOJI_MODIFIER:
                    if (AndroidEmoji.isEmojiModifierBase(codePoint)) {
                        deleteCharCount += lastSeenVSCharCount + Character.charCount(codePoint);
                    }
                    state = STATE_FINISHED;
                    break;
                case STATE_BEFORE_VS:
                    if (AndroidEmoji.isEmoji(codePoint)) {
                        deleteCharCount += Character.charCount(codePoint);
                        state = STATE_BEFORE_EMOJI;
                        break;
                    }
                    if (!isVariationSelector(codePoint) &&
                            UCharacter.getCombiningClass(codePoint) == 0) {
                        deleteCharCount += Character.charCount(codePoint);
                    }
                    state = STATE_FINISHED;
                    break;
                case STATE_BEFORE_EMOJI:
                    if (codePoint == AndroidEmoji.ZERO_WIDTH_JOINER) {
                        state = STATE_BEFORE_ZWJ;
                    } else {
                        state = STATE_FINISHED;
                    }
                    break;
                case STATE_BEFORE_ZWJ:
                    if (AndroidEmoji.isEmoji(codePoint)) {
                        deleteCharCount += Character.charCount(codePoint) + 1;  
                        state = AndroidEmoji.isEmojiModifier(codePoint) ?
                                STATE_BEFORE_EMOJI_MODIFIER : STATE_BEFORE_EMOJI;
                    } else if (isVariationSelector(codePoint)) {
                        lastSeenVSCharCount = Character.charCount(codePoint);
                        state = STATE_BEFORE_VS_AND_ZWJ;
                    } else {
                        state = STATE_FINISHED;
                    }
                    break;
                case STATE_BEFORE_VS_AND_ZWJ:
                    if (AndroidEmoji.isEmoji(codePoint)) {
                        deleteCharCount += lastSeenVSCharCount + 1 + Character.charCount(codePoint);
                        lastSeenVSCharCount = 0;
                        state = STATE_BEFORE_EMOJI;
                    } else {
                        state = STATE_FINISHED;
                    }
                    break;
                case STATE_IN_TAG_SEQUENCE:
                    if (AndroidEmoji.isTagSpecChar(codePoint)) {
                        deleteCharCount += 2; 
                    } else if (AndroidEmoji.isEmoji(codePoint)) {
                        deleteCharCount += Character.charCount(codePoint);
                        state = STATE_FINISHED;
                    } else {
                        deleteCharCount = 2;  
                        state = STATE_FINISHED;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("state " + state + " is unknown");
            }
        } while (tmpOffset > 0 && state != STATE_FINISHED);
        return offset - deleteCharCount;
    }
}
