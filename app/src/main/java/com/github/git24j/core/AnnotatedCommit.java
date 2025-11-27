package com.github.git24j.core;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicLong;

public class AnnotatedCommit extends CAutoReleasable {
    static native void jniFree(long acPtr);
    static native int jniFromFetchHead(
            AtomicLong outAc, long repoPtr, String branchName, String remoteUrl, Oid oid);
    static native int jniFromRef(AtomicLong outAc, long repoPtr, long refPtr);
    static native int jniFromRevspec(AtomicLong outAc, long repoPtr, String revspec);
    static native byte[] jniId(long acPtr);
    static native int jniLookup(AtomicLong outAc, long repoPtr, Oid oid);
    static native String jniRef(long acPtr);
    protected AnnotatedCommit(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    public static AnnotatedCommit fromRef(Repository repo, Reference ref) {
        AnnotatedCommit commit = new AnnotatedCommit(true, 0);
        Error.throwIfNeeded(jniFromRef(commit._rawPtr, repo.getRawPointer(), ref.getRawPointer()));
        return commit;
    }
    @Nonnull
    public static AnnotatedCommit fromFetchHead(
            @Nonnull Repository repo,
            @Nonnull String branchName,
            @Nonnull String remoteUrl,
            @Nonnull Oid oid) {
        AnnotatedCommit commit = new AnnotatedCommit(true, 0);
        Error.throwIfNeeded(
                jniFromFetchHead(commit._rawPtr, repo.getRawPointer(), branchName, remoteUrl, oid));
        return commit;
    }
    @Nonnull
    public static AnnotatedCommit lookup(@Nonnull Repository repo, @Nonnull Oid oid) {
        AnnotatedCommit commit = new AnnotatedCommit(true, 0);
        Error.throwIfNeeded(jniLookup(commit._rawPtr, repo.getRawPointer(), oid));
        return commit;
    }
    @Nonnull
    public static AnnotatedCommit fromRevspec(@Nonnull Repository repo, @Nonnull String revspec) {
        AnnotatedCommit commit = new AnnotatedCommit(true, 0);
        Error.throwIfNeeded(jniFromRevspec(commit._rawPtr, repo.getRawPointer(), revspec));
        return commit;
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    @Nonnull
    public Oid id() {
        return Oid.of(jniId(getRawPointer()));
    }
    @Nonnull
    public String ref() {
        return jniRef(getRawPointer());
    }
}
