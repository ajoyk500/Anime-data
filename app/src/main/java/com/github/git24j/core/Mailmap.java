package com.github.git24j.core;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Mailmap {
    static native int jniAddEntry(
            long mmPtr, String realName, String realEmail, String replaceName, String replaceEmail);
    static native void jniFree(long rawPtr);
    static native int jniFromBuffer(AtomicLong outPtr, String buf);
    static native int jniFromRepository(AtomicLong outPtr, long repoPtr);
    static native int jniResolve(
            AtomicReference<String> outRealName,
            AtomicReference<String> outRealEmail,
            long mmPtr,
            String name,
            String email);
    static native int jniResolveSignature(AtomicLong out, long mm, long sig);
    private final AtomicLong _rawPtr = new AtomicLong();
    public Mailmap(long ptr) {
        _rawPtr.set(ptr);
    }
    public static Mailmap fromBuffer(String buf) {
        Mailmap mm = new Mailmap(0);
        jniFromBuffer(mm._rawPtr, buf);
        return mm;
    }
    public static Mailmap fromRepository(Repository repo) {
        AtomicLong outPtr = new AtomicLong();
        Error.throwIfNeeded(jniFromRepository(outPtr, repo.getRawPointer()));
        return new Mailmap(outPtr.get());
    }
    @Override
    protected void finalize() throws Throwable {
        if (_rawPtr.get() != 0) {
            jniFree(_rawPtr.getAndSet(0));
        }
        super.finalize();
    }
    long getRawPointer() {
        return _rawPtr.get();
    }
    public void addEntry(
            String realName, String realEmail, String replaceName, String replaceEmail) {
        Error.throwIfNeeded(
                jniAddEntry(getRawPointer(), realName, realEmail, replaceName, replaceEmail));
    }
    public void resolve(
            AtomicReference<String> outRealName,
            AtomicReference<String> outRealEmail,
            String name,
            String email) {
        Error.throwIfNeeded(jniResolve(outRealName, outRealEmail, getRawPointer(), name, email));
    }
    public Map.Entry<String, String> resolve(String name, String email) {
        AtomicReference<String> outRealName = new AtomicReference<>();
        AtomicReference<String> outRealEmail = new AtomicReference<>();
        resolve(outRealName, outRealEmail, name, email);
        return new AbstractMap.SimpleImmutableEntry<>(outRealName.get(), outRealEmail.get());
    }
    @Nullable
    public Signature resolveSignature(@Nonnull Signature sig) {
        Signature outSig = new Signature(false, 0);
        Error.throwIfNeeded(
                jniResolveSignature(outSig._rawPtr, getRawPointer(), sig.getRawPointer()));
        return outSig.isNull() ? null : outSig;
    }
}
