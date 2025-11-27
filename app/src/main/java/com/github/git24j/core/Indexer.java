package com.github.git24j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicLong;

public class Indexer extends CAutoReleasable {
    static native int jniAppend(long idx, byte[] data, int size, long stats);
    static native int jniCommit(long idx, long stats);
    static native void jniFree(long idx);
    static native byte[] jniHash(long idx);
    static native int jniNew(AtomicLong out, String path, int mode, long odb, long opts);
    static native void jniOptionsFree(long optsPtr);
    static native int jniOptionsNew(AtomicLong outOpts, int version, Internals.JCallback jCb);
    static native int jniProgressGetIndexedDeltas(long progressPtr);
    static native int jniProgressGetIndexedObjects(long progressPtr);
    static native int jniProgressGetLocalObjects(long progressPtr);
    static native int jniProgressGetReceivedBytes(long progressPtr);
    static native int jniProgressGetReceivedObjects(long progressPtr);
    static native int jniProgressGetTotalDeltas(long progressPtr);
    static native int jniProgressGetTotalObjects(long progressPtr);
    static native long jniProgressNew();
    protected Indexer(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Nonnull
    public static Indexer create(
            @Nonnull String path, int mode, @Nonnull Odb odb, @Nullable Options opts) {
        Indexer out = new Indexer(false, 0);
        int e =
                jniNew(
                        out._rawPtr,
                        path,
                        mode,
                        odb.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer());
        Error.throwIfNeeded(e);
        return out;
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public Progress append(byte[] data) {
        Progress progress = Progress.create();
        int e = jniAppend(getRawPointer(), data, data.length, progress.getRawPointer());
        Error.throwIfNeeded(e);
        return progress;
    }
    public Progress commit() {
        Progress stats = Progress.create();
        int e = jniCommit(getRawPointer(), stats.getRawPointer());
        Error.throwIfNeeded(e);
        return stats;
    }
    @Nonnull
    public Oid hash() {
        return Oid.of(jniHash(getRawPointer()));
    }
    @FunctionalInterface
    public interface ProgressCb {
        int accept(Progress stats);
    }
    public static class Options extends CAutoReleasable {
        public static final int VERSION = 1;
        private ProgressCb _progressCb;
        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }
        public Options create(int version) {
            Options out = new Options(false, 0);
            jniOptionsNew(
                    out._rawPtr,
                    version,
                    progressPtr -> {
                        if (out._progressCb != null) {
                            return out._progressCb.accept(new Progress(true, progressPtr));
                        }
                        return 0;
                    });
            return out;
        }
        public void setProgressCb(ProgressCb progressCb) {
            _progressCb = progressCb;
        }
    }
    public static class Progress extends CAutoReleasable {
        protected Progress(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static Progress create() {
            long ptr = jniProgressNew();
            return new Progress(false, ptr);
        }
        @Override
        protected void freeOnce(long cPtr) {}
        public int getTotalObjects() {
            return jniProgressGetTotalObjects(getRawPointer());
        }
        public int getIndexedObjects() {
            return jniProgressGetIndexedObjects(getRawPointer());
        }
        public int getReceivedObjects() {
            return jniProgressGetReceivedObjects(getRawPointer());
        }
        public int getLocalObjects() {
            return jniProgressGetLocalObjects(getRawPointer());
        }
        public int getTotalDeltas() {
            return jniProgressGetTotalDeltas(getRawPointer());
        }
        public int getIndexedDeltas() {
            return jniProgressGetIndexedDeltas(getRawPointer());
        }
        public int getReceivedBytes() {
            return jniProgressGetReceivedBytes(getRawPointer());
        }
    }
}
