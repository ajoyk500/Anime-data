package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Cherrypick {
    static native int jniCherrypick(long repoPtr, long commit, long cherrypickOptions);
    static native int jniCommit(
            AtomicLong out,
            long repoPtr,
            long cherrypickCommit,
            long ourCommit,
            int mainline,
            long mergeOptions);
    static native void jniOptionsFree(long optsPtr);
    static native int jniOptionsNew(AtomicLong outOpts, int version);
    static native int jniOptionsGetMainline(long optionsPtr);
    static native void jniOptionsSetMainline(long optionsPtr, int mainline);
    static native long jniOptionsGetMergeOpts(long optionsPtr);
    static native long jniOptionsGetCheckoutOpts(long optionsPtr);
    @Nonnull
    public static Index commit(
            @Nonnull Repository repo,
            @Nonnull Commit cherryPickCommit,
            @Nonnull Commit ourCommit,
            int mainline,
            @Nullable Merge.Options mergeOptions) {
        Index out = new Index(false, 0);
        int e =
                jniCommit(
                        out._rawPtr,
                        repo.getRawPointer(),
                        cherryPickCommit.getRawPointer(),
                        ourCommit.getRawPointer(),
                        mainline,
                        mergeOptions == null ? 0 : mergeOptions.getRawPointer());
        Error.throwIfNeeded(e);
        return out;
    }
    public static void cherrypick(
            @Nonnull Repository repo, @Nonnull Commit commit, @Nullable Options options) {
        int e =
                jniCherrypick(
                        repo.getRawPointer(),
                        commit.getRawPointer(),
                        options == null ? 0 : options.getRawPointer());
        Error.throwIfNeeded(e);
    }
    public static class Options extends CAutoReleasable {
        public static final int VERSION = 1;
        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static Options create(int version) {
            Options out = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(out._rawPtr, version));
            return out;
        }
        @Nonnull
        public static Options createDefault() {
            return create(VERSION);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
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
