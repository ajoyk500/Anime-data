
package io.github.rosemoe.sora.lang.util;

import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.function.Consumer;
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.analysis.StyleReceiver;
import io.github.rosemoe.sora.text.ContentReference;

public abstract class BaseAnalyzeManager implements AnalyzeManager {
    private StyleReceiver receiver;
    private ContentReference contentRef;
    private Bundle extraArguments;
    @Override
    public void setReceiver(@Nullable StyleReceiver receiver) {
        this.receiver = receiver;
    }
    @Nullable
    public StyleReceiver getReceiver() {
        return receiver;
    }
    @Nullable
    public Bundle getExtraArguments() {
        return extraArguments;
    }
    @Nullable
    public ContentReference getContentRef() {
        return contentRef;
    }
    @Override
    @CallSuper
    public void reset(@NonNull ContentReference content, @NonNull Bundle extraArguments, @Nullable StyleReceiver receiver) {
        this.extraArguments = extraArguments;
        this.contentRef = content;
        rerun((receiver == null) ? getReceiver() : receiver);
    }
    @Override
    @CallSuper
    public void destroy() {
        this.receiver = null;
        this.contentRef = null;
        this.extraArguments = null;
    }
}
