package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

public abstract class CAutoCloseable implements AutoCloseable {
    protected final AtomicLong _rawPtr = new AtomicLong();
    protected CAutoCloseable(long rawPointer) {
        _rawPtr.set(rawPointer);
    }
    long getRawPointer() {
        long ptr = _rawPtr.get();
        if (ptr == 0) {
            throw new IllegalStateException(
                    "Object has invalid memory address, likely it has been closed.");
        }
        return ptr;
    }
    protected abstract void releaseOnce(long cPtr);
    protected boolean isNull() {
        return _rawPtr.get() == 0;
    }
    @Override
    public void close() {
        if (_rawPtr.get() != 0) {
            releaseOnce(_rawPtr.getAndSet(0));
        }
    }
}
