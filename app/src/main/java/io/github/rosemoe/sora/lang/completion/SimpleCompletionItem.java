
package io.github.rosemoe.sora.lang.completion;

import android.graphics.drawable.Drawable;

public class SimpleCompletionItem extends CompletionItem {
    public String commitText;
    public SimpleCompletionItem(int prefixLength, String commitText) {
        this(commitText, prefixLength, commitText);
    }
    public SimpleCompletionItem(CharSequence label, int prefixLength, String commitText) {
        this(label, null, prefixLength, commitText);
    }
    public SimpleCompletionItem(CharSequence label, CharSequence desc, int prefixLength, String commitText) {
        this(label, desc, null, prefixLength, commitText);
    }
    public SimpleCompletionItem(CharSequence label, CharSequence desc, Drawable icon, int prefixLength, String commitText) {
        super(label, desc, icon);
        this.commitText = commitText;
        this.prefixLength = prefixLength;
    }
    @Override
    public SimpleCompletionItem desc(CharSequence desc) {
        super.desc(desc);
        return this;
    }
    @Override
    public SimpleCompletionItem icon(Drawable icon) {
        super.icon(icon);
        return this;
    }
    @Override
    public SimpleCompletionItem label(CharSequence label) {
        super.label(label);
        return this;
    }
    @Override
    public SimpleCompletionItem kind(CompletionItemKind kind) {
        super.kind(kind);
        if (this.icon == null) {
            icon = SimpleCompletionIconDrawer.draw(kind);
        }
        return this;
    }
    public SimpleCompletionItem commit(int prefixLength, String commitText) {
        this.prefixLength = prefixLength;
        this.commitText = commitText;
        return this;
    }
}
