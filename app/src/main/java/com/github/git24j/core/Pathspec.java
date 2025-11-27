package com.github.git24j.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;

public class Pathspec extends CAutoReleasable {
    static native void jniFree(long ps);
    static native int jniMatchDiff(AtomicLong out, long diff, int flags, long ps);
    static native int jniMatchIndex(AtomicLong out, long index, int flags, long ps);
    static native long jniMatchListDiffEntry(long m, int pos);
    static native String jniMatchListEntry(long m, int pos);
    static native int jniMatchListEntrycount(long m);
    static native String jniMatchListFailedEntry(long m, int pos);
    static native int jniMatchListFailedEntrycount(long m);
    static native void jniMatchListFree(long m);
    static native int jniMatchTree(AtomicLong out, long tree, int flags, long ps);
    static native int jniMatchWorkdir(AtomicLong out, long repoPtr, int flags, long ps);
    static native int jniMatchesPath(long ps, int flags, String path);
    static native int jniNew(AtomicLong out, String[] pathspec);
    protected Pathspec(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Nonnull
    public static Pathspec create(List<String> pathspec) {
        Pathspec out = new Pathspec(false, 0);
        Error.throwIfNeeded(jniNew(out._rawPtr, pathspec.toArray(new String[0])));
        return out;
    }
    private static List<String> getEntries(long matchListPtr) {
        int n = jniMatchListEntrycount(matchListPtr);
        if (n <= 0) {
            return Collections.emptyList();
        }
        List<String> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            res.add(jniMatchListEntry(matchListPtr, i));
        }
        return res;
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public boolean matchesPath(EnumSet<FlagT> flags, @Nonnull String path) {
        return jniMatchesPath(getRawPointer(), IBitEnum.bitOrAll(flags), path) == 1;
    }
    public List<String> matchWorkdir(@Nonnull Repository repo, EnumSet<FlagT> flags) {
        AtomicLong outMatchList = new AtomicLong();
        int e =
                jniMatchWorkdir(
                        outMatchList,
                        repo.getRawPointer(),
                        IBitEnum.bitOrAll(flags),
                        getRawPointer());
        Error.throwIfNeeded(e);
        List<String> res = getEntries(outMatchList.get());
        jniMatchListFree(outMatchList.get());
        return res;
    }
    public List<String> matchIndex(@Nonnull Index index, EnumSet<FlagT> flags) {
        AtomicLong outMatchList = new AtomicLong();
        int e =
                jniMatchIndex(
                        outMatchList,
                        index.getRawPointer(),
                        IBitEnum.bitOrAll(flags),
                        getRawPointer());
        Error.throwIfNeeded(e);
        long matchListPtr = outMatchList.get();
        List<String> res = getEntries(matchListPtr);
        jniMatchListFree(matchListPtr);
        return res;
    }
    public List<String> matchTree(@Nonnull Tree tree, EnumSet<FlagT> flags) {
        AtomicLong outMatchList = new AtomicLong();
        int e =
                jniMatchTree(
                        outMatchList,
                        tree.getRawPointer(),
                        IBitEnum.bitOrAll(flags),
                        getRawPointer());
        Error.throwIfNeeded(e);
        long matchListPtr = outMatchList.get();
        List<String> res = getEntries(matchListPtr);
        jniMatchListFree(matchListPtr);
        return res;
    }
    public List<Diff.Delta> matchDiff(@Nonnull Diff diff, EnumSet<FlagT> flags) {
        AtomicLong outMatchList = new AtomicLong();
        int e =
                jniMatchDiff(
                        outMatchList,
                        diff.getRawPointer(),
                        IBitEnum.bitOrAll(flags),
                        getRawPointer());
        Error.throwIfNeeded(e);
        long matchListPtr = outMatchList.get();
        List<Diff.Delta> deltas = new ArrayList<>();
        int n = jniMatchListEntrycount(matchListPtr);
        for (int i = 0; i < n; i++) {
            deltas.add(new Diff.Delta(jniMatchListDiffEntry(matchListPtr, i)));
        }
        return deltas;
    }
    public enum FlagT implements IBitEnum {
        DEFAULT(0),
        IGNORE_CASE(1 << 0),
        USE_CASE(1 << 1),
        NO_GLOB(1 << 2),
        NO_MATCH_ERROR(1 << 3),
        FIND_FAILURES(1 << 4),
        FAILURES_ONLY(1 << 5);
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
