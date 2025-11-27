package com.github.git24j.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Status {
    static native long jniByindex(long statuslist, int idx);
    static native long jniEntryGetHeadToIndex(long entryPtr);
    static native long jniEntryGetIndexToWorkdir(long entryPtr);
    static native int jniEntryGetStatus(long entryPtr);
    static native int jniFile(AtomicInteger statusFlags, long repoPtr, String path);
    static native int jniInitOptions(long opts, int version);
    static native int jniListEntrycount(long statuslist);
    static native void jniListFree(long statuslist);
    static native int jniListNew(AtomicLong out, long repoPtr, long opts);
    static native long jniOptionsGetBaseline(long optionsPtr);
    static native int jniOptionsGetFlags(long optionsPtr);
    static native void jniOptionsGetPathspec(long optionsPtr, List<String> outPathSpec);
    static native int jniOptionsGetShow(long optionsPtr);
    static native int jniOptionsGetVersion(long optionsPtr);
    static native int jniOptionsNew(AtomicLong outOpts, int version);
    static native void jniOptionsSetBaseline(long optionsPtr, long baseline);
    static native void jniOptionsSetFlags(long optionsPtr, int flags);
    static native void jniOptionsSetPathspec(long optionsPtr, String[] pathspec);
    static native void jniOptionsSetShow(long optionsPtr, int show);
    static native void jniOptionsSetVersion(long optionsPtr, int version);
    static native int jniShouldIgnore(AtomicInteger ignored, long repoPtr, String path);
    public static boolean shouldIgnore(@Nonnull Repository repo, @Nonnull String path) {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(jniShouldIgnore(out, repo.getRawPointer(), path));
        return out.get() == 1;
    }
    public static EnumSet<StatusT> file(@Nonnull Repository repo, @Nonnull String path) {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(jniFile(out, repo.getRawPointer(), path));
        return IBitEnum.parse(out.get(), StatusT.class);
    }
    public enum StatusT implements IBitEnum {
        CURRENT(0),
        INDEX_NEW(1 << 0),
        INDEX_MODIFIED(1 << 1),
        INDEX_DELETED(1 << 2),
        INDEX_RENAMED(1 << 3),
        INDEX_TYPECHANGE(1 << 4),
        WT_NEW(1 << 7),
        WT_MODIFIED(1 << 8),
        WT_DELETED(1 << 9),
        WT_TYPECHANGE(1 << 10),
        WT_RENAMED(1 << 11),
        WT_UNREADABLE(1 << 12),
        IGNORED(1 << 14),
        CONFLICTED(1 << 15),
        ;
        private final int _bit;
        StatusT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum ShowT implements IBitEnum {
        INDEX_AND_WORKDIR(0),
        INDEX_ONLY(1),
        WORKDIR_ONLY(2),
        ;
        private final int _bit;
        ShowT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum OptT implements IBitEnum {
        OPT_INCLUDE_UNTRACKED(1 << 0),
        OPT_INCLUDE_IGNORED(1 << 1),
        OPT_INCLUDE_UNMODIFIED(1 << 2),
        OPT_EXCLUDE_SUBMODULES(1 << 3),
        OPT_RECURSE_UNTRACKED_DIRS(1 << 4),
        OPT_DISABLE_PATHSPEC_MATCH(1 << 5),
        OPT_RECURSE_IGNORED_DIRS(1 << 6),
        OPT_RENAMES_HEAD_TO_INDEX(1 << 7),
        OPT_RENAMES_INDEX_TO_WORKDIR(1 << 8),
        OPT_SORT_CASE_SENSITIVELY(1 << 9),
        OPT_SORT_CASE_INSENSITIVELY(1 << 10),
        OPT_RENAMES_FROM_REWRITES(1 << 11),
        OPT_NO_REFRESH(1 << 12),
        OPT_UPDATE_INDEX(1 << 13),
        OPT_INCLUDE_UNREADABLE(1 << 14),
        OPT_INCLUDE_UNREADABLE_AS_UNTRACKED(1 << 15),
        ;
        private final int _bit;
        OptT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public static class Options extends CAutoReleasable {
        public static final int CURRENT_VERSION = 1;
        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        public static Options create(int version) {
            Options out = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(out._rawPtr, version));
            return out;
        }
        public static Options newDefault() {
            return create(CURRENT_VERSION);
        }
        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }
        public int getVersion() {
            return jniOptionsGetVersion(getRawPointer());
        }
        public void setVersion(int version) {
            jniOptionsSetVersion(getRawPointer(), version);
        }
        public int getShow() {
            return jniOptionsGetShow(getRawPointer());
        }
        public void setShow(int show) {
            jniOptionsSetShow(getRawPointer(), show);
        }
        public EnumSet<OptT> getFlags() {
            return IBitEnum.parse(jniOptionsGetFlags(getRawPointer()), OptT.class);
        }
        public void setFlags(EnumSet<OptT> flags) {
            jniOptionsSetFlags(getRawPointer(), IBitEnum.bitOrAll(flags));
        }
        public List<String> getPathspec() {
            List<String> out = new ArrayList<>();
            jniOptionsGetPathspec(getRawPointer(), out);
            return out;
        }
        public void setPathspec(List<String> pathspec) {
            jniOptionsSetPathspec(getRawPointer(), pathspec.toArray(new String[0]));
        }
        public Tree getBaseline() {
            return new Tree(true, jniOptionsGetBaseline(getRawPointer()));
        }
        public void setBaseline(@Nullable Tree baseline) {
            jniOptionsSetBaseline(getRawPointer(), baseline == null ? 0 : baseline.getRawPointer());
        }
    }
    public static class StatusList extends CAutoReleasable {
        protected StatusList(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static StatusList listNew(@Nonnull Repository repo, @Nullable Options opts) {
            StatusList out = new StatusList(false, 0);
            Error.throwIfNeeded(
                    jniListNew(
                            out._rawPtr,
                            repo.getRawPointer(),
                            opts == null ? 0 : opts.getRawPointer()));
            return out;
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniListFree(cPtr);
        }
        public int entryCount() {
            return jniListEntrycount(getRawPointer());
        }
        @Nonnull
        public Entry byIndex(int idx) {
            long ptr = jniByindex(getRawPointer(), idx);
            if (ptr == 0) {
                throw new IndexOutOfBoundsException(
                        String.format(
                                "index: %d is out of the boundary, total: %d", idx, entryCount()));
            }
            return new Entry(ptr);
        }
    }
    public static class Entry extends CAutoReleasable {
        protected Entry(long rawPtr) {
            super(true, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            throw new RuntimeException("Entries are owned by StatusList and should not be freed");
        }
        public EnumSet<StatusT> getStatus() {
            return IBitEnum.parse(jniEntryGetStatus(getRawPointer()), StatusT.class);
        }
        @Nullable
        public Diff.Delta getHeadToIndex() {
            return Diff.Delta.of(jniEntryGetHeadToIndex(getRawPointer()));
        }
        @Nullable
        public Diff.Delta getIndexToWorkdir() {
            return Diff.Delta.of(jniEntryGetIndexToWorkdir(getRawPointer()));
        }
    }
}
