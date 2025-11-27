
package io.github.rosemoe.sora.langs.textmate.folding;


public class PreviousRegion {
    public int indent;
    public int endAbove;
    public int line;
    public PreviousRegion(int indent, int endAbove, int line) {
        this.indent = indent;
        this.endAbove = endAbove;
        this.line = line;
    }
}
