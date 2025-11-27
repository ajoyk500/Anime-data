package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;
import static com.github.git24j.core.GitException.ErrorCode.ITEROVER;
import static com.github.git24j.core.Internals.BArrCallback;

public class Revwalk extends CAutoReleasable {
    static native int jniAddHideCb(long walk, BArrCallback hideCb, AtomicLong outPayload);
    static native void jniFree(long walk);
    static native void jniFreeHideCb(long payloadPtr);
    static native int jniHide(long walk, Oid commitId);
    static native int jniHideGlob(long walk, String glob);
    static native int jniHideHead(long walk);
    static native int jniHideRef(long walk, String refname);
    static native int jniNew(AtomicLong out, long repoPtr);
    static native int jniNext(Oid out, long walk);
    static native int jniPush(long walk, Oid id);
    static native int jniPushGlob(long walk, String glob);
    static native int jniPushHead(long walk);
    static native int jniPushRange(long walk, String range);
    static native int jniPushRef(long walk, String refname);
    static native long jniRepository(long walk);
    static native void jniReset(long walker);
    static native void jniSimplifyFirstParent(long walk);
    static native void jniSorting(long walk, int sortMode);
    protected Revwalk(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Nonnull
    public static Revwalk create(@Nonnull Repository repo) {
        Revwalk out = new Revwalk(true, 0);
        Error.throwIfNeeded(jniNew(out._rawPtr, repo.getRawPointer()));
        return out;
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public void hide(@Nonnull Oid commitId) {
        Error.throwIfNeeded(jniHide(getRawPointer(), commitId));
    }
    public void hideGlob(@Nonnull String glob) {
        Error.throwIfNeeded(jniHideGlob(getRawPointer(), glob));
    }
    public void hideHead() {
        Error.throwIfNeeded(jniHideHead(getRawPointer()));
    }
    public void hideRef(@Nonnull String refname) {
        Error.throwIfNeeded(jniHideRef(getRawPointer(), refname));
    }
    @CheckForNull
    public Oid next() {
        Oid oid = new Oid();
        int r = jniNext(oid, getRawPointer());
        if (ITEROVER.getCode() == r) {
            return null;
        }
        Error.throwIfNeeded(r);
        return oid;
    }
    public void push(@Nonnull Oid id) {
        Error.throwIfNeeded(jniPush(getRawPointer(), id));
    }
    public void pushGlob(@Nonnull String glob) {
        Error.throwIfNeeded(jniPushGlob(getRawPointer(), glob));
    }
    public void pushHead() {
        Error.throwIfNeeded(jniPushHead(getRawPointer()));
    }
    public void pushRange(@Nonnull String range) {
        Error.throwIfNeeded(jniPushRange(getRawPointer(), range));
    }
    public void pushRef(@Nonnull String refname) {
        Error.throwIfNeeded(jniPushRef(getRawPointer(), refname));
    }
    @CheckForNull
    public Repository repository() {
        long ptr = jniRepository(getRawPointer());
        if (ptr <= 0) {
            return null;
        }
        return new Repository(ptr);
    }
    public void reset() {
        jniReset(getRawPointer());
    }
    public void simplifyFirstParent() {
        jniSimplifyFirstParent(getRawPointer());
    }
    public void sorting(@Nullable EnumSet<SortT> sortMode) {
        jniSorting(getRawPointer(), IBitEnum.bitOrAll(sortMode));
    }
    public void addHideCb(@Nullable HideCb callback) {
        HideCbAdapter adapter = callback == null ? null : new HideCbAdapter(false, 0, callback);
        Error.throwIfNeeded(
                jniAddHideCb(getRawPointer(), adapter, callback == null ? null : adapter._rawPtr));
    }
    @FunctionalInterface
    public interface HideCb {
        int accept(Oid oid);
    }
    public static class HideCbAdapter extends CAutoReleasable implements BArrCallback {
        private final HideCb _hideCb;
        protected HideCbAdapter(boolean isWeak, long rawPtr, HideCb hideCb) {
            super(isWeak, rawPtr);
            _hideCb = hideCb;
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniFreeHideCb(cPtr);
        }
        @Override
        public int accept(byte[] rawid) {
            return _hideCb.accept(Oid.of(rawid));
        }
    }
}
