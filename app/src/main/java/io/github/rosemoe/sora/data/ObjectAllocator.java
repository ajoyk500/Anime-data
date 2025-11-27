
package io.github.rosemoe.sora.data;

import java.util.ArrayList;
import java.util.List;
import io.github.rosemoe.sora.lang.styling.CodeBlock;

public class ObjectAllocator {
    private static final int RECYCLE_LIMIT = 1024 * 8;
    private static List<CodeBlock> codeBlocks;
    private static List<CodeBlock> tempArray;
    public static void recycleBlockLines(List<CodeBlock> src) {
        if (src == null) {
            return;
        }
        if (codeBlocks == null) {
            codeBlocks = src;
            return;
        }
        int size = codeBlocks.size();
        int sizeAnother = src.size();
        while (sizeAnother > 0 && size < RECYCLE_LIMIT) {
            size++;
            sizeAnother--;
            var obj = src.get(sizeAnother);
            obj.clear();
            codeBlocks.add(obj);
        }
        src.clear();
        synchronized (ObjectAllocator.class) {
            tempArray = src;
        }
    }
    public static List<CodeBlock> obtainList() {
        List<CodeBlock> temp = null;
        synchronized (ObjectAllocator.class) {
            temp = tempArray;
            tempArray = null;
        }
        if (temp == null) {
            temp = new ArrayList<>(128);
        }
        return temp;
    }
    public static CodeBlock obtainBlockLine() {
        return (codeBlocks == null || codeBlocks.isEmpty()) ? new CodeBlock() : codeBlocks.remove(codeBlocks.size() - 1);
    }
}
