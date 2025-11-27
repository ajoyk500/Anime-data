package com.github.git24j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicLong;

public class Refdb extends CAutoReleasable {
    static native int jniCompress(long refdb);
    static native void jniFree(long refdb);
    static native int jniOpen(AtomicLong out, long repoPtr);
    protected Refdb(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Nullable
    public static Refdb open(@Nonnull Repository repository) {
        Refdb out = new Refdb(false, 0);
        Error.throwIfNeeded(jniOpen(out._rawPtr, repository.getRawPointer()));
        return out.getRawPointer() == 0 ? null : out;
    }
    public void compress() {
        Error.throwIfNeeded(jniCompress(getRawPointer()));
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
}
