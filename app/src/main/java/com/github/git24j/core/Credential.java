package com.github.git24j.core;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Credential extends CAutoReleasable {
    static native int jniDefaultNew(AtomicLong out);
    static native void jniFree(long cred);
    static native String jniGetUsername(long cred);
    static native int jniHasUsername(long cred);
    static native int jniSshKeyFromAgent(AtomicLong out, String username);
    static native int jniSshKeyMemoryNew(
            AtomicLong out,
            String username,
            String publickey,
            String privatekey,
            String passphrase);
    static native int jniSshKeyNew(
            AtomicLong out,
            String username,
            String publickey,
            String privatekey,
            String passphrase);
    static native int jniUsernameNew(AtomicLong out, String username);
    static native int jniUserpass(
            AtomicLong out, String url, String userFromUrl, int allowedTypes, long payloadPtr);
    static native int jniUserpassPlaintextNew(AtomicLong out, String username, String password);
    protected Credential(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Nonnull
    public static Credential userpassPlaintextNew(
            @Nonnull String username, @Nonnull String password) {
        Credential out = new Credential(true, 0);
        Error.throwIfNeeded(jniUserpassPlaintextNew(out._rawPtr, username, password));
        return out;
    }
    @Nonnull
    public static Credential defaultNew() {
        Credential out = new Credential(true, 0);
        Error.throwIfNeeded(jniDefaultNew(out._rawPtr));
        return out;
    }
    @Nonnull
    public static Credential usernameNew(@Nonnull String username) {
        Credential out = new Credential(true, 0);
        Error.throwIfNeeded(jniUsernameNew(out._rawPtr, username));
        return out;
    }
    @Nonnull
    public static Credential sshKeyNew(
            @Nonnull String username,
            @Nullable String publickey,
            @Nonnull String privateKey,
            @Nullable String passphrase) {
        Credential out = new Credential(true, 0);
        Error.throwIfNeeded(jniSshKeyNew(out._rawPtr, username, publickey, privateKey, passphrase));
        return out;
    }
    @Nonnull
    public static Credential sshKeyMemoryNew(
            @Nonnull String username,
            @Nullable String publickey,
            @Nonnull String privateKey,
            @Nullable String passphrase) {
        Credential out = new Credential(true, 0);
        Error.throwIfNeeded(
                jniSshKeyMemoryNew(out._rawPtr, username, publickey, privateKey, passphrase));
        return out;
    }
    @Nonnull
    public static Credential fromAgent(@Nonnull String username) {
        Credential out = new Credential(true, 0);
        Error.throwIfNeeded(jniSshKeyFromAgent(out._rawPtr, username));
        return out;
    }
    static int userpass(
            AtomicLong out,
            @Nonnull String url,
            @Nonnull String userFromUrl,
            EnumSet<Type> allowedTypes,
            long payloadPtr) {
        return jniUserpass(out, url, userFromUrl, IBitEnum.bitOrAll(allowedTypes), payloadPtr);
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public boolean hasUsername() {
        return jniHasUsername(getRawPointer()) == 1;
    }
    public String getUsername() {
        return jniGetUsername(getRawPointer());
    }
    public enum Type implements IBitEnum {
        USERPASS_PLAINTEXT(1 << 0),
        SSH_KEY(1 << 1),
        SSH_CUSTOM(1 << 2),
        DEFAULT(1 << 3),
        SSH_INTERACTIVE(1 << 4),
        USERNAME(1 << 5),
        SSH_MEMORY(1 << 6),
        ;
        private final int _bit;
        Type(int bit) {
            _bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    @FunctionalInterface
    public interface AcquireCb {
        @Nonnull
        Credential accept(@Nonnull String url, @Nullable String usernameFromUrl, int allowedTypes)
                throws GitException;
    }
}
