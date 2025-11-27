package com.github.git24j.core;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Tree extends GitObject {
    static native int jniBuilderClear(long bld);
    static native int jniBuilderEntrycount(long bld);
    static native void jniBuilderFilter(long bldPtr, Internals.JCallback callback);
    static native void jniBuilderFree(long bld);
    static native long jniBuilderGet(long bld, String filename);
    static native int jniBuilderInsert(
            AtomicLong out, long bld, String filename, Oid id, int filemode);
    static native int jniBuilderNew(AtomicLong out, long repoPtr, long source);
    static native int jniBuilderRemove(long bld, String filename);
    static native int jniBuilderWrite(Oid id, long bld);
    static native int jniBuilderWriteWithBuffer(Oid oid, long bld, Buf tree);
    static native int jniCreateUpdated(Oid out, long repoPtr, long baseline, long[] updates);
    static native int jniDup(AtomicLong out, long source);
    static native long jniEntryByid(long tree, Oid id);
    static native long jniEntryByindex(long tree, int idx);
    static native long jniEntryByname(long tree, String filename);
    static native int jniEntryBypath(AtomicLong out, long root, String path);
    static native int jniEntryCmp(long e1, long e2);
    static native int jniEntryDup(AtomicLong dest, long source);
    static native int jniEntryFilemode(long entry);
    static native int jniEntryFilemodeRaw(long entry);
    static native void jniEntryFree(long entry);
    static native byte[] jniEntryId(long entry);
    static native String jniEntryName(long entry);
    static native int jniEntryToObject(AtomicLong objectOut, long repoPtr, long entry);
    static native int jniEntryType(long entry);
    static native int jniEntrycount(long tree);
    static native void jniUpdateFree(long updatePtr);
    static native long jniUpdateNew(int updateType, Oid oid, int filemodeType, String path);
    static native int jniWalk(long treePtr, int mode, Internals.SJCallback callback);
    Tree(boolean weak, long rawPointer) {
        super(weak, rawPointer);
    }
    @Nonnull
    public static Tree lookup(@Nonnull Repository repo, @Nonnull Oid oid) {
        return (Tree) GitObject.lookup(repo, oid, Type.TREE);
    }
    @Nonnull
    public static Tree lookupPrefix(@Nonnull Repository repo, @Nonnull String shortId) {
        return (Tree) GitObject.lookupPrefix(repo, shortId, Type.TREE);
    }
    @Nonnull
    public static Builder newBuilder(@Nonnull Repository repo, @Nullable Tree source) {
        Builder bld = new Builder(false, 0);
        int e =
                jniBuilderNew(
                        bld._rawPtr,
                        repo.getRawPointer(),
                        source == null ? 0 : source.getRawPointer());
        Error.throwIfNeeded(e);
        return bld;
    }
    public int walk(WalkMode mode, WalkCb cb) {
        return jniWalk(
                getRawPointer(), mode.getBit(), ((s, ptr) -> cb.accept(s, new Entry(true, ptr))));
    }
    public int entryCount() {
        return jniEntrycount(getRawPointer());
    }
    @Nullable
    public Entry entryByName(@Nonnull String filename) {
        long ptr = jniEntryByname(getRawPointer(), filename);
        return ptr == 0 ? null : new Entry(true, ptr);
    }
    @Nullable
    public Entry entryByIndex(int idx) {
        long ptr = jniEntryByindex(getRawPointer(), idx);
        return ptr == 0 ? null : new Entry(true, ptr);
    }
    @Nullable
    public Entry entryById(@Nonnull Oid id) {
        long ptr = jniEntryByid(getRawPointer(), id);
        return ptr == 0 ? null : new Entry(true, ptr);
    }
    @Nullable
    public Entry entryByPath(@Nonnull String path) {
        Entry out = new Entry(false, 0);
        int e = jniEntryBypath(out._rawPtr, getRawPointer(), path);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }
    @Override
    public Tree dup() {
        return (Tree) super.dup();
    }
    @Nonnull
    public Oid createUpdated(
            @Nonnull Repository repo, @Nonnull Tree baseline, @Nonnull List<Update> updates) {
        Oid outOid = new Oid();
        int e =
                jniCreateUpdated(
                        outOid,
                        repo.getRawPointer(),
                        baseline.getRawPointer(),
                        updates.stream().mapToLong(Tree.Update::getRawPointer).toArray());
        Error.throwIfNeeded(e);
        return outOid;
    }
    public enum UpdateT {
        UPSERT,
        REMOVE;
    }
    public enum WalkMode implements IBitEnum {
        PRE(0),
        POST(1);
        private final int _bit;
        WalkMode(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public interface WalkCb {
        int accept(String root, Entry entry);
    }
    public static class Entry extends CAutoReleasable {
        protected Entry(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniEntryFree(cPtr);
        }
        @Nonnull
        public Entry dup() {
            Entry out = new Entry(false, 0);
            Error.throwIfNeeded(jniEntryDup(out._rawPtr, this.getRawPointer()));
            return out;
        }
        @CheckForNull
        public String name() {
            return jniEntryName(this.getRawPointer());
        }
        @Nonnull
        public Oid id() {
            return Oid.of(jniEntryId(this.getRawPointer()));
        }
        @Nonnull
        public GitObject.Type type() {
            return GitObject.Type.valueOf(jniEntryType(this.getRawPointer()));
        }
        @Nonnull
        public FileMode filemode() {
            return IBitEnum.valueOf(
                    jniEntryFilemode(this.getRawPointer()), FileMode.class, FileMode.UNREADABLE);
        }
        @Nonnull
        public FileMode filemodeRaw() {
            return IBitEnum.valueOf(
                    jniEntryFilemodeRaw(this.getRawPointer()), FileMode.class, FileMode.UNREADABLE);
        }
        public int cmp(@Nonnull Entry that) {
            return jniEntryCmp(getRawPointer(), that.getRawPointer());
        }
        @Nonnull
        public GitObject toObject(@Nonnull Repository repo) {
            GitObject out = new GitObject(false, 0);
            int e = jniEntryToObject(out._rawPtr, repo.getRawPointer(), this.getRawPointer());
            Error.throwIfNeeded(e);
            return out;
        }
    }
    public static class Update extends CAutoReleasable {
        protected Update(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static Update create(
                @Nonnull UpdateT updateType,
                @Nullable Oid oid,
                @Nonnull FileMode fileModeType,
                @Nonnull String path) {
            long ptr = jniUpdateNew(updateType.ordinal(), oid, fileModeType.getBit(), path);
            return new Update(false, ptr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniUpdateFree(cPtr);
        }
    }
    public static class Builder extends CAutoReleasable {
        protected Builder(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniBuilderFree(cPtr);
        }
        public void filter(Builder builder, FilterCb callback) {
            jniBuilderFilter(builder.getRawPointer(), ptr -> callback.accept(new Entry(true, ptr)));
        }
        public void clear() {
            Error.throwIfNeeded(jniBuilderClear(this.getRawPointer()));
        }
        public int entryCount() {
            return jniBuilderEntrycount(getRawPointer());
        }
        @Nullable
        public Entry get(@Nonnull String filename) {
            long ptr = jniBuilderGet(this.getRawPointer(), filename);
            if (ptr == 0) {
                return null;
            }
            return new Entry(true, ptr);
        }
        @Nonnull
        public Entry insert(
                @Nonnull String filename, @Nonnull Oid oid, @Nonnull FileMode filemode) {
            Entry out = new Entry(true, 0);
            Error.throwIfNeeded(
                    jniBuilderInsert(
                            out._rawPtr, this.getRawPointer(), filename, oid, filemode.getBit()));
            return out;
        }
        public void remove(@Nonnull String filename) {
            Error.throwIfNeeded(jniBuilderRemove(this.getRawPointer(), filename));
        }
        @Nonnull
        public Oid write() {
            Oid out = new Oid();
            Error.throwIfNeeded(jniBuilderWrite(out, getRawPointer()));
            return out;
        }
        public Oid writeWithBuf(@Nullable Buf buf) {
            if (buf == null) {
                buf = new Buf();
            }
            Oid out = new Oid();
            Error.throwIfNeeded(jniBuilderWriteWithBuffer(out, this.getRawPointer(), buf));
            return out;
        }
        @FunctionalInterface
        public interface FilterCb {
            int accept(Entry entry);
        }
    }
}
