package com.github.git24j.core;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FilterList extends CAutoReleasable {
    static native int jniApplyToBlob(Buf out, long filters, long blob);
    static native int jniApplyToData(Buf out, long filters, String in);
    static native int jniApplyToFile(Buf out, long filters, long repoPtr, String path);
    static native int jniContains(long filters, String name);
    static native void jniFree(long filters);
    static native int jniLoad(
            AtomicLong filters, long repoPtr, long blob, String path, int mode, int flags);
    static native int jniStreamBlob(long filters, long blob, long target);
    static native int jniStreamData(long filters, String data, long target);
    static native int jniStreamFile(long filters, long repoPtr, String path, long target);
    protected FilterList(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @CheckForNull
    public static FilterList load(
            @Nonnull Repository repo,
            @Nullable Blob blob,
            @Nonnull String path,
            ModeT mode,
            @Nonnull EnumSet<FlagT> flags) {
        FilterList out = new FilterList(false, 0);
        int e =
                jniLoad(
                        out._rawPtr,
                        repo.getRawPointer(),
                        blob == null ? 0 : blob.getRawPointer(),
                        path,
                        mode.getBit(),
                        IBitEnum.bitOrAll(flags));
        Error.throwIfNeeded(e);
        return out;
    }
    public static boolean contains(@Nullable FilterList filters, @Nonnull String name) {
        return jniContains(filters == null ? 0 : filters.getRawPointer(), name) != 0;
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    @Nonnull
    public String applyToData(@Nonnull String in) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniApplyToData(out, getRawPointer(), in));
        return out.getString().orElse("");
    }
    @Nonnull
    public String applyToFile(@Nonnull Repository repo, @Nonnull String relativePath) {
        Buf out = new Buf();
        int e = jniApplyToFile(out, getRawPointer(), repo.getRawPointer(), relativePath);
        Error.throwIfNeeded(e);
        return out.getString().orElse("");
    }
    @Nonnull
    public String applyToBlob(@Nonnull Blob blob) {
        Buf out = new Buf();
        int e = jniApplyToBlob(out, getRawPointer(), blob.getRawPointer());
        Error.throwIfNeeded(e);
        return out.getString().orElse("");
    }
    public void streamData(@Nonnull String data, @Nonnull WriteStream target) {
        int e = jniStreamData(getRawPointer(), data, target.getRawPointer());
        Error.throwIfNeeded(e);
    }
    public void streamFile(
            @Nonnull Repository repo, @Nonnull String relativePath, @Nonnull WriteStream target) {
        int e =
                jniStreamFile(
                        getRawPointer(),
                        repo.getRawPointer(),
                        relativePath,
                        target.getRawPointer());
        Error.throwIfNeeded(e);
    }
    public void streamBlob(@Nonnull Blob blob, @Nonnull WriteStream target) {
        int e = jniStreamBlob(getRawPointer(), blob.getRawPointer(), target.getRawPointer());
        Error.throwIfNeeded(e);
    }
    public enum ModeT implements IBitEnum {
        TO_WORKTREE(0),
        SMUDGE(0),
        TO_ODB(1),
        CLEAN(1),
        ;
        private final int _bit;
        ModeT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum FlagT implements IBitEnum {
        DEFAULT(0),
        ALLOW_UNSAFE(1 << 0),
        NO_SYSTEM_ATTRIBUTES(1 << 1),
        ATTRIBUTES_FROM_HEAD(1 << 2),
        ;
        private final int _bit;
        FlagT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
}
