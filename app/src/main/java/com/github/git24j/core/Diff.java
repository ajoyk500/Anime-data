package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;
import static com.github.git24j.core.Internals.*;

public class Diff extends CAutoReleasable {
    static native void jniDiffOptionsSetPathSpec(long diffOptionsPtr, String[] pathSpecJArr);
    static native String[] jniDiffOptionsGetPathSpec(long diffOptionsPtr);
    static native void jniDiffOptionsSetFlags(long diffOptionsPtr, int flags);
    static native int jniDiffOptionsGetFlags(long diffOptionsPtr);
    static native String jniBinaryFileGetData(long binary_filePtr);
    static native int jniBinaryFileGetDatalen(long binary_filePtr);
    static native int jniBinaryFileGetInflatedlen(long binary_filePtr);
    static native int jniBinaryFileGetType(long binary_filePtr);
    static native int jniBinaryGetContainsData(long binaryPtr);
    static native long jniBinaryGetNewFile(long binaryPtr);
    static native long jniBinaryGetOldFile(long binaryPtr);
    static native int jniBlobToBuffer(
            long oldBlob,
            String oldAsPath,
            String buffer,
            int bufferLen,
            String bufferAsPath,
            long options,
            JFCallback fileCb,
            JJCallback binaryCb,
            JJCallback hunkCb,
            JJJCallback lineCb);
    static native int jniBlobs(
            long oldBlob,
            String oldAsPath,
            long newBlob,
            String newAsPath,
            long options,
            JFCallback fileCb,
            JJCallback binaryCb,
            JJCallback hunkCb,
            JJJCallback lineCb);
    static native int jniBuffers(
            byte[] oldBuffer,
            int oldLen,
            String oldAsPath,
            byte[] newBuffer,
            int newLen,
            String newAsPath,
            long options,
            JFCallback fileCb,
            JJCallback binaryCb,
            JJCallback hunkCb,
            JJJCallback lineCb);
    static native int jniCommitAsEmail(
            Buf out,
            long repoPtr,
            long commit,
            int patchNo,
            int totalPatches,
            int flags,
            long diffOpts);
    static native int jniDeltaGetFlags(long deltaPtr);
    static native long jniDeltaGetNewFile(long deltaPtr);
    static native int jniDeltaGetNfiles(long deltaPtr);
    static native long jniDeltaGetOldFile(long deltaPtr);
    static native int jniDeltaGetSimilarity(long deltaPtr);
    static native int jniDeltaGetStatus(long deltaPtr);
    static native int jniFileGetFlags(long filePtr);
    static native byte[] jniFileGetId(long filePtr);
    static native int jniFileGetIdAbbrev(long filePtr);
    static native int jniFileGetMode(long filePtr);
    static native String jniFileGetPath(long filePtr);
    static native int jniFileGetSize(long filePtr);
    static native int jniFindInitOptions(AtomicLong outOpts, int version);
    static native int jniFindSimilar(long diff, long options);
    static native int jniForeach(
            long diff,
            Internals.JFCallback fileCb,
            JJCallback binaryCb,
            JJCallback hunkCb,
            JJJCallback lineCb);
    static native int jniFormatEmail(Buf out, long diff, long opts);
    static native int jniFormatEmailInitOptions(long opts, int version);
    static native int jniFormatEmailNewOptions(AtomicLong out, int version);
    static native void jniFormatEmailOptionsFree(long opts);
    static native void jniFree(long diff);
    static native void jniFreeFindOptions(long findOptsPtr);
    static native void jniFreeOptions(long opts);
    static native int jniFromBuffer(AtomicLong out, String content, int contentLen);
    static native long jniGetDelta(long diff, int idx);
    static native int jniGetStats(AtomicLong out, long diff);
    static native String jniHunkGetHeader(long hunkPtr);
    @Nullable
    static native byte[] jniHunkGetHeaderBytes(long hunkPtr);
    static native int jniHunkGetHeaderLen(long hunkPtr);
    static native int jniHunkGetNewLines(long hunkPtr);
    static native int jniHunkGetNewStart(long hunkPtr);
    static native int jniHunkGetOldLines(long hunkPtr);
    static native int jniHunkGetOldStart(long hunkPtr);
    static native int jniIndexToIndex(
            AtomicLong diff, long repoPtr, long oldIndex, long newIndex, long opts);
    static native int jniIndexToWorkdir(AtomicLong diff, long repoPtr, long index, long opts);
    static native int jniInitOptions(AtomicLong outOpts, int version);
    static native int jniIsSortedIcase(long diff);
    static native String jniLineGetContent(long linePtr);
    static native int jniLineGetContentLen(long linePtr);
    static native int jniLineGetContentOffset(long linePtr);
    @Nullable
    static native byte[] jniLineGetContentBytes(long linePtr);
    static native int jniLineGetNewLineno(long linePtr);
    static native int jniLineGetNumLines(long linePtr);
    static native int jniLineGetOldLineno(long linePtr);
    static native char jniLineGetOrigin(long linePtr);
    static native int jniMerge(long onto, long from);
    static native int jniNumDeltas(long diff);
    static native int jniNumDeltasOfType(long diff, int type);
    static native int jniPatchid(Oid out, long diff, long opts);
    static native int jniPatchidInitOptions(long opts, int version);
    static native void jniPatchidOptionsFree(long opts);
    static native int jniPatchidOptionsNew(AtomicLong outOpts, int version);
    static native int jniPrint(long diff, int format, JJJCallback printCb);
    static native int jniStatsDeletions(long stats);
    static native int jniStatsFilesChanged(long stats);
    static native void jniStatsFree(long stats);
    static native int jniStatsInsertions(long stats);
    static native int jniStatsToBuf(Buf out, long stats, int format, int width);
    static native char jniStatusChar(int status);
    static native int jniToBuf(Buf out, long diff, int format);
    static native int jniTreeToIndex(
            AtomicLong diff, long repoPtr, long oldTree, long index, long opts);
    static native int jniTreeToTree(
            AtomicLong diff, long repoPtr, long oldTree, long newTree, long opts);
    static native int jniTreeToWorkdir(AtomicLong diff, long repoPtr, long oldTree, long opts);
    static native int jniTreeToWorkdirWithIndex(
            AtomicLong diff, long repoPtr, long oldTree, long opts);
    protected Diff(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    public static Diff treeToTree(
            @Nonnull Repository repo,
            @Nullable Tree oldTree,
            @Nullable Tree newTree,
            @Nullable Options opts) {
        Diff diff = new Diff(false, 0);
        int e =
                jniTreeToTree(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldTree == null ? 0 : oldTree.getRawPointer(),
                        newTree == null ? 0 : newTree.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer());
        Error.throwIfNeeded(e);
        return diff;
    }
    public static Diff treeToIndex(Repository repo, Tree oldTree, Index index, Options opts) {
        Diff diff = new Diff(false, 0);
        int e =
                jniTreeToIndex(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldTree.getRawPointer(),
                        index.getRawPointer(),
                        opts.getRawPointer());
        Error.throwIfNeeded(e);
        return diff;
    }
    public static Diff indexToWorkdir(Repository repo, Index index, Options opts) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(
                jniIndexToWorkdir(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        index==null ? repo.index().getRawPointer() : index.getRawPointer(),
                        opts==null ? Options.create().getRawPointer() : opts.getRawPointer()));
        return diff;
    }
    public static Diff treeToWorkdir(Repository repo, Tree oldTree, Options opts) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(
                jniTreeToWorkdir(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldTree.getRawPointer(),
                        opts.getRawPointer()));
        return diff;
    }
    public static Diff treeToWorkdirWithIndex(Repository repo, Tree oldTree, Options opts) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(
                jniTreeToWorkdirWithIndex(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldTree.getRawPointer(),
                        opts.getRawPointer()));
        return diff;
    }
    @Nonnull
    public static Diff indexToIndex(
            @Nonnull Repository repo,
            @Nonnull Index oldIndex,
            @Nonnull Index newIndex,
            @Nonnull Options opts) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(
                jniIndexToIndex(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldIndex.getRawPointer(),
                        newIndex.getRawPointer(),
                        opts.getRawPointer()));
        return diff;
    }
    public static void merge(@Nonnull Diff onto, @Nonnull Diff from) {
        Error.throwIfNeeded(jniMerge(onto.getRawPointer(), from.getRawPointer()));
    }
    public static char statusChar(@Nonnull DeltaT status) {
        return jniStatusChar(status.getBit());
    }
    public static int blobs(
            @Nullable Blob oldBlob,
            @Nullable String oldAsPath,
            @Nullable Blob newBlob,
            @Nullable String newAsPath,
            @Nullable Options options,
            @Nullable FileCb fileCb,
            @Nullable BinaryCb binaryCb,
            @Nullable HunkCb hunkCb,
            @Nullable LineCb lineCb) {
        long jniOldBlob = oldBlob == null ? 0 : oldBlob.getRawPointer();
        String jniOldAsPath = oldAsPath == null ? null : oldAsPath;
        long jniNewBlob = newBlob == null ? 0 : newBlob.getRawPointer();
        String jniNewAsPath = newAsPath == null ? null : newAsPath;
        long jniOptions =
                options == null
                        ? Options.create(Options.CURRENT_VERSION).getRawPointer()
                        : options.getRawPointer();
        JFCallback jniFileCb =
                fileCb == null ? null : (pd, progress) -> fileCb.accept(new Delta(pd), progress);
        JJCallback jniBinaryCb =
                binaryCb == null
                        ? null
                        : (pd, pb) -> binaryCb.accept(new Delta(pd), new Binary(pb));
        JJCallback jniHunkCb =
                hunkCb == null ? null : (pd, ph) -> hunkCb.accept(new Delta(pd), new Hunk(ph));
        JJJCallback jniLineCb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e =
                jniBlobs(
                        jniOldBlob,
                        jniOldAsPath,
                        jniNewBlob,
                        jniNewAsPath,
                        jniOptions,
                        jniFileCb,
                        jniBinaryCb,
                        jniHunkCb,
                        jniLineCb);
        Error.throwIfNeeded(e);
        return e;
    }
    public static int blobToBuff(
            @Nullable Blob oldBlob,
            @Nullable String oldAsPath,
            @Nullable String buffer,
            @Nullable String bufferAsPath,
            @Nullable Options options,
            @Nullable FileCb fileCb,
            @Nullable BinaryCb binaryCb,
            @Nullable HunkCb hunkCb,
            @Nullable LineCb lineCb) {
        long oldBlobPtr = oldBlob == null ? 0 : oldBlob.getRawPointer();
        String oldAsPathStr = oldAsPath == null ? null : oldAsPath;
        long optsPtr = options == null ? 0 : options.getRawPointer();
        JFCallback jniFileCb =
                fileCb == null ? null : (pd, progress) -> fileCb.accept(new Delta(pd), progress);
        JJCallback jniBinaryCb =
                binaryCb == null
                        ? null
                        : (pd, pb) -> binaryCb.accept(new Delta(pd), new Binary(pb));
        JJCallback jniHunkCb =
                hunkCb == null ? null : (pd, ph) -> hunkCb.accept(new Delta(pd), new Hunk(ph));
        JJJCallback jniLineCb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e =
                jniBlobToBuffer(
                        oldBlobPtr,
                        oldAsPathStr,
                        buffer,
                        buffer == null ? 0 : buffer.length(),
                        bufferAsPath,
                        optsPtr,
                        jniFileCb,
                        jniBinaryCb,
                        jniHunkCb,
                        jniLineCb);
        Error.throwIfNeeded(e);
        return e;
    }
    public static int buffers(
            @Nullable byte[] oldBuffer,
            @Nullable String oldAsPath,
            @Nullable byte[] newBuffer,
            @Nullable String newAsPath,
            @Nullable Options options,
            FileCb fileCb,
            BinaryCb binaryCb,
            HunkCb hunkCb,
            LineCb lineCb) {
        JFCallback jniFileCb =
                fileCb == null ? null : (pd, progress) -> fileCb.accept(new Delta(pd), progress);
        JJCallback jniBinaryCb =
                binaryCb == null
                        ? null
                        : (pd, pb) -> binaryCb.accept(new Delta(pd), new Binary(pb));
        JJCallback jniHunkCb =
                hunkCb == null ? null : (pd, ph) -> hunkCb.accept(new Delta(pd), new Hunk(ph));
        JJJCallback jniLineCb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e =
                jniBuffers(
                        oldBuffer,
                        oldBuffer == null ? 0 : oldBuffer.length,
                        oldAsPath,
                        newBuffer,
                        newBuffer == null ? 0 : newBuffer.length,
                        newAsPath,
                        options == null ? 0 : options.getRawPointer(),
                        jniFileCb,
                        jniBinaryCb,
                        jniHunkCb,
                        jniLineCb);
        Error.throwIfNeeded(e);
        return e;
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public void findSimilar(@Nonnull FindOptions options) {
        Error.throwIfNeeded(jniFindSimilar(getRawPointer(), options.getRawPointer()));
    }
    public int numDeltas() {
        return jniNumDeltas(getRawPointer());
    }
    public int numDeltasOfType(@Nonnull DeltaT type) {
        return jniNumDeltasOfType(getRawPointer(), type.getBit());
    }
    @CheckForNull
    public Delta getDelta(int idx) {
        long ptr = jniGetDelta(getRawPointer(), idx);
        if (ptr == 0) {
            return null;
        }
        return new Delta(ptr);
    }
    public boolean isSortedIcase() {
        return jniIsSortedIcase(getRawPointer()) == 1;
    }
    public int foreach(
            @Nullable FileCb fileCb,
            @Nullable BinaryCb binaryCb,
            @Nullable HunkCb hunkCb,
            @Nullable LineCb lineCb) {
        JFCallback jniFileCb =
                fileCb == null ? null : (pd, progress) -> fileCb.accept(new Delta(pd), progress);
        JJCallback jniBinaryCb =
                binaryCb == null
                        ? null
                        : (pd, pb) -> binaryCb.accept(new Delta(pd), new Binary(pb));
        JJCallback jniHunkCb =
                hunkCb == null ? null : (pd, ph) -> hunkCb.accept(new Delta(pd), new Hunk(ph));
        JJJCallback jniLineCb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e = jniForeach(getRawPointer(), jniFileCb, jniBinaryCb, jniHunkCb, jniLineCb);
        Error.throwIfNeeded(e);
        return e;
    }
    public int print(@Nonnull FormatT format, @Nullable LineCb lineCb) {
        JJJCallback cb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e = jniPrint(getRawPointer(), format.getCode(), cb);
        Error.throwIfNeeded(e);
        return e;
    }
    @Nonnull
    public Buf toBuf(@Nonnull FormatT format) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniToBuf(out, getRawPointer(), format.getCode()));
        return out;
    }
    @Nonnull
    public static Diff fromBuffer(@Nonnull String content) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(jniFromBuffer(diff._rawPtr, content, content.getBytes(StandardCharsets.UTF_8).length));
        return diff;
    }
    @Nonnull
    public Stats getStats() {
        Stats out = new Stats(false, 0);
        Error.throwIfNeeded(jniGetStats(out._rawPtr, getRawPointer()));
        return out;
    }
    @Nonnull
    public Buf formatEmail(@Nonnull FormatEmailOptions opts) {
        Buf out = new Buf();
        jniFormatEmail(out, getRawPointer(), opts.getRawPointer());
        return out;
    }
    @Nonnull
    public Buf commitAsEmail(
            @Nonnull Repository repo,
            @Nonnull Commit commit,
            int patchNo,
            int totalPatches,
            @Nonnull FormatEmailFlagT flags,
            @Nullable Options diffOpts) {
        Buf outBuf = new Buf();
        Error.throwIfNeeded(
                jniCommitAsEmail(
                        outBuf,
                        repo.getRawPointer(),
                        commit.getRawPointer(),
                        patchNo,
                        totalPatches,
                        flags.getCode(),
                        diffOpts == null ? 0 : diffOpts.getRawPointer()));
        return outBuf;
    }
    @Nonnull
    public Oid patchid(@Nullable PatchidOptions opts) {
        Oid oid = new Oid();
        Error.throwIfNeeded(
                jniPatchid(oid, getRawPointer(), opts == null ? 0 : opts.getRawPointer()));
        return oid;
    }
    @Deprecated
    public enum OptionFlag implements IBitEnum {
        NORMAL0(0),
        REVERSE(1 << 0),
        INCLUDE_IGNORED(1 << 1),
        RECURSE_IGNORED_DIRS(1 << 2),
        INCLUDE_UNTRACKED(1 << 3),
        RECURSE_UNTRACKED_DIRS(1 << 4),
        INCLUDE_UNMODIFIED(1 << 5),
        INCLUDE_TYPECHANGE(1 << 6),
        INCLUDE_TYPECHANGE_TREES(1 << 7),
        IGNORE_FILEMODE(1 << 8),
        IGNORE_SUBMODULES(1 << 9),
        IGNORE_CASE(1 << 10),
        INCLUDE_CASECHANGE(1 << 11),
        DISABLE_PATHSPEC_MATCH(1 << 12),
        SKIP_BINARY_CHECK(1 << 13),
        ENABLE_FAST_UNTRACKED_DIRS(1 << 14),
        UPDATE_INDEX(1 << 15),
        INCLUDE_UNREADABLE(1 << 16),
        INCLUDE_UNREADABLE_AS_UNTRACKED(1 << 17),
        INDENT_HEURISTIC(1 << 18),
        FORCE_TEXT(1 << 20),
        FORCE_BINARY(1 << 21),
        IGNORE_WHITESPACE(1 << 22),
        IGNORE_WHITESPACE_CHANGE(1 << 23),
        IGNORE_WHITESPACE_EOL(1 << 24),
        SHOW_UNTRACKED_CONTENT(1 << 25),
        SHOW_UNMODIFIED(1 << 26),
        PATIENCE(1 << 28),
        MINIMAL(1 << 29),
        SHOW_BINARY(1 << 30),
        ;
        final int _bit;
        OptionFlag(int bit) {
            this._bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum FlagT implements IBitEnum {
        BINARY(1 << 0),
        NOT_BINARY(1 << 1),
        VALID_ID(1 << 2),
        EXISTS(1 << 3),
        ;
        private final int _bit;
        FlagT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum DeltaT implements IBitEnum {
        UNMODIFIED(0),
        ADDED(1),
        DELETED(2),
        MODIFIED(3),
        RENAMED(4),
        COPIED(5),
        IGNORED(6),
        UNTRACKED(7),
        TYPECHANGE(8),
        UNREADABLE(9),
        CONFLICTED(10),
        ;
        private final int _bit;
        DeltaT(int _code) {
            this._bit = _code;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum FormatT {
        PATCH(1),
        PATCH_HEADER(2),
        RAW(3),
        NAME_ONLY(4),
        NAME_STATUS(5),
        PATCH_ID(6),
        ;
        private final int _code;
        FormatT(int _code) {
            this._code = _code;
        }
        public int getCode() {
            return _code;
        }
    }
    public enum StatsFormatT {
        NONE(0),
        FULL(1 << 0),
        SHORT(1 << 1),
        NUMBER(1 << 2),
        INCLUDE_SUMMARY(1 << 3),
        ;
        private final int _code;
        StatsFormatT(int _code) {
            this._code = _code;
        }
        public int getCode() {
            return _code;
        }
    }
    public enum FormatEmailFlagT {
        NONE(0),
        EXCLUDE_SUBJECT_PATCH_MARKER(1 << 0);
        private final int _code;
        FormatEmailFlagT(int _code) {
            this._code = _code;
        }
        public int getCode() {
            return _code;
        }
    }
    @FunctionalInterface
    public interface FileCb {
        int accept(Delta delta, float progress);
    }
    @FunctionalInterface
    public interface BinaryCb {
        int accept(Delta delta, Binary binary);
    }
    @FunctionalInterface
    public interface HunkCb {
        int accept(Delta delta, Hunk hunk);
    }
    @FunctionalInterface
    public interface LineCb {
        int accept(Delta delta, Hunk hunk, Line line);
    }
    public static class Options extends CAutoReleasable {
        public static final int CURRENT_VERSION = 1;
        Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        public static Options create(int version) {
            Options opts = new Options(false, 0);
            Error.throwIfNeeded(jniInitOptions(opts._rawPtr, version));
            return opts;
        }
        public static Options create() {
            return create(CURRENT_VERSION);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniFreeOptions(cPtr);
        }
        public String[] getPathSpec() {
            return jniDiffOptionsGetPathSpec(_rawPtr.get());
        }
        public void setPathSpec(String[] pathSpec) {
            jniDiffOptionsSetPathSpec(_rawPtr.get(), pathSpec);
        }
        public EnumSet<Diff.Options.FlagT> getFlags() {
            int flagValue = jniDiffOptionsGetFlags(_rawPtr.get());
            return IBitEnum.parse(flagValue, Diff.Options.FlagT.class);
        }
        public void setFlags(EnumSet<Diff.Options.FlagT> flags) {
            jniDiffOptionsSetFlags(_rawPtr.get(), IBitEnum.bitOrAll(flags));
        }
        public enum FlagT implements IBitEnum {
            NORMAL(0),
            REVERSE(1<<0),
            INCLUDE_IGNORED(1<<1),
            RECURSE_IGNORED_DIRS(1<<2),
            INCLUDE_UNTRACKED(1<<3),
            RECURSE_UNTRACKED_DIRS(1<<4),
            INCLUDE_UNMODIFIED(1<<5),
            INCLUDE_TYPECHANGE(1<<6),
            INCLUDE_TYPECHANGE_TREES(1<<7),
            IGNORE_FILEMODE(1<<8),
            IGNORE_SUBMODULES(1<<9),
            IGNORE_CASE(1<<10),
            INCLUDE_CASECHANGE(1<<11),
            DISABLE_PATHSPEC_MATCH(1<<12),
            SKIP_BINARY_CHECK(1<<13),
            ENABLE_FAST_UNTRACKED_DIRS(1<<14),
            UPDATE_INDEX(1<<15),
            INCLUDE_UNREADABLE(1<<16),
            INCLUDE_UNREADABLE_AS_UNTRACKED(1<<17),
            INDENT_HEURISTIC(1<<18),
            IGNORE_BLANK_LINES(1<<19),
            FORCE_TEXT(1<<20),
            FORCE_BINARY(1<<21),
            IGNORE_WHITESPACE(1<<22),
            IGNORE_WHITESPACE_CHANGE(1<<23),
            IGNORE_WHITESPACE_EOL(1<<24),
            SHOW_UNTRACKED_CONTENT(1<<25),
            SHOW_UNMODIFIED(1<<26),
            PATIENCE(1<<28),
            MINIMAL(1<<29),
            SHOW_BINARY(1<<30),
            ;
            private final int _bit;
            FlagT(int bit) {
                _bit = bit;
            }
            @Override
            public int getBit() {
                return _bit;
            }
        }
    }
    public static class FindOptions extends CAutoReleasable {
        FindOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        public static FindOptions create(int version) {
            FindOptions opts = new FindOptions(false, 0);
            jniFindInitOptions(opts._rawPtr, version);
            return opts;
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniFreeFindOptions(cPtr);
        }
    }
    public static class Delta extends CAutoReleasable {
        protected Delta(long rawPtr) {
            super(true, rawPtr);
        }
        @CheckForNull
        static Delta of(long rawPtr) {
            if (rawPtr == 0) {
                return null;
            }
            return new Delta(rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            throw new IllegalStateException(
                    "Diff.Delta is owned by Diff and should not be released manually");
        }
        @Nonnull
        public DeltaT getStatus() {
            return IBitEnum.valueOf(
                    jniDeltaGetStatus(getRawPointer()), DeltaT.class, DeltaT.UNMODIFIED);
        }
        public EnumSet<FlagT> getFlags() {
            return IBitEnum.parse(jniDeltaGetFlags(getRawPointer()), FlagT.class);
        }
        public int getSimilarity() {
            return jniDeltaGetSimilarity(getRawPointer());
        }
        public int getNfiles() {
            return jniDeltaGetNfiles(getRawPointer());
        }
        public File getOldFile() {
            return File.ofWeak(jniDeltaGetOldFile(getRawPointer()));
        }
        public File getNewFile() {
            return File.ofWeak(jniDeltaGetNewFile(getRawPointer()));
        }
    }
    public static class BinaryFile extends CAutoReleasable {
        protected BinaryFile(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }
        public int getType() {
            return jniBinaryFileGetType(getRawPointer());
        }
        public String getData() {
            return jniBinaryFileGetData(getRawPointer());
        }
        public int getDatalen() {
            return jniBinaryFileGetDatalen(getRawPointer());
        }
        public int getInflatedlen() {
            return jniBinaryFileGetInflatedlen(getRawPointer());
        }
    }
    public static class Binary extends CAutoReleasable {
        protected Binary(long rawPtr) {
            super(true, rawPtr);
        }
        @CheckForNull
        static Binary of(long rawPtr) {
            if (rawPtr == 0) {
                return null;
            }
            return new Binary(rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            throw new IllegalStateException(
                    "Diff.Binary is owned by Diff and should not be released manually");
        }
        public int getContainsData() {
            return jniBinaryGetContainsData(getRawPointer());
        }
        public BinaryFile getOldFile() {
            return new BinaryFile(true, jniBinaryGetOldFile(getRawPointer()));
        }
        public BinaryFile getNewFile() {
            return new BinaryFile(true, jniBinaryGetNewFile(getRawPointer()));
        }
    }
    public static class Hunk extends CAutoReleasable {
        protected Hunk(long rawPtr) {
            super(true, rawPtr);
        }
        @CheckForNull
        static Hunk of(long rawPtr) {
            if (rawPtr == 0) {
                return null;
            }
            return new Hunk(rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            throw new IllegalStateException(
                    "Diff.Hunk is owned by Diff and should not be released manually");
        }
        public int getOldStart() {
            return jniHunkGetOldStart(getRawPointer());
        }
        public int getOldLines() {
            return jniHunkGetOldLines(getRawPointer());
        }
        public int getNewStart() {
            return jniHunkGetNewStart(getRawPointer());
        }
        public int getNewLines() {
            return jniHunkGetNewLines(getRawPointer());
        }
        public int getHeaderLen() {
            return jniHunkGetHeaderLen(getRawPointer());
        }
        @Nullable
        public byte[] getHeaderBytes() {
            return jniHunkGetHeaderBytes(getRawPointer());
        }
        public String getHeader() {
            byte[] headerBytes = getHeaderBytes();
            return headerBytes!=null ? new String(headerBytes, StandardCharsets.UTF_8) : "";
        }
        public String getHeaderRaw() {
            return jniHunkGetHeader(getRawPointer());
        }
        @Deprecated
        public String getHeader_Depercated() {
            String rawHeader = jniHunkGetHeader(getRawPointer());
            int headerLen = getHeaderLen();
            byte[] src = rawHeader.getBytes(StandardCharsets.UTF_8);
            if(src.length > headerLen) {
                return new String(src, 0, headerLen, StandardCharsets.UTF_8);
            }
            return rawHeader;
        }
    }
    public static class File extends CAutoReleasable {
        protected File(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @CheckForNull
        static File ofWeak(long ptr) {
            return ptr == 0 ? null : new File(true, ptr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }
        public Oid getId() {
            return Oid.of(jniFileGetId(getRawPointer()));
        }
        public String getPath() {
            return jniFileGetPath(getRawPointer());
        }
        public int getSize() {
            return jniFileGetSize(getRawPointer());
        }
        @Nonnull
        public EnumSet<FlagT> getFlags() {
            return IBitEnum.parse(jniFileGetFlags(getRawPointer()), FlagT.class);
        }
        @Nonnull
        public FileMode getMode() {
            return IBitEnum.valueOf(
                    jniFileGetMode(getRawPointer()), FileMode.class, FileMode.UNREADABLE);
        }
        public int getIdAbbrev() {
            return jniFileGetIdAbbrev(getRawPointer());
        }
    }
    public static class Line extends CAutoReleasable {
        public static class OriginType {
            public static final char CONTEXT   = ' ';
            public static final char ADDITION  = '+';
            public static final char DELETION  = '-';
            public static final char CONTEXT_EOFNL = '='; 
            public static final char ADD_EOFNL = '>';     
            public static final char DEL_EOFNL = '<';     
            public static final char FILE_HDR  = 'F';
            public static final char HUNK_HDR  = 'H';
            public static final char BINARY    = 'B'; 
        }
        protected Line(long rawPtr) {
            super(true, rawPtr);
        }
        @CheckForNull
        static Line of(long rawPtr) {
            if (rawPtr == 0) {
                return null;
            }
            return new Line(rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            throw new IllegalStateException(
                    "Diff.Line is owned by Diff and should not be released manually");
        }
        public char getOrigin() {
            return jniLineGetOrigin(getRawPointer());
        }
        public int getOldLineno() {
            return jniLineGetOldLineno(getRawPointer());
        }
        public int getNewLineno() {
            return jniLineGetNewLineno(getRawPointer());
        }
        public int getNumLines() {
            return jniLineGetNumLines(getRawPointer());
        }
        public int getContentLen() {
            return jniLineGetContentLen(getRawPointer());
        }
        public int getContentOffset() {
            return jniLineGetContentOffset(getRawPointer());
        }
        @Nullable
        public byte[] getContentBytes(){
            return jniLineGetContentBytes(getRawPointer());
        }
        public String getContent(){
            byte[] bytes = getContentBytes();
            return (bytes!=null) ? new String(bytes, StandardCharsets.UTF_8) : "";
        }
        @Deprecated
        public String getContent_Deprecated() {
            String content = jniLineGetContent(getRawPointer());
            int contentLen = jniLineGetContentLen(getRawPointer());
            byte[] src = content.getBytes(StandardCharsets.UTF_8);
            if(src.length > contentLen) {  
                return new String(src, 0, contentLen, StandardCharsets.UTF_8);
            }
            return content;
        }
    }
    public static class Stats extends CAutoReleasable {
        protected Stats(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniStatsFree(cPtr);
        }
        public int filesChanged() {
            return jniStatsFilesChanged(getRawPointer());
        }
        public int insertions() {
            return jniStatsInsertions(getRawPointer());
        }
        public int deletions() {
            return jniStatsDeletions(getRawPointer());
        }
        public Buf toBuf(StatsFormatT format, int width) {
            Buf out = new Buf();
            Error.throwIfNeeded(jniStatsToBuf(out, getRawPointer(), format.getCode(), width));
            return out;
        }
    }
    public static class FormatEmailOptions extends CAutoReleasable {
        public static final int CURRENT_VERSION = 1;
        protected FormatEmailOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static FormatEmailOptions create(int version) {
            FormatEmailOptions opts = new FormatEmailOptions(false, 0);
            Error.throwIfNeeded(jniFormatEmailNewOptions(opts._rawPtr, version));
            return opts;
        }
        public static FormatEmailOptions defaultOptions() {
            return Holder.__DEFAULT;
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniFormatEmailOptionsFree(cPtr);
        }
        private static class Holder {
            static final FormatEmailOptions __DEFAULT = FormatEmailOptions.create(CURRENT_VERSION);
        }
    }
    public static class PatchidOptions extends CAutoReleasable {
        protected PatchidOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniPatchidOptionsFree(cPtr);
        }
        public PatchidOptions create(int version) {
            PatchidOptions out = new PatchidOptions(false, 0);
            Error.throwIfNeeded(jniPatchidOptionsNew(out._rawPtr, version));
            return out;
        }
    }
}
