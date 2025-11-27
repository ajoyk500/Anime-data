package com.github.git24j.core;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Worktree {
    static native int jniAdd(AtomicLong out, long repoPtr, String name, String path, long opts);
    static native int jniAddInitOptions(long opts, int version);
    static native void jniFree(long wt);
    static native int jniIsLocked(Buf reason, long wt);
    static native int jniIsPrunable(long wt, long opts);
    static native int jniList(List<String> out, long repoPtr);
    static native int jniLock(long wt, String reason);
    static native int jniLookup(AtomicLong out, long repoPtr, String name);
    static native String jniName(long wt);
    static native int jniOpenFromRepository(AtomicLong out, long repoPtr);
    static native String jniPath(long wt);
    static native int jniPrune(long wt, long opts);
    static native int jniPruneInitOptions(long opts, int version);
    static native int jniUnlock(long wt);
    static native int jniValidate(long wt);
}
