package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;
import static com.github.git24j.core.GitException.ErrorCode.EUNBORNBRANCH;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Repository extends CAutoCloseable {
    static native String jniCommondir(long repoPtr);
    static native int jniConfig(AtomicLong outCfg, long repoPtr);
    static native int jniConfigSnapshot(AtomicLong outCfg, long repoPtr);
    static native int jniDetachHead(long repoPtr);
    static native int jniDiscover(Buf out, String startPath, int accessFs, String ceilingDirs);
    static native int jniFetchheadForeach(long repoPtr, FetchHeadForeachCb cb);
    static native void jniFree(long repoPtr);
    static native String jniGetNamespace(long repoPtr);
    static native int jniHashfile(Oid oid, long repoPtr, String path, int type, String asPath);
    static native int jniHead(AtomicLong gitRef, long repoPtr);
    static native int jniHeadDetached(long repoPtr);
    static native int jniHeadForWorktree(AtomicLong outRef, long repoPtr, String name);
    static native int jniHeadUnborn(long repoPtr);
    static native int jniIdent(Identity identity, long repoPtr);
    static native int jniIndex(AtomicLong outIndex, long repoPtr);
    static native int jniInit(AtomicLong outRepo, String path, int isBare);
    static native int jniInitExt(AtomicLong outRepo, String repoPath, InitOptions initOpts);
    static native int jniInitOptionsInit(InitOptions initOpts, int version);
    static native int jniIsBare(long repoPtr);
    static native int jniIsEmpty(long repoPtr);
    static native int jniIsShallow(long repoPtr);
    static native int jniIsWorktree(long repoPtr);
    static native int jniItemPath(Buf buf, long repoPtr, int item);
    static native int jniMergeheadForeach(long repoPtr, MergeheadForeachCb cb);
    static native int jniMessage(Buf buf, long repoPtr);
    static native int jniMessageRemove(long repoPtr);
    static native int jniOdb(AtomicLong outOdb, long repoPtr);
    static native int jniOpen(AtomicLong outRepo, String path);
    static native int jniOpenBare(AtomicLong outRepo, String path);
    static native int jniOpenExt(AtomicLong outRepo, String path, int flags, String ceilingDirs);
    static native int jniOpenFromWorkTree(AtomicLong outRepo, long wtPtr);
    static native String jniPath(long repoPtr);
    static native int jniRefdb(AtomicLong outRefdb, long repoPtr);
    static native int jniSetHead(long repoPtr, String refName);
    static native int jniSetHeadDetached(long repoPtr, Oid oid);
    static native int jniSetHeadDetachedFromAnnotated(long repoPtr, long commitishPtr);
    static native int jniSetIdent(long repoPtr, String name, String email);
    static native int jniSetNamespace(long repoPtr, String namespace);
    static native int jniSetWorkdir(long repoPtr, String workdir, int updateGitlink);
    static native int jniState(long repoPtr);
    static native int jniStateCleanup(long repoPtr);
    static native String jniWorkdir(long repoPtr);
    static native int jniWrapOdb(AtomicLong outRepo, long odbPtr);
    public Repository(long rawPointer) {
        super(rawPointer);
    }
    static Repository ofRaw(long ptr) {
        return new Repository(ptr);
    }
    @Nonnull
    public static Repository open(@Nonnull Path path) {
        return open(path.toString());
    }
    @Nonnull
    public static Repository open(@Nonnull String path) {
        AtomicLong outRepo = new AtomicLong();
        int error = jniOpen(outRepo, path);
        Error.throwIfNeeded(error);
        return new Repository(outRepo.get());
    }
    @Nonnull
    public static Repository init(@Nonnull String path, boolean isBare) {
        Repository repo = new Repository(0);
        Error.throwIfNeeded(jniInit(repo._rawPtr, path, isBare ? 1 : 0));
        return repo;
    }
    @Nonnull
    public static Repository initExt(@Nonnull String path, @Nullable InitOptions initOpts) {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniInitExt(out, path, initOpts));
        return new Repository(out.get());
    }
    @Nullable
    public static String discover(
            @Nonnull String startPath, boolean acrossFs, @Nullable String ceilingDirs) {
        Buf outBuf = new Buf();
        jniDiscover(outBuf, startPath, acrossFs ? 1 : 0, ceilingDirs);
        return outBuf.getString().orElse(null);
    }
    @Nonnull
    public static Repository openExt(
            @Nonnull String path, @Nullable EnumSet<OpenFlag> flags, @Nullable String ceilingDirs) {
        AtomicLong out = new AtomicLong();
        int error = jniOpenExt(out, path, IBitEnum.bitOrAll(flags), ceilingDirs);
        Error.throwIfNeeded(error);
        return new Repository(out.get());
    }
    @Nonnull
    public static Repository openBare(@Nonnull String path) {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniOpenBare(out, path.toString()));
        return new Repository(out.get());
    }
    @Override
    protected void releaseOnce(long cPtr) {
        jniFree(cPtr);
    }
    @Nonnull
    public String getPath() {
        return jniPath(getRawPointer());
    }
    @CheckForNull
    public String workdir() {
        return jniWorkdir(getRawPointer());
    }
    public void setWorkdir(String path, boolean updateGitLink) {
        Error.throwIfNeeded(jniSetWorkdir(getRawPointer(), path, updateGitLink ? 1 : 0));
    }
    public String getCommondir() {
        return jniCommondir(getRawPointer());
    }
    public boolean isBare() {
        return jniIsBare(getRawPointer()) == 1;
    }
    public boolean isWorktree() {
        return jniIsWorktree(getRawPointer()) == 1;
    }
    public Config config() {
        AtomicLong outCfg = new AtomicLong();
        Error.throwIfNeeded(jniConfig(outCfg, getRawPointer()));
        return new Config(false, outCfg.get());
    }
    public Config configSnapshot() {
        AtomicLong outCfg = new AtomicLong();
        Error.throwIfNeeded(jniConfigSnapshot(outCfg, getRawPointer()));
        return new Config(false, outCfg.get());
    }
    public Odb odb() {
        AtomicLong outOdb = new AtomicLong();
        Error.throwIfNeeded(jniOdb(outOdb, getRawPointer()));
        return new Odb(false, outOdb.get());
    }
    public Refdb refdb() {
        AtomicLong outRefdb = new AtomicLong();
        Error.throwIfNeeded(jniRefdb(outRefdb, getRawPointer()));
        return new Refdb(false, outRefdb.get());
    }
    @Nullable
    public String message() {
        Buf buf = new Buf();
        int e = jniMessage(buf, getRawPointer());
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return buf.getString().orElse(null);
    }
    public void messageRemove() {
        Error.throwIfNeeded(jniMessageRemove(getRawPointer()));
    }
    public void stateCleanup() {
        Error.throwIfNeeded(jniStateCleanup(getRawPointer()));
    }
    public void fetchheadForeach(FetchHeadForeachCb cb) {
        Error.throwIfNeeded(jniFetchheadForeach(getRawPointer(), cb));
    }
    public void mergeHeadForeach(MergeheadForeachCb cb) {
        Error.throwIfNeeded(jniMergeheadForeach(getRawPointer(), cb));
    }
    public Oid hashfile(String path, GitObject.Type type, String asPath) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniHashfile(oid, getRawPointer(), path, type.getBit(), asPath));
        return oid;
    }
    public void setHead(String refName) {
        Error.throwIfNeeded(jniSetHead(getRawPointer(), refName));
    }
    public void setHeadDetached(Oid oid) {
        Error.throwIfNeeded(jniSetHeadDetached(getRawPointer(), oid));
    }
    public void setHeadDetachedFromAnnotated(AnnotatedCommit annotatedCommit) {
        Error.throwIfNeeded(jniSetHeadDetachedFromAnnotated(getRawPointer(), annotatedCommit.getRawPointer()));
    }
    public void detachHead() {
        Error.throwIfNeeded(jniDetachHead(getRawPointer()));
    }
    @CheckForNull
    public StateT state() {
        int idx = jniState(getRawPointer());
        return IBitEnum.valueOf(idx, StateT.class);
    }
    public String getNamespace() {
        return jniGetNamespace(getRawPointer());
    }
    public void setNamespace(String namespace) {
        Error.throwIfNeeded(jniSetNamespace(getRawPointer(), namespace));
    }
    public boolean isShallow() {
        return jniIsShallow(getRawPointer()) == 1;
    }
    @Deprecated
    public boolean isShadow() {
        return isShallow();
    }
    public Identity ident() {
        Identity identity = new Identity("", "");
        Error.throwIfNeeded(jniIdent(identity, getRawPointer()));
        return identity;
    }
    public void setIdent(String name, String email) {
        Error.throwIfNeeded(jniSetIdent(getRawPointer(), name, email));
    }
    @Override
    long getRawPointer() {
        long ptr = _rawPtr.get();
        if (ptr == 0) {
            throw new IllegalStateException("Repository has been closed");
        }
        return ptr;
    }
    @Override
    public void close() {
        free();
    }
    public void free() {
        if (_rawPtr.get() != 0) {
            jniFree(_rawPtr.getAndSet(0));
        }
    }
    @Nullable
    public Reference head() {
        Reference out = new Reference(true, 0);
        int e = jniHead(out._rawPtr, getRawPointer());
        if (ENOTFOUND.getCode() == e || EUNBORNBRANCH.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }
    @Nullable
    public Reference headForWorkTree(@Nonnull String name) {
        Reference out = new Reference(true, 0);
        int e = jniHeadForWorktree(out._rawPtr, _rawPtr.get(), name);
        if (ENOTFOUND.getCode() == e || EUNBORNBRANCH.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }
    public boolean headDetached() {
        int error = jniHeadDetached(_rawPtr.get());
        Error.throwIfNeeded(error);
        return error == 1;
    }
    public boolean headUnborn() {
        int error = jniHeadUnborn(_rawPtr.get());
        Error.throwIfNeeded(error);
        return error == 1;
    }
    public boolean isEmpty() {
        int error = jniIsEmpty(_rawPtr.get());
        Error.throwIfNeeded(error);
        return error == 1;
    }
    @CheckForNull
    public String itemPath(Item item) {
        Buf buf = new Buf();
        int error = jniItemPath(buf, _rawPtr.get(), item.ordinal());
        Error.throwIfNeeded(error);
        return buf.getString().orElse(null);
    }
    @Nonnull
    public Index index() {
        Index index = new Index(false, 0);
        int error = jniIndex(index._rawPtr, getRawPointer());
        Error.throwIfNeeded(error);
        return index;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Repository that = (Repository) o;
        if (this._rawPtr.equals(that._rawPtr)) {
            return true;
        }
        return Objects.equals(this.getPath(), that.getPath());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getPath());
    }
    public enum Item {
        GITDIR,
        WORKDIR,
        COMMONDIR,
        INDEX,
        OBJECTS,
        REFS,
        PACKED_REFS,
        REMOTES,
        CONFIG,
        INFO,
        HOOKS,
        LOGS,
        MODULES,
        WORKTREES,
        WORKTREE_CONFIG,
        LAST
    }
    public enum OpenFlag implements IBitEnum {
        NO_FLAG(0),
        NO_SEARCH(1 << 0),
        CROSS_FS(1 << 1),
        BARE(1 << 2),
        NO_DOTGIT(1 << 3),
        FROM_ENV(1 << 4);
        private final int _bit;
        OpenFlag(int bit) {
            this._bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum StateT implements IBitEnum {
        NONE(0),
        MERGE(1),
        REVERT(2),
        REVERT_SEQUENCE(3),
        CHERRYPICK(4),
        CHERRYPICK_SEQUENCE(5),
        BISECT(6),
        REBASE(7),
        REBASE_INTERACTIVE(8),
        REBASE_MERGE(9),
        APPLY_MAILBOX(10),
        APPLY_MAILBOX_OR_REBASE(11);
        private final int _bit;
        StateT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum InitFlagT implements IBitEnum {
        BARE(1 << 0),
        NO_REINIT(1 << 1),
        NO_DOTGIT_DIR(1 << 2),
        MKDIR(1 << 3),
        MKPATH(1 << 4),
        EXTERNAL_TEMPLATE(1 << 5),
        RELATIVE_GITLINK(1 << 6);
        private final int _bit;
        InitFlagT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum InitModeT implements IBitEnum {
        SHARED_UMASK(0),
        SHARED_GROUP(0002775),
        SHARED_ALL(0002777),
        ;
        private final int _bit;
        InitModeT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public static class InitOptions {
        public static final int VERSION = 1;
        private final int version;
        private int flags;
        private int mode;
        private String workdirPath;
        private String description;
        private String templatePath;
        private String initialHead;
        private String originUrl;
        public InitOptions(int version) {
            this.version = version;
        }
        @Nonnull
        public static InitOptions defaultOpts() {
            return new InitOptions(VERSION);
        }
        public int getVersion() {
            return version;
        }
        public int getFlags() {
            return flags;
        }
        public void setFlags(EnumSet<InitFlagT> flags) {
            this.flags = IBitEnum.bitOrAll(flags);
        }
        public int getMode() {
            return mode;
        }
        public void setMode(int mode) {
            this.mode = mode;
        }
        public void setMode(InitModeT mode) {
            this.mode = mode.getBit();
        }
        public String getWorkdirPath() {
            return workdirPath;
        }
        public void setWorkdirPath(String workdirPath) {
            this.workdirPath = workdirPath;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public String getTemplatePath() {
            return templatePath;
        }
        public void setTemplatePath(String templatePath) {
            this.templatePath = templatePath;
        }
        public String getInitialHead() {
            return initialHead;
        }
        public void setInitialHead(String initialHead) {
            this.initialHead = initialHead;
        }
        public String getOriginUrl() {
            return originUrl;
        }
        public void setOriginUrl(String originUrl) {
            this.originUrl = originUrl;
        }
    }
    public static class Identity {
        private final String name;
        private final String email;
        public Identity(String name, String email) {
            this.name = name;
            this.email = email;
        }
        public String getName() {
            return name;
        }
        public String getEmail() {
            return email;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Identity identity = (Identity) o;
            return Objects.equals(name, identity.name) && Objects.equals(email, identity.email);
        }
        @Override
        public int hashCode() {
            return Objects.hash(name, email);
        }
    }
    public abstract static class FetchHeadForeachCb {
        private int accept(String remoteUrl, byte[] oidRaw, int isMerge) {
            return call(remoteUrl, Oid.of(oidRaw), isMerge == 1);
        }
        public abstract int call(String remoteUrl, Oid oid, boolean isMerge);
    }
    public abstract static class MergeheadForeachCb {
        private int accept(byte[] oidRaw) {
            return call(Oid.of(oidRaw));
        }
        public abstract int call(Oid oid);
    }
}
