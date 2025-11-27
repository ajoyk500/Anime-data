
package org.eclipse.tm4e.core.internal.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.internal.oniguruma.OnigCaptureIndex;

public final class RegexSource {
    private static final Pattern CAPTURING_REGEX_SOURCE = Pattern
            .compile("\\$(\\d+)|\\$\\{(\\d+):/(downcase|upcase)\\}");
    public static String escapeRegExpCharacters(final CharSequence value) {
        final int valueLen = value.length();
        final var sb = new StringBuilder(valueLen);
        for (int i = 0; i < valueLen; i++) {
            final char ch = value.charAt(i);
            switch (ch) {
                case '-', '\\', '{', '}', '*', '+', '?', '|', '^', '$', '.', ',', '[', ']', '(', ')', '#':
                    sb.append('\\');
            }
            sb.append(ch);
        }
        return sb.toString();
    }
    public static boolean hasCaptures(@Nullable final CharSequence regexSource) {
        if (regexSource == null) {
            return false;
        }
        return CAPTURING_REGEX_SOURCE.matcher(regexSource).find();
    }
    public static String replaceCaptures(final CharSequence regexSource, final CharSequence captureSource,
                                         final OnigCaptureIndex[] captureIndices) {
        final Matcher m = CAPTURING_REGEX_SOURCE.matcher(regexSource);
        final var result = new StringBuffer();
        while (m.find()) {
            final String match = m.group();
            final String replacement = getReplacement(match, captureSource, captureIndices);
            m.appendReplacement(result, replacement);
        }
        m.appendTail(result);
        return result.toString();
    }
    private static String getReplacement(final String match, final CharSequence captureSource, final OnigCaptureIndex[] captureIndices) {
        final int index;
        final String command;
        final int doublePointIndex = match.indexOf(':');
        if (doublePointIndex != -1) {
            index = Integer.parseInt(match.substring(2, doublePointIndex));
            command = match.substring(doublePointIndex + 2, match.length() - 1);
        } else {
            index = Integer.parseInt(match.substring(1));
            command = null;
        }
        final OnigCaptureIndex capture = captureIndices.length > index ? captureIndices[index] : null;
        if (capture != null) {
            var result = captureSource.subSequence(capture.start, capture.end);
            while (!(result.length() < 1) && result.charAt(0) == '.') {
                result = result.subSequence(1, result.length());
            }
            if ("downcase".equals(command)) {
                return result.toString().toLowerCase();
            }
            if ("upcase".equals(command)) {
                return result.toString().toUpperCase();
            }
            return result.toString();
        }
        return match;
    }
    private RegexSource() {
    }
}
