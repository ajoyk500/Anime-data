package com.github.git24j.core;


public class Error {
    @Deprecated
    static native void jniClear();
    static native GitException jniLast();
    @Deprecated
    static native void jniSetStr(int klass, String message);
    public static void throwIfNeeded(int error) {
        if (error < 0) {
            GitException e = jniLast();
            if (e != null) {
                e.setCode(error);
                throw e;
            }
        }
    }
}
