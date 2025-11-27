
package io.github.rosemoe.sora.lang.styling.span;

import io.github.rosemoe.sora.lang.styling.color.ResolvableColor;

public class SpanExtAttrs {
    public final static int EXT_COLOR_RESOLVER = 0;
    public final static int EXT_EXTERNAL_RENDERER = 1;
    public final static int EXT_INTERACTION_INFO = 2;
    public final static int EXT_UNDERLINE_COLOR = 3;
    public static boolean checkType(int extType, SpanExt ext) {
        if (ext == null) {
            return true;
        }
        switch (extType) {
            case EXT_COLOR_RESOLVER -> {
                return ext instanceof SpanColorResolver;
            }
            case EXT_EXTERNAL_RENDERER -> {
                return ext instanceof SpanExternalRenderer;
            }
            case EXT_INTERACTION_INFO -> {
                return ext instanceof SpanInteractionInfo;
            }
            case EXT_UNDERLINE_COLOR -> {
                return ext instanceof ResolvableColor;
            }
        }
        return true;
    }
}
