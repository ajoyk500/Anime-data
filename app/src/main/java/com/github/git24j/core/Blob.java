package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Blob extends GitObject {
    static native int jniCreateFromBuffer(Oid oid, long repoPtr, final byte[] buf);
    static native int jniCreateFromDisk(Oid oid, long repoPtr, String path);
    static native int jniCreateFromStream(AtomicLong outStream, long repoPtr, String hintPath);
    static native int jniCreateFromStreamCommit(Oid oid, long streamPtr);
    static native int jniCreateFromWorkdir(Oid oid, long repoPtr, String relativePath);
    static native int jniDup(AtomicLong outDest, long srcPtr);
    static native int jniFilteredContent(Buf out, long blob, String asPath, int checkForBinaryData);
    static native byte[] jniId(long blobPtr);
    static native int jniIsBinary(long blobPtr);
    static native int jniLookup(AtomicLong outBlob, long repoPtr, Oid oid);
    static native int jniLookupPrefix(AtomicLong outBlob, long repoPtr, String shortId);
    static native long jniOwner(long blobPtr);
    static native long jniRawSize(long blobPtr);
    Blob(boolean weak, long rawPointer) {
        super(weak, rawPointer);
    }
    public static Oid createFromWorkdir(Repository repo, String relativePath) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniCreateFromWorkdir(oid, repo.getRawPointer(), relativePath));
        return oid;
    }
    public static Oid createdFromDisk(Repository repo, String path) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniCreateFromDisk(oid, repo.getRawPointer(), path));
        return oid;
    }
    public static @Nonnull WriteStream createFromStream(
            @Nonnull Repository repo, @Nullable String hintpath) {
        AtomicLong outWs = new AtomicLong();
        Error.throwIfNeeded(jniCreateFromStream(outWs, repo.getRawPointer(), hintpath));
        return new WriteStream(outWs.get());
    }
    public static Oid createFromStreamCommit(WriteStream ws) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniCreateFromStreamCommit(oid, ws.getRawPointer()));
        return oid;
    }
    public static Oid createFromBuffer(Repository repo, final byte[] buf) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniCreateFromBuffer(oid, repo.getRawPointer(), buf));
        return oid;
    }
    @CheckForNull
    public static Blob lookup(@Nonnull Repository repo, @Nonnull Oid oid) {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniLookup(out, repo.getRawPointer(), oid));
        return out.get() == 0 ? null : new Blob(false, out.get());
    }
    @CheckForNull
    public static Blob lookupPrefix(@Nonnull Repository repo, @Nonnull String shortId) {
        AtomicLong outBlob = new AtomicLong();
        Error.throwIfNeeded(jniLookupPrefix(outBlob, repo.getRawPointer(), shortId));
        return outBlob.get() == 0 ? null : new Blob(false, outBlob.get());
    }
    @Override
    @Nonnull
    public Blob dup() {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniDup(out, getRawPointer()));
        return new Blob(false, out.get());
    }
    @CheckForNull
    @Override
    public Oid id() {
        return Oid.ofNullable(jniId(getRawPointer()));
    }
    @Override
    public Repository owner() {
        return Repository.ofRaw(jniOwner(getRawPointer()));
    }
    public long rawSize() {
        return jniRawSize(getRawPointer());
    }
    public boolean isBinary() {
        return jniIsBinary(getRawPointer()) == 1;
    }
    @Nullable
    public String filteredContent(@Nonnull String asPath, boolean checkForBinaryData) {
        Buf out = new Buf();
        Error.throwIfNeeded(
                jniFilteredContent(out, getRawPointer(), asPath, checkForBinaryData ? 1 : 0));
        return out.getString().orElse(null);
    }
}
