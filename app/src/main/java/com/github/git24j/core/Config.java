package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;
import static com.github.git24j.core.GitException.ErrorCode.ITEROVER;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Config extends CAutoReleasable {
    static native int jniAddFileOndisk(long cfg, String path, int level, long repoPtr, int force);
    static native int jniBackendForeachMatch(long backend, String regexp, CallbackJ callback);
    static native int jniDeleteEntry(long cfg, String name);
    static native int jniDeleteMultivar(long cfg, String name, String regexp);
    static native void jniEntryFree(long entry);
    static native int jniEntryGetIncludeDepth(long entryPtr);
    static native int jniEntryGetLevel(long entryPtr);
    static native String jniEntryGetName(long entryPtr);
    static native String jniEntryGetValue(long entryPtr);
    static native String jniEntryGetBackendType(long entryPtr);
    static native String jniEntryGetOriginPath(long entryPtr);
    static native int jniFindGlobal(Buf out);
    static native int jniFindProgramdata(Buf out);
    static native int jniFindSystem(Buf out);
    static native int jniFindXdg(Buf out);
    static native int jniForeach(long cfg, CallbackJ callback);
    static native int jniForeachMatch(long cfg, String regexp, CallbackJ callback);
    static native void jniFree(long cfg);
    static native int jniGetBool(AtomicInteger out, long cfg, String name);
    static native int jniGetEntry(AtomicLong out, long cfg, String name);
    static native int jniGetInt32(AtomicInteger out, long cfg, String name);
    static native int jniGetInt64(AtomicLong out, long cfg, String name);
    static native int jniGetMultivarForeach(
            long cfg, String name, String regexp, CallbackJ callback);
    static native int jniGetPath(Buf out, long cfg, String name);
    static native int jniGetString(AtomicReference<String> out, long cfg, String name);
    static native int jniGetStringBuf(Buf out, long cfg, String name);
    static native void jniIteratorFree(long iter);
    static native int jniIteratorGlobNew(AtomicLong out, long cfg, String regexp);
    static native int jniIteratorNew(AtomicLong out, long cfg);
    static native int jniLock(AtomicLong tx, long cfg);
    static native int jniMultivarIteratorNew(AtomicLong out, long cfg, String name, String regexp);
    static native int jniNew(AtomicLong out);
    static native int jniNext(AtomicLong entry, long iter);
    static native int jniOpenDefault(AtomicLong out);
    static native int jniOpenGlobal(AtomicLong out, long config);
    static native int jniOpenLevel(AtomicLong out, long parent, int level);
    static native int jniOpenOndisk(AtomicLong out, String path);
    static native int jniParseBool(AtomicInteger out, String value);
    static native int jniParseInt32(AtomicInteger out, String value);
    static native int jniParseInt64(AtomicLong out, String value);
    static native int jniParsePath(Buf out, String value);
    static native int jniSetBool(long cfg, String name, int value);
    static native int jniSetInt32(long cfg, String name, int value);
    static native int jniSetInt64(long cfg, String name, long value);
    static native int jniSetMultivar(long cfg, String name, String regexp, String value);
    static native int jniSetString(long cfg, String name, String value);
    static native int jniSnapshot(AtomicLong out, long config);
    protected Config(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Nullable
    public static String findGlobal() {
        Buf buf = new Buf();
        int e = jniFindGlobal(buf);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        if (buf.getSize() == 0 || buf.getPtr() == null) {
            return null;
        }
        return buf.getString().orElse(null);
    }
    @Nullable
    public static String findXdg() {
        Buf buf = new Buf();
        int e = jniFindXdg(buf);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        if (buf.getSize() == 0 || buf.getPtr() == null) {
            return null;
        }
        return buf.getString().orElse(null);
    }
    @Nullable
    public static String findSystem() {
        Buf buf = new Buf();
        int e = jniFindSystem(buf);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        if (buf.getSize() == 0 || buf.getPtr() == null) {
            return null;
        }
        return buf.getString().orElse(null);
    }
    @CheckForNull
    public static String findProgramdata() {
        Buf buf = new Buf();
        int e = jniFindProgramdata(buf);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        if (buf.getSize() == 0 || buf.getPtr() == null) {
            return null;
        }
        return buf.getString().orElse(null);
    }
    public static Config openDefault() {
        Config cfg = new Config(false, 0);
        Error.throwIfNeeded(jniOpenDefault(cfg._rawPtr));
        return cfg;
    }
    public static Config newConfig() {
        Config cfg = new Config(false, 0);
        Error.throwIfNeeded(jniNew(cfg._rawPtr));
        return cfg;
    }
    @Nonnull
    public static Config openOndisk(@Nonnull String path) {
        Config cfg = new Config(false, 0);
        Error.throwIfNeeded(jniOpenOndisk(cfg._rawPtr, path));
        return cfg;
    }
    public static Config openLevel(ConfigLevel level, Config parent) {
        Config cfg = new Config(false, 0);
        int e = jniOpenLevel(cfg._rawPtr, parent.getRawPointer(), level._code);
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return cfg;
    }
    @CheckForNull
    public static Config openGlobal(@Nonnull Config parent) {
        Config cfg = new Config(false, 0);
        int e = jniOpenGlobal(cfg._rawPtr, parent.getRawPointer());
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return cfg;
    }
    public static boolean parseBool(@Nonnull String value) {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(jniParseBool(out, value));
        return out.get() != 0;
    }
    public static int parseInt(@Nonnull String value) {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(jniParseInt32(out, value));
        return out.get();
    }
    public static long parseLong(@Nonnull String value) {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniParseInt64(out, value));
        return out.get();
    }
    public static String parsePath(@Nonnull String value) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniParsePath(out, value));
        return out.getString()
                .orElseThrow(
                        () ->
                                new GitException(
                                        GitException.ErrorClass.CONFIG,
                                        "could not parse: " + value));
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public void foreachMatch(String regexp, ForeachCb foreachCb) {
        jniForeachMatch(
                getRawPointer(), regexp, entryPtr -> foreachCb.accept(new Entry(true, entryPtr)));
    }
    public void addFileOndisk(String path, ConfigLevel level, Repository repo, boolean force) {
        Error.throwIfNeeded(
                jniAddFileOndisk(
                        getRawPointer(),
                        path,
                        level._code,
                        repo == null ? 0 : repo.getRawPointer(),
                        force ? 1 : 0));
    }
    public Config snapshot() {
        Config cfg = new Config(false, 0);
        Error.throwIfNeeded(jniSnapshot(cfg._rawPtr, getRawPointer()));
        return cfg;
    }
    @Nonnull
    public Optional<Entry> getEntry(@Nonnull String name) {
        Entry entry = new Entry(false, 0);
        int e = jniGetEntry(entry._rawPtr, getRawPointer(), name);
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(entry);
    }
    @Nonnull
    public Optional<Integer> getInt(@Nonnull String name) {
        AtomicInteger out = new AtomicInteger();
        int e = jniGetInt32(out, getRawPointer(), name);
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(out.get());
    }
    @Nonnull
    public Optional<Long> getLong(@Nonnull String name) {
        AtomicLong out = new AtomicLong();
        int e = jniGetInt64(out, getRawPointer(), name);
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(out.get());
    }
    public Optional<Boolean> getBool(String name) {
        AtomicInteger out = new AtomicInteger();
        int e = jniGetBool(out, getRawPointer(), name);
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(out.get() != 0);
    }
    @Nonnull
    public Optional<String> getPath(@Nonnull String name) {
        Buf out = new Buf();
        int e = jniGetPath(out, getRawPointer(), name);
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return out.getString();
    }
    public Optional<String> getString(String name) {
        AtomicReference<String> out = new AtomicReference<>();
        int e = jniGetString(out, getRawPointer(), name);
        if (ENOTFOUND.getCode() == e) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.ofNullable(out.get());
    }
    public Optional<String> getStringBuf(String name) {
        Buf outBuf = new Buf();
        int e = jniGetStringBuf(outBuf, getRawPointer(), name);
        if (ENOTFOUND.getCode() == e) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return outBuf.getString();
    }
    public void getMultivarForeach(
            @Nonnull String name, @Nullable String regexp, @Nonnull ForeachCb cb) {
        int e =
                jniGetMultivarForeach(
                        getRawPointer(),
                        name,
                        regexp,
                        entryPtr -> cb.accept(new Entry(true, entryPtr)));
        if (e == ENOTFOUND.getCode()) {
            return;
        }
        Error.throwIfNeeded(e);
    }
    @Nonnull
    Iterator multivarIteratorNew(@Nonnull String name, @Nullable String regexp) {
        Iterator out = new Iterator(false, 0);
        int e = jniMultivarIteratorNew(out._rawPtr, getRawPointer(), name, regexp);
        Error.throwIfNeeded(e);
        return out;
    }
    public void setInt(@Nonnull String name, int value) {
        Error.throwIfNeeded(jniSetInt32(getRawPointer(), name, value));
    }
    public void setLong(@Nonnull String name, long value) {
        Error.throwIfNeeded(jniSetInt64(getRawPointer(), name, value));
    }
    public void setBool(@Nonnull String name, boolean value) {
        Error.throwIfNeeded(jniSetBool(getRawPointer(), name, value ? 1 : 0));
    }
    public void setString(@Nonnull String name, @Nonnull String value) {
        Error.throwIfNeeded(jniSetString(getRawPointer(), name, value));
    }
    public void setMultivar(@Nonnull String name, @Nonnull String regexp, @Nonnull String value) {
        Error.throwIfNeeded(jniSetMultivar(getRawPointer(), name, regexp, value));
    }
    public List<String> getMultivar(@Nonnull String name, @Nonnull String regexp) {
        List<String> out = new ArrayList<>();
        getMultivarForeach(
                name,
                regexp,
                entry -> {
                    out.add(entry.getValue());
                    return 0;
                });
        return out;
    }
    public void deleteEntry(@Nonnull String name) {
        Error.throwIfNeeded(jniDeleteEntry(getRawPointer(), name));
    }
    public void deleteMultivar(@Nonnull String name, @Nonnull String regexp) {
        Error.throwIfNeeded(jniDeleteMultivar(getRawPointer(), name, regexp));
    }
    public void foreach(ForeachCb cb) {
        int e = jniForeach(getRawPointer(), entryPtr -> cb.accept(new Entry(true, entryPtr)));
        Error.throwIfNeeded(e);
    }
    @Nonnull
    public Iterator iteratorNew() {
        Iterator out = new Iterator(false, 0);
        int e = jniIteratorNew(out._rawPtr, getRawPointer());
        Error.throwIfNeeded(e);
        return out;
    }
    @Nonnull
    public Iterator iteratorGlobalNew(@Nonnull String regexp) {
        Iterator out = new Iterator(false, 0);
        int e = jniIteratorGlobNew(out._rawPtr, getRawPointer(), regexp);
        Error.throwIfNeeded(e);
        return out;
    }
    @Nonnull
    Transaction lock() {
        Transaction out = new Transaction(true, 0);
        Error.throwIfNeeded(jniLock(out._rawPtr, getRawPointer()));
        return out;
    }
    public enum ConfigLevel {
        PROGRAMDATA(1),
        SYSTEM(2),
        XDG(3),
        GLOBAL(4),
        LOCAL(5),
        WORKTREE(6),
        APP(7),
        HIGHEST(-1),
        ;
        private final int _code;
        ConfigLevel(int code) {
            _code = code;
        }
        public int getValue(){
            return _code;
        }
    }
    @FunctionalInterface
    public interface ForeachCb {
        int accept(Entry entry);
    }
    @FunctionalInterface
    private interface CallbackJ {
        int accept(long entryPtr);
    }
    public static class Entry extends CAutoReleasable {
        protected Entry(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
        }
        public String getName() {
            return jniEntryGetName(getRawPointer());
        }
        public String getValue() {
            return jniEntryGetValue(getRawPointer());
        }
        public int getIncludeDepth() {
            return jniEntryGetIncludeDepth(getRawPointer());
        }
        public int getLevel() {
            return jniEntryGetLevel(getRawPointer());
        }
        public String getBackendType(){
            return jniEntryGetBackendType(getRawPointer());
        }
        public String getOriginPath(){
            return jniEntryGetOriginPath(getRawPointer());
        }
    }
    public static class Iterator extends CAutoReleasable {
        protected Iterator(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniIteratorFree(cPtr);
        }
        @CheckForNull
        Entry next() {
            Entry entry = new Entry(true, 0);
            int e = jniNext(entry._rawPtr, getRawPointer());
            if (e == ITEROVER.getCode()) {
                return null;
            }
            Error.throwIfNeeded(e);
            return entry;
        }
    }
}
