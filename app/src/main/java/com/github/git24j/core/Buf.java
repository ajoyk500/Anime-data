package com.github.git24j.core;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Buf {
    private byte[] ptr;
    private int reserved;
    private int size;  
    public byte[] getPtr() {
        return ptr;
    }
    public void setPtr(byte[] ptr) {
        this.ptr = ptr;
    }
    public int getReserved() {
        return reserved;
    }
    public void setReserved(int reserved) {
        this.reserved = reserved;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public Optional<String> getString() {
        if (size < 1 || ptr == null || ptr.length<1) {
            return Optional.empty();  
        }
        if(size > ptr.length) {
            size = ptr.length;
        }
        return Optional.of(new String(ptr, 0, size, StandardCharsets.UTF_8));
    }
    @Override
    public String toString() {
        return getString().orElse("");
    }
}
