package com.github.git24j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Reset {
    static native int jniDefault(long repoPtr, long target, String[] pathspecs);
    static native int jniFromAnnotated(long repoPtr, long commit, int resetType, long checkoutOpts);
    static native int jniReset(long repoPtr, long target, int resetType, long checkoutOpts);
    public static void resetDefault(
            @Nonnull Repository repo, @Nonnull GitObject target, String[] pathspecs) {
        int e = jniDefault(repo.getRawPointer(), target.getRawPointer(), pathspecs);
        Error.throwIfNeeded(e);
    }
    public static void reset(
            @Nonnull Repository repo,
            @Nonnull GitObject target,
            @Nonnull ResetT resetType,
            @Nullable Checkout.Options checkoutOpts) {
        int e =
                jniReset(
                        repo.getRawPointer(),
                        target.getRawPointer(),
                        resetType.getBit(),
                        checkoutOpts == null ? 0 : checkoutOpts.getRawPointer());
        Error.throwIfNeeded(e);
    }
    public static void resetFromAnnotated(
            @Nonnull Repository repo,
            @Nonnull AnnotatedCommit commit,
            @Nonnull ResetT resetType,
            @Nullable Checkout.Options checkoutOpts) {
        int e =
                jniFromAnnotated(
                        repo.getRawPointer(),
                        commit.getRawPointer(),
                        resetType.getBit(),
                        checkoutOpts == null ? 0 : checkoutOpts.getRawPointer());
        Error.throwIfNeeded(e);
    }
    public enum ResetT implements IBitEnum {
        SOFT(1),
        MIXED(2),
        HARD(3);
        private final int _bit;
        ResetT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
}
