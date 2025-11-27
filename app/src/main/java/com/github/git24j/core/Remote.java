package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Remote extends CAutoReleasable {
    static native int jniAddFetch(long repoPtr, String remote, String refspec);
    static native int jniAddPush(long repoPtr, String remote, String refspec);
    static native int jniAutotag(long remote);
    static native void jniCallbacksFree(long cbsPtr);
    static native int jniCallbacksNew(AtomicLong outCb, int version);
    static native void jniCallbacksSetCallbackObject(long cbsPtr, Callbacks cbsObject, int callbackType);
    static native void jniCallbacksTest(long cbsPtr, Callbacks callbacks);
    static native int jniConnect(
            long remote, int direction, long callbacks, long proxyOpts, String[] customHeaders);
    static native int jniConnected(long remote);
    static native int jniCreate(AtomicLong out, long repoPtr, String name, String url);
    static native int jniCreateAnonymous(AtomicLong out, long repoPtr, String url);
    static native int jniCreateDetached(AtomicLong out, String url);
    static native void jniCreateOptionsFree(long optsPtr);
    static native String jniCreateOptionsGetFetchspec(long create_optionsPtr);
    static native int jniCreateOptionsGetFlags(long create_optionsPtr);
    static native String jniCreateOptionsGetName(long create_optionsPtr);
    static native long jniCreateOptionsGetRepository(long create_optionsPtr);
    static native int jniCreateOptionsGetVersion(long create_optionsPtr);
    static native int jniCreateOptionsNew(AtomicLong outOpts, int version);
    static native void jniCreateOptionsSetFetchspec(long create_optionsPtr, String fetchspec);
    static native void jniCreateOptionsSetFlags(long create_optionsPtr, int flags);
    static native void jniCreateOptionsSetName(long create_optionsPtr, String name);
    static native void jniCreateOptionsSetRepository(long create_optionsPtr, long repository);
    static native void jniCreateOptionsSetVersion(long create_optionsPtr, int version);
    static native int jniCreateWithFetchspec(
            AtomicLong out, long repoPtr, String name, String url, String fetch);
    static native int jniCreateWithOpts(AtomicLong out, String url, long opts);
    static native int jniDefaultBranch(Buf out, long remote);
    static native int jniDelete(long repoPtr, String name);
    static native void jniDisconnect(long remote);
    static native int jniDownload(long remote, String[] refspecs, long opts);
    static native int jniDup(AtomicLong dest, long source);
    static native int jniFetch(long remote, String[] refspecs, long opts, String reflogMessage);
    static native void jniFetchOptionsFree(long ptr);
    static native long jniFetchOptionsGetCallbacks(long fetch_optionsPtr);
    static native void jniFetchOptionsGetCustomHeaders(
            long fetch_optionsPtr, List<String> customHeaders);
    static native int jniFetchOptionsGetDownloadTags(long fetch_optionsPtr);
    static native long jniFetchOptionsGetProxyOpts(long fetch_optionsPtr);
    static native int jniFetchOptionsGetPrune(long fetch_optionsPtr);
    static native int jniFetchOptionsGetUpdateFetchhead(long fetch_optionsPtr);
    static native int jniFetchOptionsGetVersion(long fetch_optionsPtr);
    static native void jniFetchOptionsSetDepth(long fetch_optionsPtr, int depth);
    static native int jniFetchOptionsGetDepth(long fetch_optionsPtr);
    static native void jniFetchOptionsSetFollowRedirects(long fetch_optionsPtr, int redirectT);
    static native int jniFetchOptionsGetFollowRedirects(long fetch_optionsPtr);
    static native int jniFetchOptionsNew(AtomicLong outPtr, int version);
    static native int jniInitCallbacks(long remoteCallbacksPtr, int version);
    static native void jniFetchOptionsSetCustomHeaders(
            long fetch_optionsPtr, String[] customHeaders);
    static native void jniFetchOptionsSetDownloadTags(long fetch_optionsPtr, int downloadTags);
    static native void jniFetchOptionsSetPrune(long fetch_optionsPtr, int prune);
    static native void jniFetchOptionsSetUpdateFetchhead(
            long fetch_optionsPtr, int updateFetchhead);
    static native void jniFetchOptionsSetVersion(long fetch_optionsPtr, int version);
    static native void jniFree(long remote);
    static native int jniGetFetchRefspecs(List<String> array, long remote);
    static native int jniGetPushRefspecs(List<String> array, long remote);
    static native long jniGetRefspec(long remote, int n);
    static native int jniIsValidName(String remoteName);
    static native int jniList(List<String> out, long repoPtr);
    static native int jniLookup(AtomicLong out, long repoPtr, String name);
    static native String jniName(long remote);
    static native long jniOwner(long remote);
    static native int jniPrune(long remote, long callbacks);
    static native int jniPruneRefs(long remote);
    static native int jniPush(long remote, String[] refspecs, long opts);
    static native void jniPushOptionsFree(long optsPtr);
    static native long jniPushOptionsGetCallbacks(long push_optionsPtr);
    static native void jniPushOptionsGetCustomHeaders(
            long push_optionsPtr, List<String> outHeaders);
    static native int jniPushOptionsGetPbParallelism(long push_optionsPtr);
    static native long jniPushOptionsGetProxyOpts(long push_optionsPtr);
    static native int jniPushOptionsGetVersion(long push_optionsPtr);
    static native int jniPushOptionsNew(AtomicLong outPtr, int version);
    static native void jniPushOptionsSetCustomHeaders(long push_optionsPtr, String[] customHeaders);
    static native void jniPushOptionsSetRemotePushOptions(long push_optionsPtr, String[] remotePushOptions);
    static native void jniPushOptionsGetRemotePushOptions(long push_optionsPtr, List<String> outRemotePushOptions);
    static native void jniPushOptionsSetPbParallelism(long push_optionsPtr, int pbParallelism);
    static native void jniPushOptionsSetVersion(long push_optionsPtr, int version);
    static native void jniPushUpdateFree(long push_updatePtr);
    static native byte[] jniPushUpdateGetDst(long push_updatePtr);
    static native String jniPushUpdateGetDstRefname(long push_updatePtr);
    static native byte[] jniPushUpdateGetSrc(long push_updatePtr);
    static native String jniPushUpdateGetSrcRefname(long push_updatePtr);
    static native long jniPushUpdateNew();
    static native void jniPushUpdateSetDst(long push_updatePtr, Oid dst);
    static native void jniPushUpdateSetDstRefname(long push_updatePtr, String dstRefname);
    static native void jniPushUpdateSetSrc(long push_updatePtr, Oid src);
    static native void jniPushUpdateSetSrcRefname(long push_updatePtr, String srcRefname);
    static native String jniPushurl(long remote);
    static native int jniRefspecCount(long remote);
    static native int jniRename(List<String> problems, long repoPtr, String name, String newName);
    static native int jniSetAutotag(long repoPtr, String remote, int value);
    static native int jniSetPushurl(long repoPtr, String remote, String url);
    static native int jniSetUrl(long repoPtr, String remote, String url);
    static native long jniStats(long remote);
    static native void jniStop(long remote);
    static native int jniUpdateTips(
            long remote,
            long callbacks,
            int updateFetchhead,
            int downloadTags,
            String reflogMessage);
    static native int jniUpload(long remote, String[] refspecs, long opts);
    static native String jniUrl(long remote);
    protected Remote(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    public static void addFetch(
            @Nonnull Repository repo, @Nonnull String remote, @Nonnull String refspec) {
        Error.throwIfNeeded(jniAddFetch(repo.getRawPointer(), remote, refspec));
    }
    public static void addPush(
            @Nonnull Repository repo, @Nonnull String remote, @Nonnull String refspec) {
        Error.throwIfNeeded(jniAddPush(repo.getRawPointer(), remote, refspec));
    }
    @Nonnull
    public static Remote create(@Nonnull Repository repo, @Nonnull String name, @Nonnull String url) {
        Remote remote = new Remote(false, 0);
        Error.throwIfNeeded(jniCreate(remote._rawPtr, repo.getRawPointer(), name, url));
        return remote;
    }
    @Nonnull
    public static Remote createAnonymous(@Nonnull Repository repo, @Nonnull String url) {
        Remote remote = new Remote(false, 0);
        Error.throwIfNeeded(
                jniCreateAnonymous(remote._rawPtr, repo.getRawPointer(), url));
        return remote;
    }
    @Nonnull
    public static Remote createDetached(@Nonnull String url) {
        Remote remote = new Remote(false, 0);
        Error.throwIfNeeded(jniCreateDetached(remote._rawPtr, url));
        return remote;
    }
    @Nonnull
    public static Remote createWithFetchspec(
            @Nonnull Repository repo,
            @Nonnull String name,
            @Nonnull String url,
            @Nullable String fetch) {
        Remote remote = new Remote(false, 0);
        Error.throwIfNeeded(
                jniCreateWithFetchspec(
                        remote._rawPtr, repo.getRawPointer(), name, url, fetch));
        return remote;
    }
    @Nonnull
    public static Remote createWithOpts(@Nonnull String url, @Nullable CreateOptions opts) {
        Remote remote = new Remote(false, 0);
        Error.throwIfNeeded(
                jniCreateWithOpts(
                        remote._rawPtr, url, opts == null ? 0 : opts.getRawPointer()));
        return remote;
    }
    public static void delete(@Nonnull Repository repo, @Nonnull String name) {
        Error.throwIfNeeded(jniDelete(repo.getRawPointer(), name));
    }
    @Nonnull
    public static List<String> list(@Nonnull Repository repo) {
        List<String> out = new ArrayList<>();
        Error.throwIfNeeded(jniList(out, repo.getRawPointer()));
        return out;
    }
    @Nullable
    public static Remote lookup(@Nonnull Repository repo, @Nonnull String name) {
        Remote out = new Remote(true, 0);
        int e = jniLookup(out._rawPtr, repo.getRawPointer(), name);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }
    @Nonnull
    public static List<String> rename(
            @Nonnull Repository repo, @Nonnull String name, @Nonnull String newName) {
        List<String> problems = new ArrayList<>();
        Error.throwIfNeeded(jniRename(problems, repo.getRawPointer(), name, newName));
        return problems;
    }
    public static void setAutotag(
            @Nonnull Repository repo, @Nonnull String remote, @Nonnull AutotagOptionT value) {
        Error.throwIfNeeded(jniSetAutotag(repo.getRawPointer(), remote, value.getBit()));
    }
    public static void setPushurl(
            @Nonnull Repository repo, @Nonnull String remote, @Nullable String url) {
        Error.throwIfNeeded(
                jniSetPushurl(repo.getRawPointer(), remote, url));
    }
    public static void setUrl(@Nonnull Repository repo, @Nonnull String remote, @Nullable String url) {
        Error.throwIfNeeded(
                jniSetUrl(repo.getRawPointer(), remote, url));
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    @Nonnull
    AutotagOptionT autotag() {
        int t = jniAutotag(getRawPointer());
        AutotagOptionT ret = IBitEnum.valueOf(t, AutotagOptionT.class);
        if (ret == null) {
            throw new GitException(
                    GitException.ErrorClass.CONFIG,
                    "remote autotag(" + t + ") is not recognizable");
        }
        return ret;
    }
    public void connect(
            @Nonnull Direction direction,
            @Nullable Callbacks callbacks,
            @Nullable Proxy.Options proxyOpts,
            @Nullable List<String> customHeaders) {
        Error.throwIfNeeded(
                jniConnect(
                        getRawPointer(),
                        direction.ordinal(),
                        callbacks == null ? 0 : Callbacks.createDefault().getRawPointer(),
                        proxyOpts == null ? 0 : proxyOpts.getRawPointer(),
                        customHeaders == null
                                ? new String[0]
                                : customHeaders.toArray(new String[0])));
    }
    public boolean connected() {
        return jniConnected(getRawPointer()) == 1;
    }
    @Nullable
    public String defaultBranch() {
        Buf out = new Buf();
        int e = jniDefaultBranch(out, getRawPointer());
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        return out.getString().orElse(null);
    }
    public void disconnect() {
        jniDisconnect(getRawPointer());
    }
    public void download(@Nonnull String[] refspecs, @Nullable FetchOptions opts) {
        Error.throwIfNeeded(
                jniDownload(getRawPointer(), refspecs, opts == null ? 0 : opts.getRawPointer()));
    }
    @Nonnull
    public Remote dup() {
        Remote out = new Remote(false, 0);
        Error.throwIfNeeded(jniDup(out._rawPtr, getRawPointer()));
        return out;
    }
    public void fetch(
            @Nullable String[] refspecs,
            @Nullable FetchOptions opts,
            @Nullable String reflogMessage) {
        Error.throwIfNeeded(
                jniFetch(
                        getRawPointer(),
                        refspecs,
                        opts == null ? 0 : opts.getRawPointer(),
                        reflogMessage));
    }
    public List<String> getFetchRefspecs() {
        List<String> out = new ArrayList<>();
        Error.throwIfNeeded(jniGetFetchRefspecs(out, getRawPointer()));
        return out;
    }
    public List<String> getPushRefspecs() {
        List<String> out = new ArrayList<>();
        Error.throwIfNeeded(jniGetPushRefspecs(out, getRawPointer()));
        return out;
    }
    @Nullable
    public Refspec getRefspec(int n) {
        long ptr = jniGetRefspec(getRawPointer(), n);
        if (ptr == 0) {
            return null;
        }
        return new Refspec(false, ptr);
    }
    public boolean isValidName(String remoteName) {
        return jniIsValidName(remoteName) == 1;
    }
    @Nullable
    public String name() {
        return jniName(getRawPointer());
    }
    @Nullable
    public Repository owner() {
        long ptr = jniOwner(getRawPointer());
        if (ptr == 0) {
            return null;
        }
        return new Repository(ptr);
    }
    public void prune(@Nullable Callbacks callbacks) {
        Error.throwIfNeeded(
                jniPrune(getRawPointer(), callbacks == null ? 0 : callbacks.getRawPointer()));
    }
    public int pruneRefs() {
        return jniPruneRefs(getRawPointer());
    }
    public void push(@Nonnull List<String> refspecs, @Nullable PushOptions opts) {
        Error.throwIfNeeded(
                jniPush(
                        getRawPointer(),
                        refspecs.toArray(new String[0]),
                        opts == null ? 0 : opts.getRawPointer()));
    }
    @Nullable
    public String pushurl() {
        return jniPushurl(getRawPointer());
    }
    public int refspecCount() {
        return jniRefspecCount(getRawPointer());
    }
    @CheckForNull
    public TransferProgress stats() {
        long ptr = jniStats(getRawPointer());
        if (ptr == 0) {
            return null;
        }
        return new TransferProgress(true, ptr);
    }
    public void stop() {
        jniStop(getRawPointer());
    }
    public void updateTips(
            @Nullable String reflogMessage,
            @Nullable Callbacks callbacks,
            boolean updateFetchhead,
            @Nonnull AutotagOptionT downloadTags) {
        Error.throwIfNeeded(
                jniUpdateTips(
                        getRawPointer(),
                        callbacks == null ? 0 : callbacks.getRawPointer(),
                        updateFetchhead ? 1 : 0,
                        downloadTags.getBit(),
                        reflogMessage));
    }
    public void updaload(@Nonnull List<String> refspecs, @Nullable PushOptions opts) {
        Error.throwIfNeeded(
                jniUpload(
                        getRawPointer(),
                        refspecs.toArray(new String[0]),
                        opts == null ? 0 : opts.getRawPointer()));
    }
    @Nonnull
    public String url() {
        String url = jniUrl(getRawPointer());
        return url==null ? "" : url;
    }
    public enum AutotagOptionT implements IBitEnum {
        UNSPECIFIED(0),
        AUTO(1),
        NONE(2),
        ALL(3);
        private final int _bit;
        AutotagOptionT(int bit) {
            this._bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum CompletionT implements IBitEnum {
        DOWNLOAD(0),
        INDEXING(1),
        ERROR(2);
        private final int _bit;
        CompletionT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum Direction {
        FETCH,
        PUSH;
    }
    public enum CreateFlags implements IBitEnum {
        SKIP_INSTEADOF(1 << 0),
        SKIP_DEFAULT_FETCHSPEC(1 << 1);
        private final int _bit;
        CreateFlags(int bit) {
            this._bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum RedirectT implements IBitEnum {
        NONE(1 << 0),  
        INITIAL(1 << 1),  
        ALL(1 << 2);  
        private final int _bit;
        RedirectT(int bit) {
            this._bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    @FunctionalInterface
    public interface CredAcquireCb {
        @Nullable
        Credential acquire(String url, String usernameFromUrl, int allowedTypes);
    }
    @FunctionalInterface
    public interface TransportMessageCb {
        int accept(String message);
    }
    @FunctionalInterface
    public interface CompletionCb {
        int accept(CompletionT completionT);
    }
    @FunctionalInterface
    public interface TransportCertificateCheckCb {
        int accept(Cert cert, boolean valid, String host); 
    }
    @FunctionalInterface
    public interface TransferProgressCb {
        int accept(TransferProgress stats);
    }
    @FunctionalInterface
    public interface UpdateTipsCb {
        int accept(@Nullable String refname, @Nullable Oid a, @Nullable Oid b);
    }
    @FunctionalInterface
    public interface PackProgressCb {
        int accept(int stage, long current, long total);
    }
    @FunctionalInterface
    public interface PushTransferProgressCb {
        int accept(long current, long total, int bytes);
    }
    @FunctionalInterface
    public interface PushUpdateReferenceCb {
        int accept(String refname, String status);
    }
    @FunctionalInterface
    public interface PushNegotiationCb {
        int accept(@Nonnull List<PushUpdate> updates);
    }
    @FunctionalInterface
    public interface TransportCb {
        @Nullable
        Transport accept(Remote owner);
    }
    @FunctionalInterface
    public interface UrlResolveCb {
        int accept(String urlResolved, String url, @Nonnull Direction direction);
    }
    public static class CreateOptions extends CAutoReleasable {
        public static final int VERSION = 1;
        protected CreateOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static CreateOptions create(int version) {
            CreateOptions opts = new CreateOptions(false, 0);
            Error.throwIfNeeded(jniCreateOptionsNew(opts._rawPtr, version));
            return opts;
        }
        @Nonnull
        public static CreateOptions createDefault() {
            return create(VERSION);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniCreateOptionsFree(cPtr);
        }
        public int getVersion() {
            return jniCreateOptionsGetVersion(getRawPointer());
        }
        public void setVersion(int version) {
            jniCreateOptionsSetVersion(getRawPointer(), version);
        }
        @Nullable
        public Repository getRepository() {
            long repoPtr = jniCreateOptionsGetRepository(getRawPointer());
            if (repoPtr <= 0) {
                return null;
            }
            return new Repository(repoPtr);
        }
        public void setRepository(@Nullable Repository repository) {
            jniCreateOptionsSetRepository(
                    getRawPointer(), repository == null ? 0 : repository.getRawPointer());
        }
        @Nullable
        public String getName() {
            return jniCreateOptionsGetName(getRawPointer());
        }
        public void setName(String name) {
            jniCreateOptionsSetName(getRawPointer(), name);
        }
        public String getFetchspec() {
            return jniCreateOptionsGetFetchspec(getRawPointer());
        }
        public void setFetchspec(String fetchspec) {
            jniCreateOptionsSetFetchspec(getRawPointer(), fetchspec);
        }
        public EnumSet<Flag> getFlags() {
            return IBitEnum.parse(jniCreateOptionsGetFlags(getRawPointer()), Flag.class);
        }
        public void setFlags(EnumSet<Flag> flags) {
            jniCreateOptionsSetFlags(getRawPointer(), IBitEnum.bitOrAll(flags));
        }
        public enum Flag implements IBitEnum {
            SKIP_INSTEADOF(1 << 0),
            SKIP_DEFAULT_FETCHSPEC(1 << 1);
            private final int _bit;
            Flag(int bit) {
                _bit = bit;
            }
            @Override
            public int getBit() {
                return _bit;
            }
        }
    }
    public static class PushUpdate extends CAutoReleasable {
        protected PushUpdate(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        public static PushUpdate create() {
            long ptr = jniPushUpdateNew();
            return new PushUpdate(true, ptr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniPushUpdateFree(cPtr);
        }
        public String getSrcRefname() {
            return jniPushUpdateGetSrcRefname(getRawPointer());
        }
        public void setSrcRefname(@Nonnull String srcRefname) {
            jniPushUpdateSetSrcRefname(getRawPointer(), srcRefname);
        }
        public String getDstRefname() {
            return jniPushUpdateGetDstRefname(getRawPointer());
        }
        public void setDstRefname(@Nonnull String dstRefname) {
            jniPushUpdateSetDstRefname(getRawPointer(), dstRefname);
        }
        @CheckForNull
        public Oid getSrc() {
            byte[] src = jniPushUpdateGetSrc(getRawPointer());
            return src == null ? null : Oid.of(src);
        }
        public void setSrc(@Nonnull Oid src) {
            jniPushUpdateSetSrc(getRawPointer(), src);
        }
        @CheckForNull
        public Oid getDst() {
            byte[] dst = jniPushUpdateGetDst(getRawPointer());
            return dst == null ? null : Oid.of(dst);
        }
        public void setDst(@Nonnull Oid dst) {
            jniPushUpdateSetDst(getRawPointer(), dst);
        }
    }
    public static final class Callbacks extends CAutoReleasable {
        public static final int VERSION = 1;
        private CredAcquireCb _credAcquireCb;
        private TransportMessageCb _transportMsg;
        private CompletionCb _completionCb;
        private TransportCertificateCheckCb _certificateCheckCb;
        private TransferProgressCb _transferProgressCb;
        private UpdateTipsCb _updateTipsCb;
        private PackProgressCb _packProgressCb;
        private PushTransferProgressCb _pushTransferProgressCb;
        private PushUpdateReferenceCb _pushUpdateReferenceCb;
        private PushNegotiationCb _pushNegotiationCb;
        private TransportCb _transportCb;
        private UrlResolveCb _urlResolveCb;
        protected Callbacks(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static Callbacks create(int version) {
            Callbacks cb = new Callbacks(false, 0);
            Error.throwIfNeeded(jniCallbacksNew(cb._rawPtr, version));
            return cb;
        }
        public static Callbacks createDefault() {
            return create(VERSION);
        }
        public void setCredAcquireCb(CredAcquireCb credAcquireCb) {
            jniCallbacksSetCallbackObject(getRawPointer(), this, CallbackType.CRED.ordinal());
            _credAcquireCb = credAcquireCb;
        }
        public void setTransportMsg(TransportMessageCb transportMsg) {
            jniCallbacksSetCallbackObject(
                    getRawPointer(), this, CallbackType.TRANSPORT_MSG.ordinal());
            _transportMsg = transportMsg;
        }
        public void setSidebandProgress(TransportMessageCb transportMsg) {
            setTransportMsg(transportMsg);
        }
        public void setCompletionCb(CompletionCb completion) {
            jniCallbacksSetCallbackObject(getRawPointer(), this, CallbackType.COMPLETION.ordinal());
            _completionCb = completion;
        }
        public void setCertificateCheckCb(TransportCertificateCheckCb certificateCheckCb) {
            jniCallbacksSetCallbackObject(
                    getRawPointer(), this, CallbackType.CERTIFICATE_CHECK.ordinal());
            _certificateCheckCb = certificateCheckCb;
        }
        public void setTransferProgressCb(TransferProgressCb transferProgressCb) {
            jniCallbacksSetCallbackObject(
                    getRawPointer(), this, CallbackType.TRANSFER_PROGRESS.ordinal());
            _transferProgressCb = transferProgressCb;
        }
        public void setUpdateTipsCb(UpdateTipsCb updateTipsCb) {
            jniCallbacksSetCallbackObject(getRawPointer(), this, CallbackType.UPDATE_TIP.ordinal());
            _updateTipsCb = updateTipsCb;
        }
        public void setPackProgressCb(PackProgressCb packProgressCb) {
            jniCallbacksSetCallbackObject(
                    getRawPointer(), this, CallbackType.PACK_PROGRESS.ordinal());
            _packProgressCb = packProgressCb;
        }
        public void setPushTransferProgressCb(PushTransferProgressCb pushTransferProgressCb) {
            jniCallbacksSetCallbackObject(
                    getRawPointer(), this, CallbackType.PUSH_TRANSFER_PROGRESS.ordinal());
            _pushTransferProgressCb = pushTransferProgressCb;
        }
        public void setPushUpdateReferenceCb(PushUpdateReferenceCb pushUpdateReferenceCb) {
            jniCallbacksSetCallbackObject(
                    getRawPointer(), this, CallbackType.PUSH_UPDATE_REFERENCE.ordinal());
            _pushUpdateReferenceCb = pushUpdateReferenceCb;
        }
        public void setPushNegotiationCb(PushNegotiationCb pushNegotiationCb) {
            jniCallbacksSetCallbackObject(
                    getRawPointer(), this, CallbackType.PUSH_NEGOTIATION.ordinal());
            _pushNegotiationCb = pushNegotiationCb;
        }
        public void setTransportCb(TransportCb transportCb) {
            jniCallbacksSetCallbackObject(
                    getRawPointer(), this, CallbackType.TRANSPORT.ordinal());
            _transportCb = transportCb;
        }
        public void setUrlResolveCbCb(UrlResolveCb urlResolveCbCb) {
            jniCallbacksSetCallbackObject(
                    getRawPointer(), this, CallbackType.URL_RESOLVE.ordinal());
            _urlResolveCb = urlResolveCbCb;
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniCallbacksFree(cPtr);
        }
        private long acquireCred(String url, String usernameFromUrl, int allowedTypes) {
            if (_credAcquireCb != null) {
                return Optional.ofNullable(
                                _credAcquireCb.acquire(url, usernameFromUrl, allowedTypes))
                        .map(CAutoReleasable::getRawPointer)
                        .orElse(0L);
            }
            return 0;
        }
        private int transportMessage(String message) {
            if (_transportMsg != null) {
                return _transportMsg.accept(message);
            }
            return 0;
        }
        private int complete(int type) {
            if (_completionCb != null) {
                CompletionT completionT =
                        type == 0
                                ? CompletionT.DOWNLOAD
                                : (type == 1 ? CompletionT.INDEXING : CompletionT.ERROR);
                return _completionCb.accept(completionT);
            }
            return 0;
        }
        private int transportMessageCheck(long certPtr, int valid, String host) {
            if (_certificateCheckCb != null) {
                Cert cert = certPtr == 0 ? null : new Cert(true, certPtr);
                return _certificateCheckCb.accept(cert, valid != 0, host);
            }
            return 0;
        }
        private int transferProgress(long progressPtr) {
            if (_transferProgressCb != null) {
                TransferProgress progress =
                        progressPtr == 0 ? null : new TransferProgress(true, progressPtr);
                return _transferProgressCb.accept(progress);
            }
            return 0;
        }
        private int updateTips(String refname, byte[] ida, byte[] idb) {
            if (_updateTipsCb != null) {
                Oid oida = ida == null ? null : Oid.of(ida);
                Oid oidb = idb == null ? null : Oid.of(idb);
                return _updateTipsCb.accept(refname, oida, oidb);
            }
            return 0;
        }
        private int packProgress(int stage, long current, long total) {
            if (_packProgressCb != null) {
                return _packProgressCb.accept(stage, current, total);
            }
            return 0;
        }
        private int pushTransferProgress(long current, long total, int bytes) {
            if (_pushTransferProgressCb != null) {
                return _pushTransferProgressCb.accept(current, total, bytes);
            }
            return 0;
        }
        private int pushUpdateReference(String refname, String status) {
            if (_pushUpdateReferenceCb != null) {
                return _pushUpdateReferenceCb.accept(refname, status);
            }
            return 0;
        }
        private int pushNegotiation(long[] updates) {
            if (_pushNegotiationCb != null) {
                if (updates == null) {
                    return _pushNegotiationCb.accept(Collections.emptyList());
                }
                return _pushNegotiationCb.accept(
                        Arrays.stream(updates)
                                .mapToObj(p -> new PushUpdate(true, p))
                                .collect(Collectors.toList()));
            }
            return 0;
        }
        private int resolveUrl(String resolvedUrl, String url, int direction) {
            if (_urlResolveCb != null) {
                return _urlResolveCb.accept(
                        resolvedUrl, url, direction == 0 ? Direction.FETCH : Direction.PUSH);
            }
            return 0;
        }
        private long transport(long ownerPtr) {
            if (_transportCb != null) {
                Remote remote = ownerPtr == 0 ? null : new Remote(true, ownerPtr);
                return Optional.ofNullable(_transportCb.accept(remote))
                        .map(CAutoReleasable::getRawPointer)
                        .orElse(0L);
            }
            return 0L;
        }
        private enum CallbackType {
            CRED,
            TRANSPORT_MSG,
            COMPLETION,
            CERTIFICATE_CHECK,
            TRANSFER_PROGRESS,
            UPDATE_TIP,
            PACK_PROGRESS,
            PUSH_TRANSFER_PROGRESS,
            PUSH_UPDATE_REFERENCE,
            PUSH_NEGOTIATION,
            TRANSPORT,
            URL_RESOLVE;
        }
    }
}
