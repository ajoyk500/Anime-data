
package io.github.rosemoe.sora.util;


public class MutableInt {
    public int value;
    public MutableInt(int v) {
        value = v;
    }
    public int decreaseAndGet() {
        return --value;
    }
    public void increase() {
        value++;
    }
    public MutableInt copy() {
        return new MutableInt((value));
    }
}
