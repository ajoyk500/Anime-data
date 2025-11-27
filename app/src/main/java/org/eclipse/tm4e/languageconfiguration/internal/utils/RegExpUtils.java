
package org.eclipse.tm4e.languageconfiguration.internal.utils;

import java.util.regex.Pattern;
import org.eclipse.jdt.annotation.Nullable;
import io.github.rosemoe.sora.util.Logger;

public final class RegExpUtils {
    private static final Logger log = Logger.instance(RegExpUtils.class.getName());
    public static String escapeRegExpCharacters(final String value) {
        return value.replaceAll("[\\-\\\\\\{\\}\\*\\+\\?\\|\\^\\$\\.\\[\\]\\(\\)\\#]", "\\\\$0"); 
    }
    @Nullable
    public static Pattern create(final String regex) {
        try {
            return Pattern.compile(regex);
        } catch (final Exception ex) {
            log.e("Failed to parse pattern: " + regex, ex);
            return null;
        }
    }
    private RegExpUtils() {
    }
}
