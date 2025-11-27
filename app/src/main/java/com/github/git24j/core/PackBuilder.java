package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PackBuilder extends CAutoReleasable {
    static native int jniForeach(long pb, Internals.BArrCallback cb);
    static native void jniFree(long pb);
    static native byte[] jniHash(long pb);
    static native int jniInsert(long pb, Oid id, String name);
    static native int jniInsertCommit(long pb, Oid id);
    static native int jniInsertRecur(long pb, Oid id, String name);
    static native int jniInsertTree(long pb, Oid id);
    static native int jniInsertWalk(long pb, long walk);
    static native int jniNew(AtomicLong out, long repoPtr);
    static native int jniObjectCount(long pb);
    static native int jniSetCallbacks(long pb, Internals.IIICallback progressCb);
    static native int jniSetThreads(long pb, int n);
    static native int jniWrite(long pb, String path, int mode, Internals.JCallback progressCb);
    static native int jniWriteBuf(Buf buf, long pb);
    static native int jniWritten(long pb);
    protected PackBuilder(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Nonnull
    public static PackBuilder create(@Nonnull Repository repo) {
        PackBuilder builder = new PackBuilder(false, 0);
        Error.throwIfNeeded(jniNew(builder._rawPtr, repo.getRawPointer()));
        return builder;
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public int setThreads(int n) {
        return jniSetThreads(getRawPointer(), n);
    }
    public PackBuilder insert(@Nonnull Oid id, @Nullable String name) {
        Error.throwIfNeeded(jniInsert(getRawPointer(), id, name));
        return this;
    }
    public PackBuilder insertTree(@Nonnull Oid id) {
        Error.throwIfNeeded(jniInsertTree(getRawPointer(), id));
        return this;
    }
    @Nonnull
    public PackBuilder insertCommit(@Nonnull Oid id) {
        Error.throwIfNeeded(jniInsertCommit(getRawPointer(), id));
        return this;
    }
    @Nonnull
    public PackBuilder insertWalk(@Nonnull Revwalk walk) {
        Error.throwIfNeeded(jniInsertWalk(getRawPointer(), walk.getRawPointer()));
        return this;
    }
    @Nonnull
    public PackBuilder insertRecur(@Nonnull Oid id, @Nullable String name) {
        Error.throwIfNeeded(jniInsertRecur(getRawPointer(), id, name));
        return this;
    }
    @Nonnull
    public String writeBuf() {
        Buf out = new Buf();
        jniWriteBuf(out, getRawPointer());
        return out.getString().orElse("");
    }
    public void write(String path, int mode, @Nullable Indexer.ProgressCb progressCb) {
        jniWrite(
                getRawPointer(),
                path,
                mode,
                progressCb == null
                        ? null
                        : ptr -> progressCb.accept(new Indexer.Progress(true, ptr)));
    }
    @Nonnull
    public Oid hash() {
        return Oid.of(jniHash(getRawPointer()));
    }
    public void foreach(@Nonnull ForeachCb foreachCb) {
        int e = jniForeach(getRawPointer(), foreachCb::accept);
        Error.throwIfNeeded(e);
    }
    public int objectCount() {
        return jniObjectCount(getRawPointer());
    }
    public int written() {
        return jniWritten(getRawPointer());
    }
    public void setCallbacks(ProgressCb progressCb) {
        int e = jniSetCallbacks(getRawPointer(), progressCb::accept);
        Error.throwIfNeeded(e);
    }
    @FunctionalInterface
    public interface ForeachCb {
        int accept(byte[] data);
    }
    public interface ProgressCb {
        int accept(int stage, int current, int total);
    }
}
