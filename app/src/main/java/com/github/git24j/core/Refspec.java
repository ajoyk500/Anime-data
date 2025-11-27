package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Refspec extends CAutoReleasable {
    static native int jniDirection(long spec);
    static native String jniDst(long refspec);
    static native int jniDstMatches(long refspec, String refname);
    static native int jniForce(long refspec);
    static native void jniFree(long refspec);
    static native int jniParse(AtomicLong refspec, String input, int isFetch);
    static native int jniRtransform(Buf out, long spec, String name);
    static native String jniSrc(long refspec);
    static native int jniSrcMatches(long refspec, String refname);
    static native String jniString(long refspec);
    static native int jniTransform(Buf out, long spec, String name);
    protected Refspec(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Nonnull
    public static Refspec parse(@Nonnull String input, boolean isFetch) {
        Refspec out = new Refspec(false, 0);
        Error.throwIfNeeded(jniParse(out._rawPtr, input, isFetch ? 1 : 0));
        return out;
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public String src() {
        return jniSrc(getRawPointer());
    }
    public String dst() {
        return jniDst(getRawPointer());
    }
    public String getString() {
        return jniString(getRawPointer());
    }
    public boolean force() {
        return jniForce(getRawPointer()) == 1;
    }
    @Nonnull
    public Remote.Direction direction() {
        return jniDirection(getRawPointer()) == 0 ? Remote.Direction.FETCH : Remote.Direction.PUSH;
    }
    public boolean srcMatches(@Nonnull String refname) {
        return jniSrcMatches(getRawPointer(), refname) == 1;
    }
    public boolean dstMatches(@Nonnull String refname) {
        return jniDstMatches(getRawPointer(), refname) == 1;
    }
    @Nullable
    public String transform(@Nonnull String name) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniTransform(out, getRawPointer(), name));
        return out.getString().orElse(null);
    }
    @Nullable
    public String rtransform(@Nonnull String name) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniRtransform(out, getRawPointer(), name));
        return out.getString().orElse(null);
    }
}
