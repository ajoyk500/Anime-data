package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ITEROVER;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class Index extends CAutoReleasable {
    static native int jniAdd(long idxPtr, long entryPtr);
    static native int jniAddAll(long idxPtr, String[] pathSpec, int flags, Callback callback);
    static native int jniAddByPath(long idxPtr, String path);
    static native int jniAddFromBuffer(long indexPtr, long entryPtr, byte[] buffer);
    static native int jniCaps(long idxPtr);
    static native byte[] jniChecksum(long indexPtr);
    static native int jniClear(long indexPtr);
    static native int jniConflictAdd(
            long indexPtr, long ancestorEntryPtr, long ourEntryPtr, long theirEntryPtr);
    static native int jniConflictCleanup(long indexPtr);
    static native int jniConflictGet(
            AtomicLong ancestorOut,
            AtomicLong ourOut,
            AtomicLong theirOut,
            long indexPtr,
            String path);
    static native void jniConflictIteratorFree(long iterPtr);
    static native int jniConflictIteratorNew(AtomicLong outIter, long indexPtr);
    static native int jniConflictNext(
            AtomicLong ancestorOut, AtomicLong ourOut, AtomicLong theirOut, long iterPtr);
    static native int jniConflictRemove(long indexPtr, String path);
    static native int jniEntryCount(long indexPtr);
    static native void jniEntryFree(long entryPtr);
    static native long jniEntryGetCtimeNanoseconds(long entryPtr);
    static native long jniEntryGetCtimeSeconds(long entryPtr);
    static native int jniEntryGetDev(long entryPtr);
    static native int jniEntryGetFileSize(long entryPtr);
    static native int jniEntryGetFlags(long entryPtr);
    static native int jniEntryGetFlagsExtended(long entryPtr);
    static native int jniEntryGetGid(long entryPtr);
    static native byte[] jniEntryGetId(long entryPtr);
    static native int jniEntryGetIno(long entryPtr);
    static native int jniEntryGetMode(long entryPtr);
    static native long jniEntryGetMtimeNanoseconds(long entryPtr);
    static native long jniEntryGetMtimeSeconds(long entryPtr);
    static native String jniEntryGetPath(long entryPtr);
    static native int jniEntryGetUid(long entryPtr);
    static native int jniEntryIsConflict(long entryPtr);
    static native long jniEntryNew();
    static native void jniEntrySetCtimeNanoseconds(long entryPtr, long ctimeNanoseconds);
    static native void jniEntrySetCtimeSeconds(long entryPtr, long ctimeSeconds);
    static native void jniEntrySetDev(long entryPtr, int dev);
    static native void jniEntrySetFileSize(long entryPtr, int fileSize);
    static native void jniEntrySetFlags(long entryPtr, int flags);
    static native void jniEntrySetFlagsExtended(long entryPtr, int flagsExtended);
    static native void jniEntrySetGid(long entryPtr, int gid);
    static native void jniEntrySetId(long entryPtr, Oid id);
    static native void jniEntrySetIno(long entryPtr, int ino);
    static native void jniEntrySetMode(long entryPtr, int mode);
    static native void jniEntrySetMtimeNanoseconds(long entryPtr, long mtimeNanoseconds);
    static native void jniEntrySetMtimeSeconds(long entryPtr, long mtimeSeconds);
    static native void jniEntrySetPath(long entryPtr, String path);
    static native void jniEntrySetUid(long entryPtr, int uid);
    static native int jniEntryStage(long entryPtr);
    static native int jniFind(AtomicInteger outPos, long indexPtr, String path);
    static native int jniFindPrefix(AtomicInteger outPos, long indexPtr, String prefix);
    static native void jniFree(long idxPtr);
    static native long jniGetByIndex(long indexPtr, int n);
    static native long jniGetByPath(long indexPtr, String path, int stage);
    static native int jniHasConflicts(long indexPtr);
    static native void jniIteratorFree(long iterPtr);
    static native int jniIteratorNew(AtomicLong outIterPtr, long indexPtr);
    static native int jniIteratorNext(AtomicLong outEntryPtr, long iterPtr);
    static native int jniOpen(AtomicLong outIndexPtr, String indexPath);
    static native long jniOwner(long idxPtr);
    static native String jniPath(long indexPtr);
    static native int jniRead(long indexPtr, int force);
    static native int jniReadTree(long indexPtr, long treePtr);
    static native int jniRemove(long indexPtr, String path, int stage);
    static native int jniRemoveByPath(long indexPtr, String path);
    static native int jniRemoveDirectory(long indexPtr, String dir, int stage);
    static native int jniSetCaps(long idxPtr, int caps);
    static native int jniSetVersion(long indexPtr, int version);
    static native int jniUpdateAll(long idxPtr, String[] pathSpec, Callback callback);
    static native int jniVersion(long indexPtr);
    static native int jniWrite(long idxPtr);
    static native int jniWriteTree(Oid outOid, long indexPtr);
    static native int jniWriteTreeTo(Oid outOid, long indexPtr, long repoPtr);
    protected Index(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public static Index open(String indexPath) {
        Index outIdx = new Index(false, 0);
        Error.throwIfNeeded(jniOpen(outIdx._rawPtr, indexPath));
        return outIdx;
    }
    public Repository owner() {
        long ptr = jniOwner(getRawPointer());
        if (ptr == 0) {
            throw new GitException(GitException.ErrorClass.INDEX, "Index owner not found");
        }
        return new Repository(ptr);
    }
    public EnumSet<Capability> caps() {
        int c = jniCaps(getRawPointer());
        if (c < 0) {
            return EnumSet.of(Capability.FROM_OWNER);
        }
        return IBitEnum.parse(c, Capability.class);
    }
    public void setCaps(EnumSet<Capability> caps) {
        if (caps.contains(Capability.FROM_OWNER)) {
            Error.throwIfNeeded(jniSetCaps(getRawPointer(), Capability.FROM_OWNER.getBit()));
            return;
        }
        Error.throwIfNeeded(jniSetCaps(getRawPointer(), IBitEnum.bitOrAll(caps)));
    }
    public int version() {
        return jniVersion(getRawPointer());
    }
    public void setVersion(int version) {
        Error.throwIfNeeded(jniSetVersion(getRawPointer(), version));
    }
    public void read(boolean force) {
        Error.throwIfNeeded(jniRead(getRawPointer(), force ? 0 : 1));
    }
    public void write() {
        Error.throwIfNeeded(jniWrite(getRawPointer()));
    }
    public String path() {
        return jniPath(getRawPointer());
    }
    @Nonnull
    public Oid checksum() {
        byte[] bytes = jniChecksum(getRawPointer());
        if (bytes == null) {
            throw new GitException(
                    GitException.ErrorClass.INDEX,
                    "git_index_checksum returned NULL unexpectedly.");
        }
        return Oid.of(bytes);
    }
    public void readTree(Tree tree) {
        Error.throwIfNeeded(jniReadTree(getRawPointer(), tree.getRawPointer()));
    }
    public Oid writeTree() {
        Oid outOid = new Oid();
        Error.throwIfNeeded(jniWriteTree(outOid, getRawPointer()));
        return outOid;
    }
    public Oid writeTreeTo(Repository repo) {
        Oid outOid = new Oid();
        Error.throwIfNeeded(jniWriteTreeTo(outOid, getRawPointer(), repo.getRawPointer()));
        return outOid;
    }
    public int entryCount() {
        return jniEntryCount(getRawPointer());
    }
    public void clear() {
        Error.throwIfNeeded(jniClear(getRawPointer()));
    }
    public Entry getEntryByIndex(int n) {
        return Entry.getByIndex(this, n);
    }
    @CheckForNull
    public Entry getEntryByPath(@Nonnull String path, @Nonnull Stage stage) {
        return Entry.getByPath(this, path, stage);
    }
    public void remove(String path, int stage) {
        Error.throwIfNeeded(jniRemove(getRawPointer(), path, stage));
    }
    public void removeDirectory(String dir, int stage) {
        Error.throwIfNeeded(jniRemoveDirectory(getRawPointer(), dir, stage));
    }
    public void add(Entry sourceEntry) {
        if (sourceEntry == null) {
            return;
        }
        Error.throwIfNeeded(jniAdd(getRawPointer(), sourceEntry.getRawPointer()));
    }
    public void addFromBuffer(Entry entry, byte[] buffer) {
        Error.throwIfNeeded(jniAddFromBuffer(getRawPointer(), entry.getRawPointer(), buffer));
    }
    public void removeByPath(String path) {
        Error.throwIfNeeded(jniRemoveByPath(getRawPointer(), path));
    }
    public void add(String path) {
        Error.throwIfNeeded(jniAddByPath(getRawPointer(), path));
    }
    public void addAll(
            String[] pathSpec, EnumSet<AddOption> flags, BiConsumer<String, String> callback) {
        Error.throwIfNeeded(
                jniAddAll(getRawPointer(), pathSpec, IBitEnum.bitOrAll(flags), callback::accept));
    }
    public void updateAll(List<String> pathSpec, Callback callback) {
        Error.throwIfNeeded(
                jniUpdateAll(getRawPointer(), pathSpec.toArray(new String[0]), callback));
    }
    public int find(String path) {
        AtomicInteger outPos = new AtomicInteger();
        int e = jniFind(outPos, getRawPointer(), path);
        Error.throwIfNeeded(e);
        return outPos.get();
    }
    public int findPrefix(String prefix) {
        AtomicInteger outPos = new AtomicInteger();
        int e = jniFindPrefix(outPos, getRawPointer(), prefix);
        Error.throwIfNeeded(e);
        return outPos.get();
    }
    public void conflictAdd(Conflict conflict) {
        Error.throwIfNeeded(
                jniConflictAdd(
                        getRawPointer(),
                        conflict.ancestor.getRawPointer(),
                        conflict.our.getRawPointer(),
                        conflict.their.getRawPointer()));
    }
    Conflict conflictGet(String path) {
        Conflict conflict = new Conflict();
        jniConflictGet(
                conflict.ancestor._rawPtr,
                conflict.our._rawPtr,
                conflict.their._rawPtr,
                getRawPointer(),
                path);
        return conflict;
    }
    public void conflictRemove(String path) {
        Error.throwIfNeeded(jniConflictRemove(getRawPointer(), path));
    }
    public void conflictCleanup() {
        Error.throwIfNeeded(jniConflictCleanup(getRawPointer()));
    }
    public boolean hasConflicts() {
        return jniHasConflicts(getRawPointer()) == 1;
    }
    public ConflictIterator conflictIteratorNew() {
        ConflictIterator iterator = new ConflictIterator();
        Error.throwIfNeeded(jniConflictIteratorNew(iterator._ptr, getRawPointer()));
        return iterator;
    }
    public enum Capability implements IBitEnum {
        IGNORE_CASE(1),
        NO_FILEMODE(1 << 1),
        NO_SYMLINKS(1 << 2),
        FROM_OWNER(-1),
        ;
        private final int _bit;
        Capability(int bit) {
            this._bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum AddOption implements IBitEnum {
        DEFAULT(0),
        FORCE(1 << 0),
        DISABLE_PATHSPEC_MATCH(1 << 1),
        CHECK_PATHSPEC(1 << 2),
        ;
        private final int _bit;
        AddOption(int bit) {
            this._bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum Stage implements IBitEnum {
        ANY(-1),
        NORMAL(0),
        ANCESTOR(1),
        OURS(2),
        THEIRS(3);
        private final int _bit;
        Stage(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public interface Callback {
        void accept(String path, String pathSpec);
    }
    public static class Entry extends CAutoReleasable {
        protected Entry(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static Entry create() {
            return new Entry(false, jniEntryNew());
        }
        public static Entry getByIndex(Index index, int n) {
            long ptr = Index.jniGetByIndex(index.getRawPointer(), n);
            if (ptr == 0) {
                return null;
            }
            return new Entry(true, ptr);
        }
        @CheckForNull
        public static Entry getByPath(
                @Nonnull Index index, @Nonnull String path, @Nonnull Stage stage) {
            long ptr = Index.jniGetByPath(index.getRawPointer(), path, stage.getBit());
            if (ptr == 0) {
                return null;
            }
            return new Entry(true, ptr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniEntryFree(cPtr);
        }
        public int state() {
            return Index.jniEntryStage(getRawPointer());
        }
        public boolean isConflict() {
            return Index.jniEntryIsConflict(getRawPointer()) == 1;
        }
        @Nonnull
        public Instant getCtime() {
            return Instant.ofEpochSecond(
                    jniEntryGetCtimeSeconds(getRawPointer()),
                    Index.jniEntryGetCtimeNanoseconds(getRawPointer()));
        }
        public void setCtime(@Nonnull Instant ctime) {
            jniEntrySetCtimeSeconds(getRawPointer(), ctime.getEpochSecond());
            jniEntrySetCtimeNanoseconds(getRawPointer(), ctime.getNano());
        }
        @Nonnull
        public Instant getMtime() {
            return Instant.ofEpochSecond(
                    jniEntryGetMtimeSeconds(getRawPointer()),
                    Index.jniEntryGetMtimeNanoseconds(getRawPointer()));
        }
        public void setMtime(@Nonnull Instant mtime) {
            jniEntrySetMtimeSeconds(getRawPointer(), mtime.getEpochSecond());
            jniEntrySetMtimeNanoseconds(getRawPointer(), mtime.getNano());
        }
        public int getDev() {
            return jniEntryGetDev(getRawPointer());
        }
        public void setDev(int dev) {
            jniEntrySetDev(getRawPointer(), dev);
        }
        public int getIno() {
            return jniEntryGetIno(getRawPointer());
        }
        public void setIno(int ino) {
            jniEntrySetIno(getRawPointer(), ino);
        }
        public int getMode() {
            return jniEntryGetMode(getRawPointer());
        }
        public void setMode(int mode) {
            jniEntrySetMode(getRawPointer(), mode);
        }
        public int getUid() {
            return jniEntryGetUid(getRawPointer());
        }
        public void setUid(int uid) {
            jniEntrySetUid(getRawPointer(), uid);
        }
        public int getGid() {
            return jniEntryGetGid(getRawPointer());
        }
        public void setGid(int gid) {
            jniEntrySetGid(getRawPointer(), gid);
        }
        public int getFileSize() {
            return jniEntryGetFileSize(getRawPointer());
        }
        public void setFileSize(int fileSize) {
            jniEntrySetFileSize(getRawPointer(), fileSize);
        }
        @Nonnull
        public Oid getId() {
            byte[] raw = jniEntryGetId(getRawPointer());
            return Oid.of(raw);
        }
        public void setId(@Nonnull Oid id) {
            jniEntrySetId(getRawPointer(), id);
        }
        public int getFlags() {
            return jniEntryGetFlags(getRawPointer());
        }
        public void setFlags(int flags) {
            jniEntrySetFlags(getRawPointer(), flags);
        }
        public int getFlagsExtended() {
            return jniEntryGetFlagsExtended(getRawPointer());
        }
        public void setFlagsExtended(int flagsExtended) {
            jniEntrySetFlagsExtended(getRawPointer(), flagsExtended);
        }
        public String getPath() {
            return jniEntryGetPath(getRawPointer());
        }
        public void setPath(String path) {
            jniEntrySetPath(getRawPointer(), path);
        }
    }
    public static class Iterator extends CAutoReleasable {
        private final AtomicLong _ptr = new AtomicLong();
        protected Iterator(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static Iterator of(@Nonnull Index index) {
            Iterator iterator = new Iterator(false, 0);
            Error.throwIfNeeded(Index.jniIteratorNew(iterator._ptr, index.getRawPointer()));
            return iterator;
        }
        @Override
        protected void freeOnce(long cPtr) {
            Index.jniIteratorFree(cPtr);
        }
        @CheckForNull
        public Entry next() {
            Entry nextEntry = new Entry(true, 0);
            int e = Index.jniIteratorNext(nextEntry._rawPtr, _ptr.get());
            if (e == ITEROVER.getCode()) {
                return null;
            }
            Error.throwIfNeeded(e);
            return nextEntry;
        }
    }
    public static class Conflict {
        public final Entry ancestor;
        public final Entry our;
        public final Entry their;
        Conflict() {
            ancestor = new Entry(true, 0);
            our = new Entry(true, 0);
            their = new Entry(true, 0);
        }
    }
    public static class ConflictIterator {
        AtomicLong _ptr = new AtomicLong();
        @Override
        protected void finalize() throws Throwable {
            if (_ptr.get() != 0) {
                jniConflictIteratorFree(_ptr.get());
            }
            super.finalize();
        }
        public Conflict next() {
            Conflict conflict = new Conflict();
            int e =
                    jniConflictNext(
                            conflict.ancestor._rawPtr,
                            conflict.our._rawPtr,
                            conflict.their._rawPtr,
                            _ptr.get());
            if (e == ITEROVER.getCode()) {
                return null;
            }
            Error.throwIfNeeded(e);
            return conflict;
        }
    }
}
