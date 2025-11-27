package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;
import static com.github.git24j.core.Internals.OidArray;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Merge {
    static native int jniAnalysis(
            AtomicInteger analysisOut,
            AtomicInteger preferenceOut,
            long repoPtr,
            long[] theirHeads);
    static native int jniAnalysisForRef(
            AtomicInteger analysisOut,
            AtomicInteger preferenceOut,
            long repoPtr,
            long ourRefPtr,
            long[] theirHeads);;
    static native int jniBase(Oid out, long repoPtr, Oid one, Oid two);
    static native int jniBaseMany(Oid outOid, long repoPtr, Oid[] inputArray);
    static native int jniBaseOctopus(Oid outOid, long repoPtr, Oid[] intputArray);
    static native int jniBases(OidArray outOids, long repoPtr, Oid one, Oid two);
    static native int jniBasesMany(OidArray outOids, long repoPtr, Oid[] inputArray);
    static native int jniCommits(
            AtomicLong out, long repoPtr, long ourCommit, long theirCommit, long opts);
    static native int jniFile(
            AtomicLong out, long ancestorPtr, long oursPtr, long theirsPtr, long optsPtr);
    static native int jniFileInitInput(long opts, int version);
    static native int jniFileInitOptions(long opts, int version);
    static native void jniFileInputFree(long optsPtr);
    static native int jniFileInputGetMode(long file_inputPtr);
    static native String jniFileInputGetPath(long file_inputPtr);
    static native String jniFileInputGetPtr(long file_inputPtr);
    static native int jniFileInputGetSize(long file_inputPtr);
    static native int jniFileInputGetVersion(long file_inputPtr);
    static native int jniFileInputNew(AtomicLong outOpts, int version);
    static native void jniFileInputSetMode(long file_inputPtr, int mode);
    static native void jniFileInputSetPath(long file_inputPtr, String path);
    static native void jniFileInputSetPtr(long file_inputPtr, String ptr);
    static native void jniFileInputSetSize(long file_inputPtr, int size);
    static native void jniFileInputSetVersion(long file_inputPtr, int version);
    static native void jniFileOptionsFree(long opts);
    static native String jniFileOptionsGetAncestorLabel(long file_optionsPtr);
    static native int jniFileOptionsGetFavor(long file_optionsPtr);
    static native int jniFileOptionsGetFlags(long file_optionsPtr);
    static native int jniFileOptionsGetMarkerSize(long file_optionsPtr);
    static native String jniFileOptionsGetOurLabel(long file_optionsPtr);
    static native String jniFileOptionsGetTheirLabel(long file_optionsPtr);
    static native int jniFileOptionsGetVersion(long file_optionsPtr);
    static native int jniFileOptionsNew(AtomicLong outOpts, int version);
    static native void jniFileOptionsSetAncestorLabel(long file_optionsPtr, String ancestorLabel);
    static native void jniFileOptionsSetFavor(long file_optionsPtr, int favor);
    static native void jniFileOptionsSetFlags(long file_optionsPtr, int flags);
    static native void jniFileOptionsSetMarkerSize(long file_optionsPtr, int markerSize);
    static native void jniFileOptionsSetOurLabel(long file_optionsPtr, String ourLabel);
    static native void jniFileOptionsSetTheirLabel(long file_optionsPtr, String theirLabel);
    static native void jniFileOptionsSetVersion(long file_optionsPtr, int version);
    static native void jniFileResultFree(long resultPtr);
    static native int jniFileResultGetAutomergeable(long file_resultPtr);
    static native int jniFileResultGetLen(long file_resultPtr);
    static native int jniFileResultGetMode(long file_resultPtr);
    static native String jniFileResultGetPath(long file_resultPtr);
    static native String jniFileResultGetPtr(long file_resultPtr);
    static native int jniInitOptions(long opts, int version);
    static native int jniMerge(
            long repoPtr, long[] theirHeads, long mergeOptsPtr, long checkoutOpts);
    static native void jniOptionsFree(long optsPtr);
    static native String jniOptionsGetDefaultDriver(long optionsPtr);
    static native int jniOptionsGetFileFavor(long optionsPtr);
    static native int jniOptionsGetFileFlags(long optionsPtr);
    static native int jniOptionsGetFlags(long optionsPtr);
    static native long jniOptionsGetMetric(long optionsPtr);
    static native int jniOptionsGetRecursionLimit(long optionsPtr);
    static native int jniOptionsGetRenameThreshold(long optionsPtr);
    static native int jniOptionsGetTargetLimit(long optionsPtr);
    static native int jniOptionsGetVersion(long optionsPtr);
    static native int jniOptionsNew(AtomicLong outOpts, int version);
    static native void jniOptionsSetDefaultDriver(long optionsPtr, String defaultDriver);
    static native void jniOptionsSetFileFavor(long optionsPtr, int fileFavor);
    static native void jniOptionsSetFileFlags(long optionsPtr, int fileFlags);
    static native void jniOptionsSetFlags(long optionsPtr, int flags);
    static native void jniOptionsSetMetric(long optionsPtr, long metric);
    static native void jniOptionsSetRecursionLimit(long optionsPtr, int recursionLimit);
    static native void jniOptionsSetRenameThreshold(long optionsPtr, int renameThreshold);
    static native void jniOptionsSetTargetLimit(long optionsPtr, int targetLimit);
    static native void jniOptionsSetVersion(long optionsPtr, int version);
    static native int jniTrees(
            AtomicLong out,
            long repoPtr,
            long ancestorTree,
            long ourTree,
            long theirTree,
            long opts);
    @Nullable
    public static Oid base(@Nonnull Repository repo, @Nonnull Oid one, @Nonnull Oid two) {
        Oid out = new Oid();
        int e = jniBase(out, repo.getRawPointer(), one, two);
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }
    @Nonnull
    public static List<Oid> bases(@Nonnull Repository repo, @Nonnull Oid one, @Nonnull Oid two) {
        OidArray outOids = new OidArray();
        int e = jniBases(outOids, repo.getRawPointer(), one, two);
        if (ENOTFOUND.getCode() == e) {
            return Collections.emptyList();
        }
        Error.throwIfNeeded(e);
        return outOids.getOids();
    }
    @Nonnull
    public static Index trees(
            @Nonnull Repository repo,
            @Nullable Tree ancestorTree,
            @Nonnull Tree ourTree,
            @Nonnull Tree theirTree,
            @Nullable Options opts) {
        Index out = new Index(false, 0);
        Error.throwIfNeeded(
                jniTrees(
                        out._rawPtr,
                        repo.getRawPointer(),
                        ancestorTree == null ? 0 : ancestorTree.getRawPointer(),
                        ourTree.getRawPointer(),
                        theirTree.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer()));
        return out;
    }
    @Nonnull
    public static Index commits(
            @Nonnull Repository repo,
            @Nullable Commit ourCommit,
            @Nullable Commit theirCommit,
            @Nullable Options opts) {
        Index out = new Index(false, 0);
        Error.throwIfNeeded(
                jniCommits(
                        out._rawPtr,
                        repo.getRawPointer(),
                        ourCommit == null ? 0 : ourCommit.getRawPointer(),
                        theirCommit == null ? 0 : theirCommit.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer()));
        return out;
    }
    @Nonnull
    public static AnalysisPair analysis(
            @Nonnull Repository repo, @Nonnull List<AnnotatedCommit> theirHeads) {
        AtomicInteger outAnalysis = new AtomicInteger();
        AtomicInteger outPreference = new AtomicInteger();
        Error.throwIfNeeded(
                jniAnalysis(
                        outAnalysis,
                        outPreference,
                        repo.getRawPointer(),
                        theirHeads.stream().mapToLong(AnnotatedCommit::getRawPointer).toArray()));
        return new AnalysisPair(
                IBitEnum.parse(outAnalysis.get(), AnalysisT.class),
                IBitEnum.parse(outPreference.get(), PreferenceT.class),
                outAnalysis.get(),
                outPreference.get());
    }
    @Nonnull
    public static AnalysisPair analysisForRef(
            @Nonnull Repository repo,
            @Nullable Reference ourRef,
            @Nonnull List<AnnotatedCommit> theirHeads) {
        AtomicInteger outAnalysis = new AtomicInteger();
        AtomicInteger outPreference = new AtomicInteger();
        Error.throwIfNeeded(
                jniAnalysisForRef(
                        outAnalysis,
                        outPreference,
                        repo.getRawPointer(),
                        ourRef == null ? 0 : ourRef.getRawPointer(),
                        theirHeads.stream().mapToLong(AnnotatedCommit::getRawPointer).toArray()));
        return new AnalysisPair(
                IBitEnum.parse(outAnalysis.get(), AnalysisT.class),
                IBitEnum.parse(outPreference.get(), PreferenceT.class),
                outAnalysis.get(),
                outPreference.get());
    }
    @Nullable
    public static Oid baseMany(@Nonnull Repository repo, @Nonnull Oid[] inputArray) {
        Oid outOid = new Oid();
        int e = jniBaseMany(outOid, repo.getRawPointer(), inputArray);
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return outOid;
    }
    @Nonnull
    public static List<Oid> basesMany(@Nonnull Repository repo, @Nonnull Oid[] inputArray) {
        OidArray outOids = new OidArray();
        int e = jniBasesMany(outOids, repo.getRawPointer(), inputArray);
        if (ENOTFOUND.getCode() == e) {
            return Collections.emptyList();
        }
        Error.throwIfNeeded(e);
        return outOids.getOids();
    }
    @Nullable
    public static Oid baseOctopus(@Nonnull Repository repo, @Nonnull Oid[] inputArray) {
        Oid outOid = new Oid();
        int e = jniBaseOctopus(outOid, repo.getRawPointer(), inputArray);
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return outOid;
    }
    @Nonnull
    public static FileResult file(
            @Nonnull FileInput ancestor,
            @Nonnull FileInput ours,
            @Nonnull FileInput theirs,
            @Nullable FileOptions opts) {
        FileResult result = new FileResult(false, 0);
        Error.throwIfNeeded(
                jniFile(
                        result._rawPtr,
                        ancestor.getRawPointer(),
                        ours.getRawPointer(),
                        theirs.getRawPointer(),
                        CAutoReleasable.rawPtr(opts)));
        return result;
    }
    public static void merge(
            @Nonnull Repository repo,
            @Nonnull List<AnnotatedCommit> theirHeads,
            @Nullable Options mergeOpts,
            @Nullable Checkout.Options checkoutOpts) {
        Error.throwIfNeeded(
                jniMerge(
                        repo.getRawPointer(),
                        theirHeads.stream().mapToLong(CAutoReleasable::getRawPointer).toArray(),
                        CAutoReleasable.rawPtr(mergeOpts),
                        CAutoReleasable.rawPtr(checkoutOpts)));
    }
    public enum FlagT implements IBitEnum {
        FIND_RENAMES(1 << 0),
        FAIL_ON_CONFLICT(1 << 1),
        SKIP_REUC(1 << 2),
        NO_RECURSIVE(1 << 3);
        private final int _bit;
        FlagT(int bit) {
            this._bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum FileFavorT {
        NORMAL(0),
        OURS(1),
        THEIRS(2),
        UNION(3);
        private final int bit;
        FileFavorT(int bit) {
            this.bit = bit;
        }
    }
    public enum FileFlagT implements IBitEnum {
        DEFAULT(0),
        STYLE_MERGE(1 << 0),
        STYLE_DIFF3(1 << 1),
        SIMPLIFY_ALNUM(1 << 2),
        IGNORE_WHITESPACE(1 << 3),
        IGNORE_WHITESPACE_CHANGE(1 << 4),
        IGNORE_WHITESPACE_EOL(1 << 5),
        DIFF_PATIENCE(1 << 6),
        DIFF_MINIMAL(1 << 7);
        private final int _bit;
        FileFlagT(int bit) {
            this._bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum AnalysisT implements IBitEnum {
        NONE(0),
        NORMAL(1 << 0),
        UP_TO_DATE(1 << 1),
        FASTFORWARD(1 << 2),
        UNBORN(1 << 3);
        private final int _bit;
        AnalysisT(int bit) {
            this._bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum PreferenceT implements IBitEnum {
        NONE(0),
        NO_FASTFORWARD(1 << 0),
        FASTFORWARD_ONLY(1 << 1);
        private final int _bit;
        PreferenceT(int bit) {
            this._bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public static class FileInput extends CAutoReleasable {
        public static final int VERSION = 1;
        FileInput(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        public static FileInput createDefault() {
            return create(VERSION);
        }
        @Nonnull
        public static FileInput create(int version) {
            FileInput out = new FileInput(false, 0);
            Error.throwIfNeeded(jniFileInputNew(out._rawPtr, version));
            return out;
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniFileInputFree(cPtr);
        }
        public int getVersion() {
            return jniFileInputGetVersion(getRawPointer());
        }
        public void setVersion(int version) {
            jniFileInputSetVersion(getRawPointer(), version);
        }
        public String getPtr() {
            return jniFileInputGetPtr(getRawPointer());
        }
        public void setPtr(String ptr) {
            jniFileInputSetPtr(getRawPointer(), ptr);
            jniFileInputSetSize(getRawPointer(), ptr.length());
        }
        public int getSize() {
            return jniFileInputGetSize(getRawPointer());
        }
        public String getPath() {
            return jniFileInputGetPath(getRawPointer());
        }
        public void setPath(String path) {
            jniFileInputSetPath(getRawPointer(), path);
        }
        public int getMode() {
            return jniFileInputGetMode(getRawPointer());
        }
        public void setMode(int mode) {
            jniFileInputSetMode(getRawPointer(), mode);
        }
    }
    public static class FileOptions extends CAutoReleasable {
        FileOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static FileOptions create(int version) {
            FileOptions opts = new FileOptions(false, 0);
            Error.throwIfNeeded(jniFileOptionsNew(opts._rawPtr, version));
            return opts;
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniFileOptionsFree(cPtr);
        }
        public int getVersion() {
            return jniFileOptionsGetVersion(getRawPointer());
        }
        public void setVersion(int version) {
            jniFileOptionsSetVersion(getRawPointer(), version);
        }
        public String getAncestorLabel() {
            return jniFileOptionsGetAncestorLabel(getRawPointer());
        }
        public void setAncestorLabel(String ancestorLabel) {
            jniFileOptionsSetAncestorLabel(getRawPointer(), ancestorLabel);
        }
        public String getOurLabel() {
            return jniFileOptionsGetOurLabel(getRawPointer());
        }
        public void setOurLabel(String ourLabel) {
            jniFileOptionsSetOurLabel(getRawPointer(), ourLabel);
        }
        public String getTheirLabel() {
            return jniFileOptionsGetTheirLabel(getRawPointer());
        }
        public void setTheirLabel(String theirLabel) {
            jniFileOptionsSetTheirLabel(getRawPointer(), theirLabel);
        }
        public int getFavor() {
            return jniFileOptionsGetFavor(getRawPointer());
        }
        public void setFavor(int favor) {
            jniFileOptionsSetFavor(getRawPointer(), favor);
        }
        public int getFlags() {
            return jniFileOptionsGetFlags(getRawPointer());
        }
        public void setFlags(int flags) {
            jniFileOptionsSetFlags(getRawPointer(), flags);
        }
        public int getMarkerSize() {
            return jniFileOptionsGetMarkerSize(getRawPointer());
        }
        public void setMarkerSize(int markerSize) {
            jniFileOptionsSetMarkerSize(getRawPointer(), markerSize);
        }
    }
    public static class FileResult extends CAutoReleasable {
        protected FileResult(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniFileResultFree(cPtr);
        }
        public boolean getAutomergeable() {
            return jniFileResultGetAutomergeable(getRawPointer()) != 0;
        }
        public String getPath() {
            return jniFileResultGetPath(getRawPointer());
        }
        public int getMode() {
            return jniFileResultGetMode(getRawPointer());
        }
        public String getPtr() {
            return jniFileResultGetPtr(getRawPointer());
        }
        public int getLen() {
            return jniFileResultGetLen(getRawPointer());
        }
    }
    public static class Options extends CAutoReleasable {
        public static final int CURRENT_VERSION = 1;
        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }
        @Nonnull
        public static Options create() {
            return create(CURRENT_VERSION);
        }
        @Nonnull
        public static Options create(int version) {
            Options out = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(out._rawPtr, version));
            return out;
        }
        public int getVersion() {
            return jniOptionsGetVersion(getRawPointer());
        }
        public void setVersion(int version) {
            jniOptionsSetVersion(getRawPointer(), version);
        }
        public int getFlags() {
            return jniOptionsGetFlags(getRawPointer());
        }
        public void setFlags(int flags) {
            jniOptionsSetFlags(getRawPointer(), flags);
        }
        public int getRenameThreshold() {
            return jniOptionsGetRenameThreshold(getRawPointer());
        }
        public void setRenameThreshold(int renameThreshold) {
            jniOptionsSetRenameThreshold(getRawPointer(), renameThreshold);
        }
        public int getTargetLimit() {
            return jniOptionsGetTargetLimit(getRawPointer());
        }
        public void setTargetLimit(int targetLimit) {
            jniOptionsSetTargetLimit(getRawPointer(), targetLimit);
        }
        public long getMetric() {
            return jniOptionsGetMetric(getRawPointer());
        }
        public void setMetric(long metric) {
            jniOptionsSetMetric(getRawPointer(), metric);
        }
        public int getRecursionLimit() {
            return jniOptionsGetRecursionLimit(getRawPointer());
        }
        public void setRecursionLimit(int recursionLimit) {
            jniOptionsSetRecursionLimit(getRawPointer(), recursionLimit);
        }
        public String getDefaultDriver() {
            return jniOptionsGetDefaultDriver(getRawPointer());
        }
        public void setDefaultDriver(String defaultDriver) {
            jniOptionsSetDefaultDriver(getRawPointer(), defaultDriver);
        }
        public int getFileFavor() {
            return jniOptionsGetFileFavor(getRawPointer());
        }
        public void setFileFavor(int fileFavor) {
            jniOptionsSetFileFavor(getRawPointer(), fileFavor);
        }
        public int getFileFlags() {
            return jniOptionsGetFileFlags(getRawPointer());
        }
        public void setFileFlags(int fileFlags) {
            jniOptionsSetFileFlags(getRawPointer(), fileFlags);
        }
    }
    public static class AnalysisPair {
        private final EnumSet<AnalysisT> analysisSet;
        private final EnumSet<PreferenceT> preferenceSet;
        private final int analysisValue;  
        private final int preferenceValue;  
        public AnalysisPair(EnumSet<AnalysisT> analysisSet, EnumSet<PreferenceT> preferenceSet, int analysisValue ,int preferenceValue) {
            this.analysisSet = analysisSet;
            this.preferenceSet = preferenceSet;
            this.analysisValue = analysisValue;
            this.preferenceValue = preferenceValue;
        }
        public EnumSet<AnalysisT> getAnalysisSet() {
            return analysisSet;
        }
        public EnumSet<PreferenceT> getPreferenceSet() {
            return preferenceSet;
        }
        public int getAnalysisValue() {
            return analysisValue;
        }
        public int getPreferenceValue() {
            return preferenceValue;
        }
    }
}
