package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;

public class Graph {
    static native int jniAheadBehind(
            AtomicInteger ahead, AtomicInteger behind, long repoPtr, Oid local, Oid upstream);
    static native int jniDescendantOf(long repoPtr, Oid commit, Oid ancestor);
    public static Count aheadBehind(
            @Nonnull Repository repo, @Nonnull Oid local, @Nonnull Oid upstream) {
        AtomicInteger ahead = new AtomicInteger();
        AtomicInteger behind = new AtomicInteger();
        Error.throwIfNeeded(jniAheadBehind(ahead, behind, repo.getRawPointer(), local, upstream));
        return new Count(ahead.get(), behind.get());
    }
    public static boolean descendantOf(
            @Nonnull Repository repo, @Nonnull Oid commit, @Nonnull Oid ancestor) {
        return jniDescendantOf(repo.getRawPointer(), commit, ancestor) != 0;
    }
    public static class Count {
        private final int _ahead;
        private final int _behind;
        public Count(int ahead, int behind) {
            _ahead = ahead;
            _behind = behind;
        }
        public int getAhead() {
            return _ahead;
        }
        public int getBehind() {
            return _behind;
        }
    }
}
