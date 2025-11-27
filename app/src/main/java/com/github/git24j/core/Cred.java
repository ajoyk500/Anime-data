package com.github.git24j.core;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicLong;

public class Cred extends CAutoReleasable {
    static native int jniDefaultNew(AtomicLong out);
    static native void jniFree(long cred);
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
    static native int jniUsernameNew(AtomicLong cred, String username);
    static native int jniUserpassPlaintextNew(AtomicLong out, String username, String password);
    protected Cred(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Nonnull
    public static Cred userpassPlaintextNew(@Nonnull String username, @Nonnull String password) {
        Cred cred = new Cred(false, 0);
        Error.throwIfNeeded(jniUserpassPlaintextNew(cred._rawPtr, username, password));
        return cred;
    }
    @Nonnull
    public static Cred sshKeyNew(
            @Nonnull String username,
            @Nonnull String publickey,
            @Nonnull String privatekey,
            @Nonnull String passphrase) {
        Cred cred = new Cred(false, 0);
        Error.throwIfNeeded(
                jniSshKeyNew(cred._rawPtr, username, publickey, privatekey, passphrase));
        return cred;
    }
    @Nonnull
    public static Cred sshKeyFromAgent(@Nonnull String username) {
        Cred cred = new Cred(false, 0);
        Error.throwIfNeeded(jniSshKeyFromAgent(cred._rawPtr, username));
        return cred;
    }
    @Nonnull
    public static Cred defaultNew() {
        Cred cred = new Cred(false, 0);
        Error.throwIfNeeded(jniDefaultNew(cred._rawPtr));
        return cred;
    }
    @Nonnull
    public static Cred usernameNew(@Nonnull String username) {
        Cred cred = new Cred(false, 0);
        Error.throwIfNeeded(jniUsernameNew(cred._rawPtr, username));
        return cred;
    }
    @Nonnull
    public static Cred sshKeyMemoryNew(
            @Nonnull String username,
            @Nonnull String publickey,
            @Nonnull String privatekey,
            @Nonnull String passphrase) {
        Cred cred = new Cred(false, 0);
        Error.throwIfNeeded(
                jniSshKeyMemoryNew(cred._rawPtr, username, publickey, privatekey, passphrase));
        return cred;
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
}
