package com.github.git24j.core;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicLong;

public class GitObject extends CAutoReleasable {
    static native int jniDup(AtomicLong outObj, long objPtr);
    static native void jniFree(long objPtr);
    static native void jniId(long objPtr, Oid oid);
    static native int jniLookup(AtomicLong outObj, long repoPtr, Oid oid, int objType);
    static native int jniLookupPrefix(AtomicLong outObj, long repoPtr, String shortId, int objType);
    static native long jniOwner(long objPtr);
    static native int jniPeel(AtomicLong outObj, long objPtr, int objType);
    static native int jniShortId(Buf buf, long objPtr);
    static native int jniType(long objPtr);
    protected GitObject(boolean weak, long rawPointer) {
        super(weak, rawPointer);
    }
    static GitObject create(long objPtr) {
        if (objPtr == 0) {
            throw new IllegalStateException("object address is NULL, has it been closed?");
        }
        return GitObject.create(objPtr, Type.valueOf(jniType(objPtr)));
    }
    static GitObject create(long objPtr, Type type) {
        switch (type) {
            case INVALID:
                throw new IllegalStateException("invalid git object");
            case COMMIT:
                return new Commit(false, objPtr);
            case BLOB:
                return new Blob(false, objPtr);
            case TAG:
                return new Tag(false, objPtr);
            case TREE:
                return new Tree(false, objPtr);
            default:
                return new GitObject(false, objPtr);
        }
    }
    @Nonnull
    public static GitObject lookup(
            @Nonnull Repository repository, @Nonnull Oid oid, @Nonnull Type type) {
        AtomicLong outObj = new AtomicLong();
        Error.throwIfNeeded(jniLookup(outObj, repository.getRawPointer(), oid, type._bit));
        return GitObject.create(outObj.get(), type);
    }
    public static GitObject lookupPrefix(
            Repository repository, @Nonnull String shortId, Type type) {
        AtomicLong outObj = new AtomicLong();
        Error.throwIfNeeded(
                jniLookupPrefix(outObj, repository.getRawPointer(), shortId, type._bit));
        return GitObject.create(outObj.get(), type);
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public Type type() {
        return Type.valueOf(jniType(_rawPtr.get()));
    }
    public Oid id() {
        Oid oid = new Oid();
        jniId(getRawPointer(), oid);
        return oid;
    }
    public Buf shortId() {
        Buf buf = new Buf();
        Error.throwIfNeeded(jniShortId(buf, _rawPtr.get()));
        return buf;
    }
    public GitObject peel(Type targetType) {
        AtomicLong outPtr = new AtomicLong();
        Error.throwIfNeeded(jniPeel(outPtr, getRawPointer(), targetType._bit));
        return new GitObject(false, outPtr.get());
    }
    public GitObject dup() {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniDup(out, getRawPointer()));
        return GitObject.create(out.get());
    }
    public Repository owner() {
        return Repository.ofRaw(jniOwner(_rawPtr.get()));
    }
    public enum Type implements IBitEnum {
        ANY(-2),  
        INVALID(-1),  
        COMMIT(1),  
        TREE(2),  
        BLOB(3),  
        TAG(4),  
        OFS_DELTA(6),  
        REF_DELTA(7),  
        ;
        private final int _bit;
        private Type(int _bit) {
            this._bit = _bit;
        }
        static Type valueOf(int iVal) {
            for (Type x : Type.values()) {
                if (x._bit == iVal) {
                    return x;
                }
            }
            return INVALID;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
}
