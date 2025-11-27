
package io.github.rosemoe.sora.lang.brackets;

import android.util.SparseIntArray;
import androidx.annotation.NonNull;
import io.github.rosemoe.sora.text.Content;

public class SimpleBracketsCollector implements BracketsProvider {
    private final SparseIntArray mapping;
    public SimpleBracketsCollector() {
        mapping = new SparseIntArray();
    }
    public void add(int start, int end) {
        mapping.put(start + 1, end + 1);
        mapping.put(end + 1, start + 1);
    }
    public void clear() {
        mapping.clear();
    }
    private PairedBracket getForIndex(int index) {
        int another = mapping.get(index + 1) - 1;
        if (another > index) {
            int tmp = index;
            index = another;
            another = tmp;
        }
        if (another != -1) {
            return new PairedBracket(index, another);
        }
        return null;
    }
    @Override
    public PairedBracket getPairedBracketAt(@NonNull Content text, int index) {
        var res = index - 1 >= 0 ? getForIndex(index - 1) : null;
        if (res == null) {
            res = getForIndex(index);
        }
        return res;
    }
}
