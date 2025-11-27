package com.github.git24j.core;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Stash {
    static native int jniApply(long repoPtr, int index, long options);
    static native void jniApplyOptionsFree(long applyOptionsPtr);
    static native long jniApplyOptionsGetCheckoutOptions(long apply_optionsPtr);
    static native int jniApplyOptionsGetFlags(long apply_optionsPtr);
    static native int jniApplyOptionsNew(AtomicLong outPtr, int version);
    static native void jniApplyOptionsSetFlags(long apply_optionsPtr, int flags);
    static native void jniApplyOptionsSetProgressCb(
            long apply_optionsPtr, Internals.ICallback progressCb);
    static native int jniDrop(long repoPtr, int index);
    static native int jniForeach(long repoPtr, Internals.ISBarrCalback callback);
    static native int jniPop(long repoPtr, int index, long options);
    static native int jniSave(Oid out, long repoPtr, long stasher, String message, int flags);
    public static void apply(@Nonnull Repository repo, int index, @Nullable ApplyOptions options) {
        int e =
                jniApply(
                        repo.getRawPointer(), index, options == null ? 0 : options.getRawPointer());
        Error.throwIfNeeded(e);
    }
    public static void drop(@Nonnull Repository repo, int index) {
        Error.throwIfNeeded(jniDrop(repo.getRawPointer(), index));
    }
    public static void foreach(@Nonnull Repository repo, @Nonnull StashCb callback) {
        int e =
                jniForeach(
                        repo.getRawPointer(),
                        (index, message, rawId) -> callback.accept(index, message, Oid.of(rawId)));
        Error.throwIfNeeded(e);
    }
    public static void pop(@Nonnull Repository repo, int index, @Nullable ApplyOptions options) {
        int e = jniPop(repo.getRawPointer(), index, options == null ? 0 : options.getRawPointer());
        Error.throwIfNeeded(e);
    }
    public static Oid save(
            @Nonnull Repository repo,
            @Nonnull Signature stasher,
            @Nonnull String message,
            @Nullable EnumSet<Flags> flags) {
        Oid out = new Oid();
        int e =
                jniSave(
                        out,
                        repo.getRawPointer(),
                        stasher.getRawPointer(),
                        message,
                        IBitEnum.bitOrAll(flags));
        Error.throwIfNeeded(e);
        return out;
    }
    public enum ApplyFlags implements IBitEnum {
        DEFAULT(0),
        REINSTATE_INDEX(1 << 0);
        private final int _bit;
        ApplyFlags(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum Flags implements IBitEnum {
        DEFAULT(0),
        KEEP_INDEX(1 << 0),
        INCLUDE_UNTRACKED(1 << 1),
        INCLUDE_IGNORED(1 << 2);
        private final int _bit;
        Flags(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum ProgressT implements IBitEnum {
        NONE(0),
        LOADING_STASH(1),
        ANALYZE_INDEX(2),
        ANALYZE_MODIFIED(3),
        ANALYZE_UNTRACKED(4),
        CHECKOUT_UNTRACKED(5),
        CHECKOUT_MODIFIED(6),
        DONE(7);
        private final int _bit;
        ProgressT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public interface StashCb {
        int accept(int index, String message, Oid stashId);
    }
    @FunctionalInterface
    public interface ProgressCb {
        int accept(ProgressT progress);
    }
    public static class ApplyOptions extends CAutoReleasable {
        public static final int VERSION = 1;
        protected ApplyOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static ApplyOptions create(int version) {
            ApplyOptions options = new ApplyOptions(false, 0);
            int e = jniApplyOptionsNew(options._rawPtr, version);
            Error.throwIfNeeded(e);
            return options;
        }
        @Nonnull
        public static ApplyOptions createDefault() {
            return create(VERSION);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniApplyOptionsFree(cPtr);
        }
        @Nonnull
        public EnumSet<ApplyFlags> getFlags() {
            int r = jniApplyOptionsGetFlags(getRawPointer());
            return IBitEnum.parse(r, ApplyFlags.class);
        }
        public void setFlags(EnumSet<ApplyFlags> flags) {
            jniApplyOptionsSetFlags(getRawPointer(), IBitEnum.bitOrAll(flags));
        }
        @Nonnull
        public Checkout.Options getCheckoutOptions() {
            long ptr = jniApplyOptionsGetCheckoutOptions(getRawPointer());
            return new Checkout.Options(true, ptr);
        }
        public void setProgressCb(@Nonnull ProgressCb progressCb) {
            jniApplyOptionsSetProgressCb(
                    getRawPointer(),
                    x -> progressCb.accept(IBitEnum.valueOf(x, ProgressT.class, ProgressT.NONE)));
        }
    }
}
