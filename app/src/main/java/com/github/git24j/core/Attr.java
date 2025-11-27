package com.github.git24j.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

public class Attr {
    static native int jniAddMacro(long repoPtr, String name, String values);
    static native int jniCacheFlush(long repoPtr);
    static native int jniForeach(long repoPtr, int flags, String path, ForeachCb foreachCb);
    static native int jniGet(
            AtomicReference<String> valueOut, long repoPtr, int flags, String path, String name);
    static native int jniGetMany(
            List<String> valuesOut, long repoPtr, int flags, String path, String[] names);
    static native int jniValue(String attr);
    public static void addMacro(
            @Nonnull Repository repo, @Nonnull String name, @Nonnull String value) {
        Error.throwIfNeeded(jniAddMacro(repo.getRawPointer(), name, value));
    }
    public static void cacheFlush(@Nonnull Repository repo) {
        Error.throwIfNeeded(jniCacheFlush(repo.getRawPointer()));
    }
    public static int foreach(
            @Nonnull Repository repo,
            EnumSet<CheckFlags> flags,
            @Nonnull String path,
            @Nonnull ForeachCb callback) {
        int e = jniForeach(repo.getRawPointer(), IBitEnum.bitOrAll(flags), path, callback);
        Error.throwIfNeeded(e);
        return e;
    }
    @Nonnull
    public static ValueT value(@Nonnull String attr) {
        return IBitEnum.valueOf(jniValue(attr), ValueT.class, ValueT.UNSPECIFIED);
    }
    public static String getAttr(
            @Nonnull Repository repo,
            @Nonnull EnumSet<CheckFlags> flags,
            @Nonnull String path,
            @Nonnull String name) {
        AtomicReference<String> out = new AtomicReference<>();
        int e = jniGet(out, repo.getRawPointer(), IBitEnum.bitOrAll(flags), path, name);
        Error.throwIfNeeded(e);
        return out.get();
    }
    @Nonnull
    public static List<String> getMany(
            @Nonnull Repository repo,
            @Nonnull EnumSet<CheckFlags> flags,
            @Nonnull String path,
            @Nonnull List<String> names) {
        List<String> out = new ArrayList<>();
        int e =
                jniGetMany(
                        out,
                        repo.getRawPointer(),
                        IBitEnum.bitOrAll(flags),
                        path,
                        names.toArray(new String[0]));
        Error.throwIfNeeded(e);
        return out;
    }
    public enum ValueT implements IBitEnum {
        UNSPECIFIED(0),
        TRUE(1),
        FALSE(2),
        STRING(3),
        ;
        private final int _bit;
        ValueT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum CheckFlags implements IBitEnum {
        CHECK_FILE_THEN_INDEX(0),
        CHECK_INDEX_THEN_FILE(1),
        CHECK_INDEX_ONLY(2),
        CHECK_NO_SYSTEM(1 << 2),
        CHECK_INCLUDE_HEAD(1 << 3);
        private final int _bit;
        CheckFlags(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    @FunctionalInterface
    public interface ForeachCb {
        int accept(String name, String value);
    }
}
