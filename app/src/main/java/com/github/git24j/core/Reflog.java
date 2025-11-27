package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Reflog extends CAutoReleasable {
    static native int jniAppend(long reflog, Oid id, long committer, String msg);
    static native int jniDelete(long repoPtr, String name);
    static native int jniDrop(long reflog, int idx, int rewritePreviousEntry);
    static native long jniEntryByindex(long reflog, int idx);
    static native long jniEntryCommitter(long entry);
    static native byte[] jniEntryIdNew(long entry);
    static native byte[] jniEntryIdOld(long entry);
    static native String jniEntryMessage(long entry);
    static native int jniEntrycount(long reflog);
    static native void jniFree(long reflog);
    static native int jniRead(AtomicLong out, long repoPtr, String name);
    static native int jniRename(long repoPtr, String oldName, String name);
    static native int jniWrite(long reflog);
    protected Reflog(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    public static void rename(
            @Nonnull Repository repository, @Nonnull String oldName, @Nonnull String name) {
        Error.throwIfNeeded(jniRename(repository.getRawPointer(), oldName, name));
    }
    public void append(@Nonnull Oid oid, @Nonnull Signature committer, @Nullable String msg) {
        Error.throwIfNeeded(jniAppend(getRawPointer(), oid, committer.getRawPointer(), msg));
    }
    public void delete(@Nonnull Repository repository, @Nonnull String name) {
        Error.throwIfNeeded(jniDelete(repository.getRawPointer(), name));
    }
    public void drop(int idx, boolean rewritePreviousEntry) {
        Error.throwIfNeeded(jniDrop(getRawPointer(), idx, rewritePreviousEntry ? 1 : 0));
    }
    @Nonnull
    public Entry entryByIndex(int idx) {
        return new Entry(true, jniEntryByindex(getRawPointer(), idx));
    }
    public int entryCount() {
        return jniEntrycount(getRawPointer());
    }
    public static Reflog read(@Nonnull Repository repository, @Nonnull String name) {
        Reflog reflog = new Reflog(false, 0);
        int e = jniRead(reflog._rawPtr, repository.getRawPointer(), name);
        Error.throwIfNeeded(e);
        return reflog;
    }
    public void write() {
        Error.throwIfNeeded(jniWrite(getRawPointer()));
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public static class Entry extends CAutoReleasable {
        protected Entry(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
        }
        @Nonnull
        public Signature committer() {
            return new Signature(true, jniEntryCommitter(getRawPointer()));
        }
        @Nonnull
        public Oid idNew() {
            return Oid.of(jniEntryIdNew(getRawPointer()));
        }
        @Nonnull
        public Oid idOld() {
            return Oid.of(jniEntryIdOld(getRawPointer()));
        }
        @Nullable
        public String message() {
            return jniEntryMessage(getRawPointer());
        }
    }
}
