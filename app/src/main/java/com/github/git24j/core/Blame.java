package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Blame extends CAutoReleasable {
    static native int jniBuffer(AtomicLong out, long reference, String buffer, int bufferLen);
    static native int jniFile(AtomicLong out, long repoPtr, String path, long options);
    static native void jniFree(long blame);
    static native long jniGetHunkByindex(long blame, int index);
    static native long jniGetHunkByline(long blame, int lineno);
    static native int jniGetHunkCount(long blame);
    static native char jniHunkGetBoundary(long hunkPtr);
    static native byte[] jniHunkGetFinalCommitId(long hunkPtr);
    static native long jniHunkGetFinalSignature(long hunkPtr);
    static native int jniHunkGetFinalStartLineNumber(long hunkPtr);
    static native int jniHunkGetLinesInHunk(long hunkPtr);
    static native byte[] jniHunkGetOrigCommitId(long hunkPtr);
    static native String jniHunkGetOrigPath(long hunkPtr);
    static native long jniHunkGetOrigSignature(long hunkPtr);
    static native int jniHunkGetOrigStartLineNumber(long hunkPtr);
    static native int jniOptionsNew(AtomicLong outPtr, int version);
    protected Blame(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Nonnull
    public static Blame file(
            @Nonnull Repository repo, @Nonnull String path, @Nullable Options options) {
        Blame blame = new Blame(false, 0);
        Error.throwIfNeeded(
                jniFile(
                        blame._rawPtr,
                        repo.getRawPointer(),
                        path,
                        options == null ? 0 : options.getRawPointer()));
        return blame;
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public int getHunkCount() {
        return jniGetHunkCount(getRawPointer());
    }
    @Nullable
    public Hunk getHunkByIndex(int index) {
        long ptr = jniGetHunkByindex(getRawPointer(), index);
        return ptr == 0 ? null : new Hunk(true, ptr);
    }
    @Nullable
    public Hunk getHunkByLine(int lineno) {
        long ptr = jniGetHunkByline(getRawPointer(), lineno);
        return ptr == 0 ? null : new Hunk(true, ptr);
    }
    @Nonnull
    public Blame buffer(@Nonnull String buffer) {
        Blame out = new Blame(false, 0);
        Error.throwIfNeeded(jniBuffer(out._rawPtr, getRawPointer(), buffer, buffer.length()));
        return out;
    }
    public enum FlagT implements IBitEnum {
        NORMAL(0),
        TRACK_COPIES_SAME_FILE(1 << 0),
        TRACK_COPIES_SAME_COMMIT_MOVES(1 << 1),
        TRACK_COPIES_SAME_COMMIT_COPIES(1 << 2),
        TRACK_COPIES_ANY_COMMIT_COPIES(1 << 3),
        FIRST_PARENT(1 << 4),
        USE_MAILMAP(1 << 5),
        IGNORE_WHITESPACE(1 << 6);
        private final int _bit;
        FlagT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public static class Options extends CAutoReleasable {
        public static final int VERSION = 1;
        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        public static Options create(int version) {
            Options out = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(out._rawPtr, version));
            return out;
        }
        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }
    }
    public static class Hunk extends CAutoReleasable {
        protected Hunk(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }
        public int getLinesInHunk() {
            return jniHunkGetLinesInHunk(getRawPointer());
        }
        @CheckForNull
        public Oid getFinalCommitId() {
            byte[] rawId = jniHunkGetFinalCommitId(getRawPointer());
            return rawId == null ? null : Oid.of(rawId);
        }
        public int getFinalStartLineNumber() {
            return jniHunkGetFinalStartLineNumber(getRawPointer());
        }
        public long getFinalSignature() {
            return jniHunkGetFinalSignature(getRawPointer());
        }
        @CheckForNull
        public Oid getOrigCommitId() {
            byte[] rawId = jniHunkGetOrigCommitId(getRawPointer());
            return rawId == null ? null : Oid.of(rawId);
        }
        @CheckForNull
        public String getOrigPath() {
            return jniHunkGetOrigPath(getRawPointer());
        }
        public int getOrigStartLineNumber() {
            return jniHunkGetOrigStartLineNumber(getRawPointer());
        }
        @CheckForNull
        public Signature getOrigSignature() {
            long ptr = jniHunkGetOrigSignature(getRawPointer());
            return ptr == 0 ? null : new Signature(true, ptr);
        }
        public char getBoundary() {
            return jniHunkGetBoundary(getRawPointer());
        }
    }
}
