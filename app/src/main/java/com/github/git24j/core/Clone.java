package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Clone {
    static native int jniClone(AtomicLong out, String url, String localPath, long options);
    static native void jniOptionsFree(long optionsPtr);
    static native int jniOptionsGetBare(long optionsPtr);
    static native String jniOptionsGetCheckoutBranch(long optionsPtr);
    static native void jniOptionsSetCheckoutBranch(long optionsPtr, String branch);
    static native long jniOptionsGetCheckoutOpts(long optionsPtr);
    static native long jniOptionsGetFetchOpts(long optionsPtr);
    static native int jniOptionsGetLocal(long optionsPtr);
    static native int jniOptionsGetVersion(long optionsPtr);
    static native int jniOptionsNew(int version, AtomicLong outOpts);
    static native void jniOptionsSetBare(long optionsPtr, int bare);
    static native void jniOptionsSetLocal(long optionsPtr, int local);
    static native void jniOptionsSetRemoteCb(long optionsPtr, Internals.ALSSCallback remoteCb);
    static native void jniOptionsSetRepositoryCb(
            long optionsPtr, Internals.ASICallback repositoryCb);
    static native void jniOptionsSetVersion(long optionsPtr, int version);
    @Nonnull
    public static Repository cloneRepo(
            @Nonnull String url, @Nonnull String localPath, @Nullable Options options) {
        Repository outRepo = new Repository(0);
        int e = jniClone(outRepo._rawPtr, url, localPath, options == null ? 0 : options.getRawPointer());
        Error.throwIfNeeded(e);
        return outRepo;
    }
    public enum LocalT implements IBitEnum {
        LOCAL_AUTO(0),
        LOCAL(1),
        NO_LOCAL(2),
        LOCAL_NO_LINKS(3);
        private final int _bit;
        LocalT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    @FunctionalInterface
    public interface RepositoryCreateCb {
        @Nonnull
        Repository accept(@Nonnull String path, boolean bare) throws GitException;
    }
    @FunctionalInterface
    public interface RemoteCreateCb {
        @Nonnull
        Remote accept(@Nonnull Repository repo, @Nonnull String name, @Nonnull String url)
                throws GitException;
    }
    public static class Options extends CAutoReleasable {
        public static int VERSION = 1;
        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static Options defaultOpts() {
            return create(VERSION);
        }
        @Nonnull
        public static Options create(int version) {
            Options opts = new Options(false, 0);
            jniOptionsNew(version, opts._rawPtr);
            return opts;
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }
        public int getVersion() {
            return jniOptionsGetVersion(getRawPointer());
        }
        public void setVersion(int version) {
            jniOptionsSetVersion(getRawPointer(), version);
        }
        @Nonnull
        public Checkout.Options getCheckoutOpts() {
            return new Checkout.Options(true, jniOptionsGetCheckoutOpts(getRawPointer()));
        }
        @Nonnull
        public FetchOptions getFetchOpts() {
            return new FetchOptions(true, jniOptionsGetFetchOpts(getRawPointer()));
        }
        public boolean getBare() {
            return jniOptionsGetBare(getRawPointer()) == 1;
        }
        public void setBare(boolean bare) {
            jniOptionsSetBare(getRawPointer(), bare ? 1 : 0);
        }
        @Nonnull
        public LocalT getLocal() {
            int r = jniOptionsGetLocal(getRawPointer());
            return IBitEnum.valueOf(r, LocalT.class, LocalT.LOCAL_AUTO);
        }
        public void setLocal(LocalT local) {
            jniOptionsSetLocal(getRawPointer(), local.getBit());
        }
        @CheckForNull
        public String getCheckoutBranch() {
            return jniOptionsGetCheckoutBranch(getRawPointer());
        }
        public void setCheckoutBranch(String branch) {
            jniOptionsSetCheckoutBranch(getRawPointer(), branch);
        }
        public void setRepositoryCreateCb(@Nonnull RepositoryCreateCb createCb) {
            jniOptionsSetRepositoryCb(
                    getRawPointer(),
                    ((out, str, i) -> {
                        try {
                            Repository repo = createCb.accept(str, i == 1);
                            out.set(repo._rawPtr.getAndSet(0));
                        } catch (GitException e) {
                            return e.getCode().getCode();
                        }
                        return 0;
                    }));
        }
        public void setRemoteCreateCb(@Nonnull RemoteCreateCb createCb) {
            jniOptionsSetRemoteCb(
                    getRawPointer(),
                    (out, repoPtr, name, url) -> {
                        try {
                            Remote remote = createCb.accept(new Repository(repoPtr), name, url);
                            out.set(remote._rawPtr.getAndSet(0));
                        } catch (GitException e) {
                            return e.getCode().getCode();
                        }
                        return 0;
                    });
        }
    }
}
