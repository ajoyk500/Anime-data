package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Submodule extends CAutoReleasable {
    static native int jniAddFinalize(long submodule);
    static native int jniAddSetup(
            AtomicLong out, long repoPtr, String url, String path, int useGitlink);
    static native int jniAddToIndex(long submodule, int writeIndex);
    static native String jniBranch(long submodule);
    static native int jniClone(AtomicLong out, long submodule, long opts);
    static native int jniFetchRecurseSubmodules(long submodule);
    static native int jniForeach(long repoPtr, Internals.SJCallback foreachCb);
    static native void jniFree(long submodule);
    static native byte[] jniHeadId(long submodule);
    static native int jniIgnore(long submodule);
    static native byte[] jniIndexId(long submodule);
    static native int jniInit(long submodule, int overwrite);
    static native int jniLocation(AtomicInteger locationStatus, long submodule);
    static native int jniLookup(AtomicLong out, long repoPtr, String name);
    static native String jniName(long submodule);
    static native int jniOpen(AtomicLong repo, long submodule);
    static native long jniOwner(long submodule);
    static native String jniPath(long submodule);
    static native int jniReload(long submodule, int force);
    static native int jniRepoInit(AtomicLong out, long sm, int useGitlink);
    static native int jniResolveUrl(Buf out, long repoPtr, String url);
    static native int jniSetBranch(long repoPtr, String name, String branch);
    static native int jniSetFetchRecurseSubmodules(
            long repoPtr, String name, int fetchRecurseSubmodules);
    static native int jniSetIgnore(long repoPtr, String name, int ignore);
    static native int jniSetUpdate(long repoPtr, String name, int update);
    static native int jniSetUrl(long repoPtr, String name, String url);
    static native int jniStatus(AtomicInteger status, long repoPtr, String name, int ignore);
    static native int jniSync(long submodule);
    static native int jniUpdate(long submodule, int init, long options);
    static native int jniUpdateOptionsGetAllowFetch(long update_optionsPtr);
    static native long jniUpdateOptionsGetCheckoutOpts(long update_optionsPtr);
    static native long jniUpdateOptionsGetFetchOpts(long update_optionsPtr);
    static native int jniUpdateOptionsGetVersion(long update_optionsPtr);
    static native int jniUpdateOptionsNew(AtomicLong outPtr, int version);
    static native void jniUpdateOptionsSetAllowFetch(long update_optionsPtr, int allowFetch);
    static native int jniUpdateInitOptions(long updateOptionsPtr, int version);
    static native int jniUpdateStrategy(long submodule);
    static native String jniUrl(long submodule);
    static native byte[] jniWdId(long submodule);
    protected Submodule(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    public static void foreach(@Nonnull Repository repo, @Nonnull Callback callback) {
        Error.throwIfNeeded(
                jniForeach(
                        repo.getRawPointer(),
                        (name, ptr) -> callback.accept(new Submodule(true, ptr), name)));
    }
    @Nonnull
    public static Submodule addSetup(
            @Nonnull Repository repo, @Nonnull String url, @Nonnull String path, boolean useGitlink) {
        Submodule out = new Submodule(false, 0);
        Error.throwIfNeeded(
                jniAddSetup(
                        out._rawPtr,
                        repo.getRawPointer(),
                        url,
                        path,
                        useGitlink ? 1 : 0));
        return out;
    }
    @Nullable
    public static Submodule lookup(@Nonnull Repository repo, @Nonnull String name) {
        Submodule out = new Submodule(false, 0);
        int e = jniLookup(out._rawPtr, repo.getRawPointer(), name);
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }
    @Nonnull
    public static String resolveUrl(@Nonnull Repository repo, @Nonnull String url) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniResolveUrl(out, repo.getRawPointer(), url));
        return out.getString().orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("'%s' cannot be resolved to an absolute url, check path please", url)
                )
        );
    }
    public static void setBranch(
            @Nonnull Repository repo, @Nonnull String name, @Nonnull String branch) {
        Error.throwIfNeeded(jniSetBranch(repo.getRawPointer(), name, branch));
    }
    @Nonnull
    public static RecurseT setFetchRecurseSubmodules(
            @Nonnull Repository repo,
            @Nonnull String name,
            @Nonnull RecurseT fetchRecurseSubmodules) {
        int e =
                jniSetFetchRecurseSubmodules(
                        repo.getRawPointer(), name, fetchRecurseSubmodules.getBit());
        return RecurseT.valueOf(e);
    }
    public static void setIgnore(@Nonnull Repository repo, @Nonnull String name, boolean ignore) {
        Error.throwIfNeeded(jniSetIgnore(repo.getRawPointer(), name, ignore ? 1 : 0));
    }
    public static void setUpdate(
            @Nonnull Repository repo, @Nonnull String name, @Nonnull UpdateT update) {
        Error.throwIfNeeded(jniSetUpdate(repo.getRawPointer(), name, update.getBit()));
    }
    public static void setUrl(@Nonnull Repository repo, @Nonnull String name, @Nonnull String url) {
        Error.throwIfNeeded(jniSetUrl(repo.getRawPointer(), name, url));
    }
    public static EnumSet<StatusT> status(
            @Nonnull Repository repo, @Nonnull String name, @Nullable IgnoreT ignore) {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(
                jniStatus(
                        out,
                        repo.getRawPointer(),
                        name,
                        ignore == null ? IgnoreT.UNSPECIFIED.getBit() : ignore.getBit()));
        return IBitEnum.parse(out.get(), StatusT.class);
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public void addFinalize() {
        Error.throwIfNeeded(jniAddFinalize(getRawPointer()));
    }
    public void addToIndex(boolean writeIndex) {
        Error.throwIfNeeded(jniAddToIndex(getRawPointer(), writeIndex ? 1 : 0));
    }
    @CheckForNull
    public String branch() {
        return jniBranch(getRawPointer());
    }
    @Nonnull
    public RecurseT fetchRecurseSubmodules() {
        int r = jniFetchRecurseSubmodules(getRawPointer());
        switch (r) {
            case 0:
                return RecurseT.NO;
            case 1:
                return RecurseT.YES;
            case 2:
                return RecurseT.ONDEMAN;
            default:
                throw new IllegalStateException(
                        "Illegal submodule.<submodule>.fetchRecurseSubmodules settings");
        }
    }
    @CheckForNull
    public Oid headId() {
        return Oid.ofNullable(jniHeadId(getRawPointer()));
    }
    public IgnoreT ignore() {
        return IBitEnum.valueOf(jniIgnore(getRawPointer()), IgnoreT.class);
    }
    @CheckForNull
    public Oid indexId() {
        return Oid.ofNullable(jniIndexId(getRawPointer()));
    }
    public void init(boolean overwrite) {
        Error.throwIfNeeded(jniInit(getRawPointer(), overwrite ? 1 : 0));
    }
    @Nonnull
    public EnumSet<StatusT> location() {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(jniLocation(out, getRawPointer()));
        return IBitEnum.parse(out.get(), StatusT.class);
    }
    @Nonnull
    public String name() {
        return jniName(getRawPointer());
    }
    @Nonnull
    public Repository open() {
        Repository repo = new Repository(0);
        Error.throwIfNeeded(jniOpen(repo._rawPtr, getRawPointer()));
        return repo;
    }
    @Nonnull
    public Repository owner() {
        return new Repository(jniOwner(getRawPointer()));
    }
    @Nonnull
    public String path() {
        return jniPath(getRawPointer());
    }
    public void reload(boolean force) {
        Error.throwIfNeeded(jniReload(getRawPointer(), force ? 1 : 0));
    }
    @Nonnull
    public Repository repoInit(boolean useGitlink) {
        Repository out = new Repository(0);
        Error.throwIfNeeded(jniRepoInit(out._rawPtr, getRawPointer(), useGitlink ? 1 : 0));
        return out;
    }
    public void sync() {
        Error.throwIfNeeded(jniSync(getRawPointer()));
    }
    public void update(boolean init, @Nullable UpdateOptions options) {
        Error.throwIfNeeded(
                jniUpdate(
                        getRawPointer(),
                        init ? 1 : 0,
                        options == null ? 0 : options.getRawPointer()));
    }
    @Nonnull
    public UpdateT updateStrategy() {
        int r = jniUpdateStrategy(getRawPointer());
        return IBitEnum.valueOf(r, UpdateT.class, UpdateT.DEFAULT);
    }
    @Nullable
    public String url() {
        return jniUrl(getRawPointer());
    }
    @Nullable
    public Oid wdId() {
        return Optional.ofNullable(jniWdId(getRawPointer())).map(Oid::of).orElse(null);
    }
    @Nonnull
    public Repository clone(@Nullable UpdateOptions updateOptions) {
        Repository out = new Repository(0);
        int e =
                jniClone(
                        out._rawPtr,
                        getRawPointer(),
                        updateOptions == null ? 0 : updateOptions.getRawPointer());
        Error.throwIfNeeded(e);
        return out;
    }
    public enum StatusT implements IBitEnum {
        IN_HEAD(1 << 0),
        IN_INDEX(1 << 1),
        IN_CONFIG(1 << 2),
        IN_WD(1 << 3),
        INDEX_ADDED(1 << 4),
        INDEX_DELETED(1 << 5),
        INDEX_MODIFIED(1 << 6),
        WD_UNINITIALIZED(1 << 7),
        WD_ADDED(1 << 8),
        WD_DELETED(1 << 9),
        WD_MODIFIED(1 << 10),
        WD_INDEX_MODIFIED(1 << 11),
        WD_WD_MODIFIED(1 << 12),
        WD_UNTRACKED(1 << 13);
        private final int _bit;
        StatusT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum RecurseT implements IBitEnum {
        NO(0),
        YES(1),
        ONDEMAN(2);
        private final int _bit;
        RecurseT(int bit) {
            _bit = bit;
        }
        static RecurseT valueOf(int val) {
            switch (val) {
                case 1:
                    return YES;
                case 0:
                    return NO;
                default:
                    return ONDEMAN;
            }
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum IgnoreT implements IBitEnum {
        UNSPECIFIED(-1),
        NONE(1),
        UNTRACKED(2),
        DIRTY(3),
        ALL(4),
        ;
        private final int _bit;
        IgnoreT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum UpdateT implements IBitEnum {
        CHECKOUT(1),
        REBASE(2),
        MERGE(3),
        NONE(4),
        DEFAULT(0);
        private final int _bit;
        UpdateT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    @FunctionalInterface
    public interface Callback {
        int accept(Submodule sm, String name);
    }
    public static class UpdateOptions extends CAutoReleasable {
        public static final int VERSION = 1;
        protected UpdateOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static UpdateOptions create(int version) {
            UpdateOptions opts = new UpdateOptions(false, 0);
            Error.throwIfNeeded(jniUpdateOptionsNew(opts._rawPtr, version));
            return opts;
        }
        public static UpdateOptions createDefault() {
            return create(VERSION);
        }
        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }
        public int getVersion() {
            return jniUpdateOptionsGetVersion(getRawPointer());
        }
        @Nonnull
        public Checkout.Options getCheckoutOpts() {
            long ptr = jniUpdateOptionsGetCheckoutOpts(getRawPointer());
            return new Checkout.Options(true, ptr);
        }
        @Nonnull
        public FetchOptions getFetchOpts() {
            long ptr = jniUpdateOptionsGetFetchOpts(getRawPointer());
            return new FetchOptions(true, ptr);
        }
        public boolean getAllowFetch() {
            return jniUpdateOptionsGetAllowFetch(getRawPointer()) != 0;
        }
        public void setAllowFetch(boolean allowFetch) {
            jniUpdateOptionsSetAllowFetch(getRawPointer(), allowFetch ? 1 : 0);
        }
    }
}
