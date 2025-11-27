
package io.github.rosemoe.sora.lang.completion;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;

@SuppressWarnings("CanBeFinal")
public abstract class CompletionItem {
    @Nullable
    public Drawable icon;
    public CharSequence label;
    public CharSequence desc;
    @Nullable
    public CompletionItemKind kind;
    public int prefixLength = 0;
    @Nullable
    public String sortText;
    @Nullable
    protected Object extra;
    public CompletionItem(CharSequence label) {
        this(label, null);
    }
    public CompletionItem(CharSequence label, CharSequence desc) {
        this(label, desc, null);
    }
    public CompletionItem(CharSequence label, CharSequence desc, Drawable icon) {
        this.label = label;
        this.desc = desc;
        this.icon = icon;
    }
    public CompletionItem label(CharSequence label) {
        this.label = label;
        return this;
    }
    public CompletionItem desc(CharSequence desc) {
        this.desc = desc;
        return this;
    }
    public CompletionItem kind(CompletionItemKind kind) {
        this.kind = kind;
        return this;
    }
    public CompletionItem icon(Drawable icon) {
        this.icon = icon;
        return this;
    }
}
