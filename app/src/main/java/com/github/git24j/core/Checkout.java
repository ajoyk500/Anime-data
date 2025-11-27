package com.github.git24j.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Checkout {
    static native int jniHead(long repoPtr, long opts);
    static native int jniIndex(long repoPtr, long index, long opts);
    static native int jniInitOptions(long opts, int version);
    static native String jniOptionsGetAncestorLabel(long opts);
    static native long jniOptionsGetBaseline(long opts);
    static native long jniOptionsGetBaselineIndex(long opts);
    static native int jniOptionsGetDirMode(long opts);
    static native int jniOptionsGetDisableFilters(long opts);
    static native int jniOptionsGetFileMode(long opts);
    static native int jniOptionsGetFileOpenFlags(long opts);
    static native int jniOptionsGetNotifyFlags(long opts);
    static native String jniOptionsGetOurLabel(long opts);
    static native void jniOptionsGetPaths(long opts, List<String> outPathList);
    static native int jniOptionsGetStrategy(long opts);
    static native String jniOptionsGetTargetDirectory(long opts);
    static native String jniOptionsGetTheirLable(long opts);
    static native int jniOptionsGetVersion(long opts);
    static native int jniOptionsNew(AtomicLong outPots, int version);
    static native void jniOptionsSetAncestorLabel(long opts, String ancestorLabel);
    static native void jniOptionsSetBaseline(long opts, long baseline);
    static native void jniOptionsSetBaselineIndex(long opts, long baselineIndex);
    static native void jniOptionsSetDirMode(long opts, int mode);
    static native void jniOptionsSetDisableFilters(long opts, int disalbeFilters);
    static native void jniOptionsSetFileMode(long opts, int mode);
    static native void jniOptionsSetFileOpenFlags(long opts, int flags);
    static native void jniOptionsSetNotifyCb(long optsPtr, Internals.ISJJJCallback notifyCb);
    static native void jniOptionsSetNotifyFlags(long opts, int flags);
    static native void jniOptionsSetOurLabel(long opts, String ourLabel);
    static native void jniOptionsSetPaths(long opts, String[] paths);
    static native void jniOptionsSetPerfdataCb(long optsPtr, PerfdataCb perfdataCb);
    static native void jniOptionsSetProgressCb(long optsPtr, ProgressCb progressCb);
    static native void jniOptionsSetStrategy(long opts, int strategy);
    static native void jniOptionsSetTargetDirectory(long opts, String targetDirectory);
    static native void jniOptionsSetTheirLable(long opts, String theirLabel);
    static native void jniOptionsSetVersion(long opts, int version);
    static native int jniTree(long repoPtr, long treeish, long opts);
    public static int head(@Nonnull Repository repo, @Nullable Options opts) {
        int e = jniHead(repo.getRawPointer(), opts == null ? 0 : opts.getRawPointer());
        Error.throwIfNeeded(e);
        return e;
    }
    public static int index(
            @Nonnull Repository repo, @Nullable Index index, @Nullable Options opts) {
        int e =
                jniIndex(
                        repo.getRawPointer(),
                        index == null ? 0 : index.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer());
        Error.throwIfNeeded(e);
        return e;
    }
    public static int tree(
            @Nonnull Repository repo, @Nullable GitObject treeish, @Nullable Options opts) {
        int e =
                jniTree(
                        repo.getRawPointer(),
                        treeish == null ? 0 : treeish.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer());
        Error.throwIfNeeded(e);
        return e;
    }
    public enum StrategyT implements IBitEnum {
        SAFE(0),
        FORCE(1 << 1),
        RECREATE_MISSING(1 << 2),
        ALLOW_CONFLICTS(1 << 4),
        REMOVE_UNTRACKED(1 << 5),
        REMOVE_IGNORED(1 << 6),
        UPDATE_ONLY(1 << 7),
        DONT_UPDATE_INDEX(1 << 8),
        NO_REFRESH(1 << 9),
        SKIP_UNMERGED(1 << 10),
        USE_OURS(1 << 11),
        USE_THEIRS(1 << 12),
        DISABLE_PATHSPEC_MATCH(1 << 13),
        SKIP_LOCKED_DIRECTORIES(1 << 18),
        DONT_OVERWRITE_IGNORED(1 << 19),
        CONFLICT_STYLE_MERGE(1 << 20),
        CONFLICT_STYLE_DIFF3(1 << 21),
        DONT_REMOVE_EXISTING(1 << 22),
        DONT_WRITE_INDEX(1 << 23),
        DRY_RUN(1 << 24),
        CONFLICT_STYLE_ZDIFF3(1 << 25),
        NONE(1 << 30),
        UPDATE_SUBMODULES(1 << 16),
        UPDATE_SUBMODULES_IF_CHANGED(1 << 17);
        private final int _bit;
        StrategyT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum NotifyT implements IBitEnum {
        NONE(0),
        CONFLICT(1 << 0),
        DIRTY(1 << 1),
        UPDATED(1 << 2),
        UNTRACKED(1 << 3),
        IGNORED(1 << 4),
        ALL(0x0FFFF);
        private final int _bit;
        NotifyT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    @FunctionalInterface
    public interface NotifyCb {
        int accept(
                NotifyT why, String path, Diff.File baseline, Diff.File target, Diff.File workdir);
    }
    @FunctionalInterface
    public interface ProgressCb {
        void accept(@CheckForNull String path, int completedSteps, int totalSteps);
    }
    @FunctionalInterface
    public interface PerfdataCb {
        void accept(int mkdirCalls, int statCalls, int chmodCalls);
    }
    public static class Options extends CAutoReleasable {
        public static int GIT_CHECKOUT_OPTIONS_VERSION = 1;
        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static Options create(int version) {
            Options opts = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(opts._rawPtr, version));
            return opts;
        }
        public static Options defaultOptions() {
            return create(GIT_CHECKOUT_OPTIONS_VERSION);
        }
        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }
        public int getVersion() {
            return jniOptionsGetVersion(getRawPointer());
        }
        public void setVersion(int ver) {
            jniOptionsSetVersion(getRawPointer(), ver);
        }
        @Nonnull
        public EnumSet<StrategyT> getStrategy() {
            return IBitEnum.parse(jniOptionsGetStrategy(getRawPointer()), StrategyT.class);
        }
        public void setStrategy(@Nonnull EnumSet<StrategyT> strategies) {
            jniOptionsSetStrategy(getRawPointer(), IBitEnum.bitOrAll(strategies));
        }
        public boolean getDisableFilter() {
            return jniOptionsGetDisableFilters(getRawPointer()) == 1;
        }
        public void setDisableFilter(boolean disableFilter) {
            jniOptionsSetDisableFilters(getRawPointer(), disableFilter ? 1 : 0);
        }
        public int getDirMode() {
            return jniOptionsGetDirMode(getRawPointer());
        }
        public void setDirMode(int mode) {
            jniOptionsSetDirMode(getRawPointer(), mode);
        }
        public int getFileMode() {
            return jniOptionsGetFileMode(getRawPointer());
        }
        public void setFileMode(int mode) {
            jniOptionsSetFileMode(getRawPointer(), mode);
        }
        public int getOpenFlags() {
            return jniOptionsGetFileOpenFlags(getRawPointer());
        }
        public void setOpenFlags(int flags) {
            jniOptionsSetFileOpenFlags(getRawPointer(), flags);
        }
        public EnumSet<NotifyT> getNotifyFlags() {
            int flags = jniOptionsGetNotifyFlags(getRawPointer());
            EnumSet<NotifyT> candidates = EnumSet.allOf(NotifyT.class);
            candidates.remove(NotifyT.NONE);
            candidates.remove(NotifyT.ALL);
            EnumSet<NotifyT> res = IBitEnum.parse(flags, candidates);
            if (res == null) {
                return EnumSet.of(NotifyT.NONE);
            }
            if (res.size() == candidates.size()) {
                return EnumSet.of(NotifyT.ALL);
            }
            return res;
        }
        public void setNotifyFlags(EnumSet<NotifyT> flags) {
            jniOptionsSetNotifyFlags(getRawPointer(), IBitEnum.bitOrAll(flags));
        }
        public void setNotifyCb(@Nonnull NotifyCb callback) {
            jniOptionsSetNotifyCb(
                    getRawPointer(),
                    (why, s, basePtr, targePtr, workdirPtr) ->
                            callback.accept(
                                    IBitEnum.valueOf(why, NotifyT.class),
                                    s,
                                    Diff.File.ofWeak(basePtr),
                                    Diff.File.ofWeak(targePtr),
                                    Diff.File.ofWeak(workdirPtr)));
        }
        public void setProgressCb(@Nonnull ProgressCb callback) {
            jniOptionsSetProgressCb(getRawPointer(), callback);
        }
        public void setPerfdataCb(@Nonnull PerfdataCb callback) {
            jniOptionsSetPerfdataCb(getRawPointer(), callback);
        }
        public List<String> getPaths() {
            List<String> out = new ArrayList<>();
            jniOptionsGetPaths(getRawPointer(), out);
            return out;
        }
        public void setPaths(@Nonnull String[] paths) {
            jniOptionsSetPaths(getRawPointer(), paths);
        }
        @Nullable
        public Tree getBaseline() {
            long ptr = jniOptionsGetBaseline(getRawPointer());
            return ptr == 0 ? null : new Tree(true, ptr);
        }
        public void setBaseline(@Nonnull Tree baseline) {
            jniOptionsSetBaseline(getRawPointer(), baseline.getRawPointer());
        }
        @Nullable
        public Index getBaselineIndex() {
            long ptr = jniOptionsGetBaselineIndex(getRawPointer());
            return ptr == 0 ? null : new Index(true, ptr);
        }
        public void setBaselineIndex(@Nonnull Index baselineIndex) {
            jniOptionsSetBaselineIndex(getRawPointer(), baselineIndex.getRawPointer());
        }
        public String getTargetDirectory() {
            return jniOptionsGetTargetDirectory(getRawPointer());
        }
        public void setTargetDirectory(@Nonnull String targetDirectory) {
            jniOptionsSetTargetDirectory(getRawPointer(), targetDirectory);
        }
        public String getAncestorLabel() {
            return jniOptionsGetAncestorLabel(getRawPointer());
        }
        public void setAncestorLabel(@Nonnull String ancestorLabel) {
            jniOptionsSetAncestorLabel(getRawPointer(), ancestorLabel);
        }
        public String getOurLabel() {
            return jniOptionsGetOurLabel(getRawPointer());
        }
        public void setOurLabel(@Nonnull String ourLabel) {
            jniOptionsSetOurLabel(getRawPointer(), ourLabel);
        }
        public String getTheirLabel() {
            return jniOptionsGetTheirLable(getRawPointer());
        }
        public void setTheirLabel(@Nonnull String theirLabel) {
            jniOptionsSetTheirLable(getRawPointer(), theirLabel);
        }
    }
}
