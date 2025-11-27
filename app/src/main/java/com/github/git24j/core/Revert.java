package com.github.git24j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicLong;

public final class Revert {
    static native int jniCommit(
            AtomicLong out,
            long repoPtr,
            long revertCommit,
            long ourCommit,
            int mainline,
            long mergeOptions);
    static native void jniOptionsFree(long optionsPtr);
    static native long jniOptionsGetCheckoutOpts(long optionsPtr);
    static native int jniOptionsGetMainline(long optionsPtr);
    static native long jniOptionsGetMergeOpts(long optionsPtr);
    static native int jniOptionsNew(AtomicLong outPtr, int version);
    static native void jniOptionsSetMainline(long optionsPtr, int mainline);
    static native int jniRevert(long repoPtr, long commit, long givenOpts);
    public static Index revertCommit(
            @Nonnull Repository repo,
            @Nonnull Commit revertCommit,
            @Nonnull Commit ourCommit,
            int mainline,
            @Nullable Merge.Options mergeOptions) {
        Index outIdx = new Index(false, 0);
        int e =
                jniCommit(
                        ourCommit._rawPtr,
                        repo.getRawPointer(),
                        revertCommit.getRawPointer(),
                        ourCommit.getRawPointer(),
                        mainline,
                        mergeOptions == null ? 0 : mergeOptions.getRawPointer());
        Error.throwIfNeeded(e);
        return outIdx;
    }
    public void revert(
            @Nonnull Repository repo, @Nonnull Commit commit, @Nullable Options revertOpts) {
        int e =
                jniRevert(
                        repo.getRawPointer(),
                        commit.getRawPointer(),
                        revertOpts == null ? 0 : revertOpts.getRawPointer());
        Error.throwIfNeeded(e);
    }
    public static class Options extends CAutoReleasable {
        public static int VERSION = 1;
        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }
        public Options create(int version) {
            Options opts = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(opts._rawPtr, version));
            return opts;
        }
        public Options createDefault() {
            return create(VERSION);
        }
        public int getMainline() {
            return jniOptionsGetMainline(getRawPointer());
        }
        public void setMainline(int mainline) {
            jniOptionsSetMainline(getRawPointer(), mainline);
        }
        public Merge.Options getMergeOpts() {
            return new Merge.Options(true, jniOptionsGetMergeOpts(getRawPointer()));
        }
        public Checkout.Options getCheckoutOpts() {
            return new Checkout.Options(true, jniOptionsGetCheckoutOpts(getRawPointer()));
        }
    }
}
