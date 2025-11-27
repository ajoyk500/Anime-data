package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;
import static com.github.git24j.core.Internals.JJJCallback;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Patch extends CAutoReleasable {
    static native void jniFree(long patch);
    static native int jniFromBlobAndBuffer(
            AtomicLong out,
            long oldBlob,
            String oldAsPath,
            byte[] buffer,
            int bufferLen,
            String bufferAsPath,
            long opts);
    static native int jniFromBlobs(
            AtomicLong out,
            long oldBlob,
            String oldAsPath,
            long newBlob,
            String newAsPath,
            long opts);
    static native int jniFromBuffers(
            AtomicLong out,
            byte[] oldBuffer,
            int oldLen,
            String oldAsPath,
            byte[] newBuffer,
            int newLen,
            String newAsPath,
            long opts);
    static native int jniFromDiff(AtomicLong out, long diff, int idx);
    static native long jniGetDelta(long patch);
    static native int jniGetHunk(
            AtomicLong out, AtomicInteger linesInHunk, long patch, int hunkIdx);
    static native int jniGetLineInHunk(AtomicLong out, long patch, int hunkIdx, int lineOfHunk);
    static native int jniLineStats(
            AtomicInteger totalContext,
            AtomicInteger totalAdditions,
            AtomicInteger totalDeletions,
            long patch);
    static native int jniNumHunks(long patch);
    static native int jniNumLinesInHunk(long patch, int hunkIdx);
    static native int jniPrint(long patch, JJJCallback printCb);
    static native int jniSize(
            long patch, int includeContext, int includeHunkHeaders, int includeFileHeaders);
    static native int jniToBuf(Buf out, long patch);
    protected Patch(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @CheckForNull
    public static Patch fromDiff(@Nonnull Diff diff, int idx) {
        Patch out = new Patch(false, 0);
        Error.throwIfNeeded(jniFromDiff(out._rawPtr, diff.getRawPointer(), idx));
        if (out._rawPtr.get() == 0) {
            return null;
        }
        return out;
    }
    @Nonnull
    public static Patch fromBlobs(
            @Nullable Blob oldBlob,
            @Nullable String oldAsPath,
            @Nullable Blob newBlob,
            @Nullable String newAsPath,
            @Nullable Diff.Options opts) {
        Patch out = new Patch(false, 0);
        Error.throwIfNeeded(
                jniFromBlobs(
                        out._rawPtr,
                        oldBlob == null ? 0 : oldBlob.getRawPointer(),
                        oldAsPath,
                        newBlob == null ? 0 : newBlob.getRawPointer(),
                        newAsPath,
                        opts == null ? 0 : opts.getRawPointer()));
        if (out._rawPtr.get() == 0) {
            return null;
        }
        return out;
    }
    @Nullable
    public static Patch fromBlobAndBuffer(
            @Nullable Blob oldBlob,
            @Nullable String oldAsPath,
            @Nullable byte[] buffer,
            @Nullable String bufferAsPath,
            @Nullable Diff.Options opts) {
        Patch out = new Patch(false, 0);
        Error.throwIfNeeded(
                jniFromBlobAndBuffer(
                        out._rawPtr,
                        oldBlob == null ? 0 : oldBlob.getRawPointer(),
                        oldAsPath,
                        buffer,
                        buffer == null ? 0 : buffer.length,
                        bufferAsPath,
                        opts == null ? 0 : opts.getRawPointer()));
        if (out._rawPtr.get() == 0) {
            return null;
        }
        return out;
    }
    @Nullable
    public static Patch fromBuffers(
            @Nullable byte[] oldBuffer,
            @Nullable String oldAsPath,
            @Nullable byte[] newBuffer,
            @Nullable String newAsPath,
            @Nullable Diff.Options opts) {
        Patch out = new Patch(false, 0);
        Error.throwIfNeeded(
                jniFromBuffers(
                        out._rawPtr,
                        oldBuffer,
                        oldBuffer == null ? 0 : oldBuffer.length,
                        oldAsPath,
                        newBuffer,
                        newBuffer == null ? 0 : newBuffer.length,
                        newAsPath,
                        opts == null ? 0 : opts.getRawPointer()));
        if (out._rawPtr.get() == 0) {
            return null;
        }
        return out;
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    @Nonnull
    public Diff.Delta getDelta() {
        long ptr = jniGetDelta(getRawPointer());
        return new Diff.Delta(ptr);
    }
    public int numHunks() {
        return jniNumHunks(getRawPointer());
    }
    @Nonnull
    public LineStats lineStats() {
        AtomicInteger totalContext = new AtomicInteger();
        AtomicInteger totalAdditions = new AtomicInteger();
        AtomicInteger totalDeletions = new AtomicInteger();
        Error.throwIfNeeded(
                jniLineStats(totalContext, totalAdditions, totalDeletions, getRawPointer()));
        return new LineStats(totalContext.get(), totalAdditions.get(), totalDeletions.get());
    }
    @Nullable
    public HunkInfo getHunk(int hunkIdx) {
        AtomicLong outHunk = new AtomicLong();
        AtomicInteger linesInHunk = new AtomicInteger();
        int e = jniGetHunk(outHunk, linesInHunk, getRawPointer(), hunkIdx);
        if (ENOTFOUND.getCode() == e || outHunk.get() == 0) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new HunkInfo(new Diff.Hunk(outHunk.get()), linesInHunk.get());
    }
    public int numLinesInHunk(int hunkIdx) {
        int r = jniNumLinesInHunk(getRawPointer(), hunkIdx);
        Error.throwIfNeeded(r);
        return r;
    }
    @Nullable
    public Diff.Line getLineInHunk(int hunkIdx, int lineOfHunk) {
        AtomicLong out = new AtomicLong();
        int e = jniGetLineInHunk(out, getRawPointer(), hunkIdx, lineOfHunk);
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Diff.Line(out.get());
    }
    public int size(
            boolean includeContext, boolean includeHunkHeaders, boolean includeFileHeaders) {
        return jniSize(
                getRawPointer(),
                includeContext ? 1 : 0,
                includeHunkHeaders ? 1 : 0,
                includeFileHeaders ? 1 : 0);
    }
    public int print(@Nonnull Diff.LineCb printCb) {
        int e =
                jniPrint(
                        getRawPointer(),
                        (delta, hunk, line) ->
                                printCb.accept(
                                        Diff.Delta.of(delta),
                                        Diff.Hunk.of(hunk),
                                        Diff.Line.of(line)));
        Error.throwIfNeeded(e);
        return e;
    }
    @Nonnull
    public String toBuf() {
        Buf buf = new Buf();
        Error.throwIfNeeded(jniToBuf(buf, getRawPointer()));
        return buf.getString().orElse("");
    }
    public static class LineStats {
        private final int totalContext;
        private final int totalAdditions;
        private final int totalDeletions;
        public LineStats(int totalContext, int totalAdditions, int totalDeletions) {
            this.totalContext = totalContext;
            this.totalAdditions = totalAdditions;
            this.totalDeletions = totalDeletions;
        }
        public int getTotalContext() {
            return totalContext;
        }
        public int getTotalAdditions() {
            return totalAdditions;
        }
        public int getTotalDeletions() {
            return totalDeletions;
        }
    }
    public static class HunkInfo {
        private final Diff.Hunk _hunk;
        private final int _lines;
        public HunkInfo(Diff.Hunk hunk, int lines) {
            _hunk = hunk;
            _lines = lines;
        }
        @Nonnull
        public Diff.Hunk getHunk() {
            return _hunk;
        }
        public int getLines() {
            return _lines;
        }
    }
}
