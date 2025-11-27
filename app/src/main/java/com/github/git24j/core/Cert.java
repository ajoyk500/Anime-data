package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.EnumSet;

public class Cert extends CAutoReleasable {
    static native long jniHostkeyCreateEmptyForTesting();
    static native byte[] jniHostkeyGetHashMd5(long hostkeyPtr);
    static native byte[] jniHostkeyGetHashSha1(long hostkeyPtr);
    static native byte[] jniHostkeyGetHashSha256(long hostkeyPtr);
    static native long jniHostkeyGetParent(long hostkeyPtr);
    static native int jniHostkeyGetType(long hostkeyPtr);
    static native long jniX509GetParent(long x509Ptr);
    protected Cert(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Override
    protected void freeOnce(long cPtr) {
        Libgit2.jniShadowFree(cPtr);
    }
    public enum T {
        NONE,
        X509,
        HOSTKEY_LIBSSH2,
        STRARRAY,
    }
    public enum SshT implements IBitEnum {
        MD5(1 << 0),
        SHA1(1 << 1),
        SHA256(1 << 2);
        private final int _bit;
        SshT(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public static class HostKey extends CAutoReleasable {
        protected HostKey(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        static HostKey createEmpty() {
            return new HostKey(false, jniHostkeyCreateEmptyForTesting());
        }
        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }
        @CheckForNull
        public Cert getParent() {
            long ptr = jniHostkeyGetParent(getRawPointer());
            return ptr == 0 ? null : new Cert(true, ptr);
        }
        public EnumSet<SshT> getType() {
            return IBitEnum.parse(jniHostkeyGetType(getRawPointer()), SshT.class);
        }
        public byte[] getHashMd5() {
            return jniHostkeyGetHashMd5(getRawPointer());
        }
        public byte[] getHashSha1() {
            return jniHostkeyGetHashSha1(getRawPointer());
        }
        public byte[] getHashSha256() {
            return jniHostkeyGetHashSha256(getRawPointer());
        }
    }
    public static class X509 extends CAutoReleasable {
        protected X509(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }
        @CheckForNull
        public Cert getParent() {
            long ptr = jniX509GetParent(getRawPointer());
            return ptr == 0 ? null : new Cert(true, ptr);
        }
    }
}
