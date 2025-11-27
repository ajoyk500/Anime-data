package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Odb extends CAutoReleasable {
    static native int jniAddAlternate(long odb, long backend, int priority);
    static native int jniAddBackend(long odb, long backend, int priority);
    static native int jniAddDiskAlternate(long odb, String path);
    static native int jniBackendLoose(
            AtomicLong out,
            String objectsDir,
            int compressionLevel,
            int doFsync,
            int dirMode,
            int fileMode);
    static native int jniBackendOnePack(AtomicLong out, String objectsDir);
    static native int jniExists(long db, Oid id);
    static native int jniExistsPrefix(Oid out, long db, String shortId);
    static native int jniExpandIds(long db, long ids, int count);
    static native byte[] jniExpandIdsGetId(long expandIdsPtr, int idx);
    static native int jniExpandIdsGetLength(long expandIdsPtr);
    static native int jniExpandIdsGetType(long expandIdsPtr, int idx);
    static native long jniExpandIdsNew(String[] shortIds, int type);
    static native void jniFree(long db);
    static native int jniGetBackend(AtomicLong out, long odb, int pos);
    static native int jniHash(Oid out, byte[] data, int len, int type);
    static native int jniHashfile(Oid out, String path, int type);
    static native int jniNew(AtomicLong out);
    static native int jniNumBackends(long odb);
    static native long jniObjectData(long object);
    static native int jniObjectDup(AtomicLong dest, long source);
    static native void jniObjectFree(long object);
    static native byte[] jniObjectId(long object);
    static native int jniObjectSize(long object);
    static native int jniObjectType(long object);
    static native int jniOpen(AtomicLong out, String objectsDir);
    static native int jniOpenRstream(
            AtomicLong out, AtomicInteger len, AtomicInteger type, long db, Oid oid);
    static native int jniOpenWstream(AtomicLong out, long db, int size, int type);
    static native int jniRead(AtomicLong out, long db, Oid id);
    static native int jniReadHeader(AtomicInteger lenOut, AtomicInteger typeOut, long db, Oid id);
    static native int jniReadPrefix(AtomicLong out, long db, String shortId);
    static native int jniRefresh(long db);
    static native int jniStreamFinalizeWrite(Oid out, long stream);
    static native void jniStreamFree(long stream);
    static native int jniStreamRead(long streamPtr, byte[] buffer, int len);
    static native int jniStreamWrite(long stream, String buffer, int len);
    static native int jniWrite(Oid out, long odb, byte[] data, int len, int type);
    protected Odb(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    @Nonnull
    public static Odb create() {
        Odb out = new Odb(false, 0);
        Error.throwIfNeeded(jniNew(out._rawPtr));
        if (out.isNull()) {
            throw new GitException(GitException.ErrorClass.ODB, "Failed to create object database");
        }
        return out;
    }
    @Nonnull
    public static Odb create(@Nonnull String objectsDir) {
        Odb out = new Odb(false, 0);
        Error.throwIfNeeded(jniOpen(out._rawPtr, objectsDir));
        if (out.isNull()) {
            throw new GitException(GitException.ErrorClass.ODB, "Failed to create object database");
        }
        return out;
    }
    @Nonnull
    public static Oid hash(@Nonnull byte[] data, @Nonnull GitObject.Type type) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniHash(oid, data, data.length, type.getBit()));
        return oid;
    }
    public static Oid hashfile(@Nonnull String path, @Nonnull GitObject.Type type) {
        Oid out = new Oid();
        Error.throwIfNeeded(jniHashfile(out, path, type.getBit()));
        return out;
    }
    @Nonnull
    public static OdbBackend backendLoose(
            @Nonnull String objectsDir,
            int compressionLevel,
            boolean doFsync,
            int dirMode,
            int fileMode) {
        OdbBackend out = new OdbBackend(false, 0);
        Error.throwIfNeeded(
                jniBackendLoose(
                        out._rawPtr,
                        objectsDir.toString(),
                        compressionLevel,
                        doFsync ? 1 : 0,
                        dirMode,
                        fileMode));
        return out;
    }
    @Nonnull
    public static OdbBackend backendOnePack(@Nonnull String indexFile) {
        OdbBackend out = new OdbBackend(false, 0);
        Error.throwIfNeeded(jniBackendOnePack(out._rawPtr, indexFile));
        return out;
    }
    public void addDiskAlternate(@Nonnull String path) {
        Error.throwIfNeeded(jniAddDiskAlternate(getRawPointer(), path));
    }
    @Nullable
    public OdbObject read(@Nonnull Oid id) {
        OdbObject out = new OdbObject(false, 0);
        int e = jniRead(out._rawPtr, getRawPointer(), id);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }
    @Nullable
    public OdbObject readPrefix(String shortId) {
        OdbObject out = new OdbObject(false, 0);
        int e = jniReadPrefix(out._rawPtr, getRawPointer(), shortId);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }
    @Nullable
    public OdbObject.Header readHeader(@Nonnull Oid id) {
        AtomicInteger lenOut = new AtomicInteger();
        AtomicInteger typeOut = new AtomicInteger();
        int e = jniReadHeader(lenOut, typeOut, getRawPointer(), id);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new OdbObject.Header(
                lenOut.get(), IBitEnum.valueOf(typeOut.get(), GitObject.Type.class));
    }
    public boolean exists(@Nonnull Oid id) {
        return jniExists(getRawPointer(), id) == 1;
    }
    @Nullable
    public Oid existsPrefix(@Nonnull String shortId) {
        Oid fullId = new Oid();
        int e = jniExistsPrefix(fullId, getRawPointer(), shortId);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return fullId;
    }
    @Nonnull
    public List<Oid> expandIds(@Nonnull List<String> shortIds, @Nonnull GitObject.Type type) {
        int idsCount = shortIds.size();
        long cIdArr = jniExpandIdsNew(shortIds.toArray(new String[0]), type.getBit());
        Error.throwIfNeeded(jniExpandIds(getRawPointer(), cIdArr, idsCount));
        List<Oid> expandIds = new ArrayList<>(idsCount);
        for (int i = 0; i < idsCount; i++) {
            Oid oid = Oid.of(jniExpandIdsGetId(cIdArr, i));
            expandIds.add(oid);
        }
        return expandIds;
    }
    public void refresh() {
        Error.throwIfNeeded(jniRefresh(getRawPointer()));
    }
    @Nonnull
    public Oid write(byte[] data, GitObject.Type type) {
        Oid out = new Oid();
        Error.throwIfNeeded(jniWrite(out, getRawPointer(), data, data.length, type.getBit()));
        return out;
    }
    @Nonnull
    public Stream openWstream(int size, @Nonnull GitObject.Type type) {
        Stream ws = new Stream(0);
        Error.throwIfNeeded(jniOpenWstream(ws._rawPtr, getRawPointer(), size, type.getBit()));
        return ws;
    }
    @Nonnull
    public RStream openRstream(@Nonnull Oid oid) {
        AtomicLong out = new AtomicLong();
        AtomicInteger outLen = new AtomicInteger();
        AtomicInteger outType = new AtomicInteger();
        Error.throwIfNeeded(jniOpenRstream(out, outLen, outType, getRawPointer(), oid));
        return new RStream(
                out.get(), outLen.get(), IBitEnum.valueOf(outType.get(), GitObject.Type.class));
    }
    public void addBackend(@Nonnull OdbBackend backend, int priority) {
        Error.throwIfNeeded(jniAddBackend(getRawPointer(), backend.getRawPointer(), priority));
    }
    public void addAlternate(@Nonnull OdbBackend backend, int priority) {
        Error.throwIfNeeded(jniAddAlternate(getRawPointer(), backend.getRawPointer(), priority));
    }
    public int numBackends() {
        return jniNumBackends(getRawPointer());
    }
    @Nullable
    public OdbBackend getBackend(int pos) {
        OdbBackend backend = new OdbBackend(false, 0);
        int e = jniGetBackend(backend._rawPtr, getRawPointer(), pos);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return backend;
    }
    public static class ExpandId {
        private final Oid _oid;
        private final GitObject.Type _type;
        public ExpandId(@Nullable Oid oid, @Nullable GitObject.Type type) {
            _oid = oid;
            _type = type;
        }
        public Oid getOid() {
            return _oid;
        }
        public GitObject.Type getType() {
            return _type;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ExpandId expandId = (ExpandId) o;
            return Objects.equals(_oid, expandId._oid) && _type == expandId._type;
        }
        @Override
        public int hashCode() {
            return Objects.hash(_oid, _type);
        }
    }
    public static class Stream extends CAutoCloseable {
        protected Stream(long rawPointer) {
            super(rawPointer);
        }
        @Override
        protected void releaseOnce(long cPtr) {
            jniStreamFree(cPtr);
        }
        public void write(@Nonnull String buffer) {
            Error.throwIfNeeded(jniStreamWrite(getRawPointer(), buffer, buffer.length()));
        }
        public Oid finalizeWrite() {
            Oid out = new Oid();
            Error.throwIfNeeded(jniStreamFinalizeWrite(out, getRawPointer()));
            return out;
        }
        public void read(byte[] buffer) {
            Error.throwIfNeeded(jniStreamRead(getRawPointer(), buffer, buffer.length));
        }
    }
    public static class RStream extends Stream {
        private final int _size;
        private final GitObject.Type _type;
        protected RStream(long rawPointer, int size, GitObject.Type type) {
            super(rawPointer);
            _size = size;
            _type = type;
        }
        public int getSize() {
            return _size;
        }
        public GitObject.Type getType() {
            return _type;
        }
    }
}
