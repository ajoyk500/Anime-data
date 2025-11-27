package com.github.git24j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Branch {
    static native int jniCreate(
            AtomicLong outRef, long repoPtr, String branchName, long targetPtr, int force);
    static native int jniCreateFromAnnotated(
            AtomicLong outRef, long repoPtr, String branchName, long annoCommitPtr, int force);
    static native int jniDelete(long refPtr);
    static native int jniIsCheckedOut(long refPtr);
    static native int jniIsHead(long refPtr);
    static native void jniIteratorFree(long branchIterPtr);
    static native int jniIteratorNew(AtomicLong outBranchIter, long repoPtr, int listFlags);
    static native int jniLookup(AtomicLong outRef, long repoPtr, String branchName, int branchType);
    static native int jniMove(AtomicLong outRef, long branchPtr, String branchName, int force);
    static native int jniName(AtomicReference<String> outStr, long refPtr);
    static native int jniNext(AtomicLong outRef, AtomicInteger outType, long branchIterPtr);
    static native int jniRemoteName(Buf outBuf, long repoPtr, String canonicalBranchName);
    static native int jniSetUpstream(long refPtr, String upstreamName);
    static native int jniUpstream(AtomicLong outRef, long branchPtr);
    static native int jniUpstreamName(Buf outBuf, long repoPtr, String refName);
    static native int jniUpstreamRemote(Buf outBuf, long repoPtr, String refName);
    public static Reference create(
            @Nonnull Repository repo,
            @Nonnull String branchName,
            @Nonnull Commit target,
            boolean force) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniCreate(
                        outRef,
                        repo.getRawPointer(),
                        branchName,
                        target.getRawPointer(),
                        force ? 1 : 0));
        return new Reference(false, outRef.get());
    }
    public static Reference createFromAnnotated(
            Repository repo, String branchName, AnnotatedCommit annotatedCommit, boolean force) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniCreateFromAnnotated(
                        outRef,
                        repo.getRawPointer(),
                        branchName,
                        annotatedCommit.getRawPointer(),
                        force ? 1 : 0));
        return new Reference(false, outRef.get());
    }
    public static void delete(Reference branch) {
        Error.throwIfNeeded(jniDelete(branch._rawPtr.getAndSet(0)));
    }
    public static Reference move(Reference branch, String branchName, boolean force) {
        Reference outRef = new Reference(true, 0);
        Error.throwIfNeeded(
                jniMove(outRef._rawPtr, branch.getRawPointer(), branchName, force ? 1 : 0));
        return outRef;
    }
    public static Reference lookup(Repository repo, String branchName, BranchType branchType) {
        AtomicLong outRef = new AtomicLong();
        int e = jniLookup(outRef, repo.getRawPointer(), branchName, branchType.ordinal());
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Reference(false, outRef.get());
    }
    public static String name(Reference branch) {
        if (branch == null) {
            return null;
        }
        AtomicReference<String> outStr = new AtomicReference<>();
        Error.throwIfNeeded(jniName(outStr, branch.getRawPointer()));
        return outStr.get();
    }
    public static Reference upstream(Reference branch) {
        if (branch == null) {
            return null;
        }
        AtomicLong outRef = new AtomicLong();
        int e = jniUpstream(outRef, branch.getRawPointer());
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Reference(false, outRef.get());
    }
    public static void setUpstream(@Nonnull Reference branch, @Nullable String upstreamName) {
        Error.throwIfNeeded(jniSetUpstream(branch.getRawPointer(), upstreamName));
    }
    public static String upstreamName(Repository repo, String refname) {
        Buf outBuf = new Buf();
        int e = jniUpstreamName(outBuf, repo.getRawPointer(), refname);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return outBuf.toString();
    }
    public static boolean isHead(Reference branch) {
        if (branch == null) {
            return false;
        }
        int e = jniIsHead(branch.getRawPointer());
        Error.throwIfNeeded(e);
        return e == 1;
    }
    public static boolean isCheckedOut(Reference branch) {
        int e = jniIsCheckedOut(branch.getRawPointer());
        Error.throwIfNeeded(e);
        return e == 1;
    }
    public static String remoteName(Repository repo, String canonicalBranchName) {
        if (repo == null) {
            return null;
        }
        Buf outBuf = new Buf();
        int e = jniRemoteName(outBuf, repo.getRawPointer(), canonicalBranchName);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return outBuf.toString();
    }
    public static String upstreamRemote(Repository repo, String refName) {
        Buf outBuf = new Buf();
        int e = jniUpstreamRemote(outBuf, repo.getRawPointer(), refName);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        return outBuf.toString();
    }
    public enum BranchType implements IBitEnum {
        INVALID(0),
        LOCAL(1),
        REMOTE(2),
        ALL(3);
        private final int _bit;
        BranchType(int bit) {
            _bit = bit;
        }
        public static BranchType valueOf(int iVal) {
            return IBitEnum.valueOf(iVal, BranchType.class, INVALID);
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public static class Iterator {
        private final AtomicLong _ptr = new AtomicLong();
        Iterator(long rawPointer) {
            _ptr.set(rawPointer);
        }
        public static Iterator create(Repository repo, BranchType flag) {
            AtomicLong outRef = new AtomicLong();
            Error.throwIfNeeded(jniIteratorNew(outRef, repo.getRawPointer(), flag.ordinal()));
            return new Iterator(outRef.get());
        }
        @Override
        protected void finalize() throws Throwable {
            if (_ptr.get() != 0) {
                jniIteratorFree(_ptr.get());
            }
            super.finalize();
        }
        public Map.Entry<Reference, BranchType> next() {
            AtomicLong outRef = new AtomicLong();
            AtomicInteger outType = new AtomicInteger();
            int e = jniNext(outRef, outType, _ptr.get());
            if (e == GitException.ErrorCode.ITEROVER.getCode()) {
                return null;
            }
            Error.throwIfNeeded(e);
            Reference ref = new Reference(true, outRef.get());
            BranchType type = BranchType.valueOf(outType.get());
            return new HashMap.SimpleImmutableEntry<>(ref, type);
        }
    }
}
