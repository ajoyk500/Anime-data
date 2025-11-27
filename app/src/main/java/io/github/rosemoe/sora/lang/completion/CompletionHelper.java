
package io.github.rosemoe.sora.lang.completion;

import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;

public class CompletionHelper {
    public static String computePrefix(ContentReference ref, CharPosition pos, PrefixChecker checker) {
        int begin = pos.column;
        var line = ref.getLine(pos.line);
        for (; begin > 0; begin--) {
            if (!checker.check(line.charAt(begin - 1))) {
                break;
            }
        }
        return line.substring(begin, pos.column);
    }
    public static boolean checkCancelled() {
            return true;
    }
    public interface PrefixChecker {
        boolean check(char ch);
    }
}
