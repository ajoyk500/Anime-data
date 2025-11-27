package com.github.git24j.core;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicLong;

public class Transaction extends CAutoReleasable {
    static native int jniCommit(long tx);
    static native void jniFree(long tx);
    static native int jniLockRef(long tx, String refname);
    static native int jniNew(AtomicLong out, long repoPtr);
    static native int jniRemove(long tx, String refname);
    static native int jniSetReflog(long tx, String refname, long reflog);
    static native int jniSetSymbolicTarget(
            long tx, String refname, String target, long sig, String msg);
    static native int jniSetTarget(long tx, String refname, Oid target, long sig, String msg);
    protected Transaction(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Nonnull
    public static Transaction create(@Nonnull Repository repo) {
        Transaction tx = new Transaction(false, 0);
        Error.throwIfNeeded(jniNew(tx._rawPtr, repo.getRawPointer()));
        return tx;
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public void lockRef(@Nonnull String refname) {
        Error.throwIfNeeded(jniLockRef(getRawPointer(), refname));
    }
    public void setTarget(
            @Nonnull String refname,
            @Nonnull Oid target,
            @Nonnull Signature signature,
            @Nonnull String message) {
        Error.throwIfNeeded(
                jniSetTarget(getRawPointer(), refname, target, signature.getRawPointer(), message));
    }
    public void setSymbolicTarget(
            @Nonnull String refname,
            @Nonnull String target,
            @Nonnull Signature signature,
            @Nonnull String message) {
        Error.throwIfNeeded(
                jniSetSymbolicTarget(
                        getRawPointer(), refname, target, signature.getRawPointer(), message));
    }
    public void setReflog(@Nonnull String refname, @Nonnull Reflog reflog) {
        Error.throwIfNeeded(jniSetReflog(getRawPointer(), refname, reflog.getRawPointer()));
    }
    public void remove(@Nonnull String refname) {
        Error.throwIfNeeded(jniRemove(getRawPointer(), refname));
    }
    public void commit() {
        Error.throwIfNeeded(jniCommit(getRawPointer()));
    }
}
