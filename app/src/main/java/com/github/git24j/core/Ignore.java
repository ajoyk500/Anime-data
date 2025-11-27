package com.github.git24j.core;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

public class Ignore {
    static native int jniAddRule(long repoPtr, String rules);
    static native int jniClearInternalRules(long repoPtr);
    static native int jniPathIsIgnored(AtomicInteger ignored, long repoPtr, String path);
    public static void addRule(@Nonnull Repository repo, @Nonnull String rules) {
        Error.throwIfNeeded(jniAddRule(repo.getRawPointer(), rules));
    }
    public static boolean pathIsIgnored(@Nonnull Repository repo, @Nonnull String path) {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(jniPathIsIgnored(out, repo.getRawPointer(), path));
        return out.get() != 0;
    }
    public static void clearInternalRules(@Nonnull Repository repo) {
        Error.throwIfNeeded(jniClearInternalRules(repo.getRawPointer()));
    }
}
