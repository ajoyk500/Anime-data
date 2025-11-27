package com.akcreation.gitsilent.jni;

import android.util.Log;

public class LibLoader {
    private static final String TAG="LibLoader";
    static {
        Log.d(TAG, "loading c libs...");
        System.loadLibrary("crypto");
        System.loadLibrary("ssl");
        System.loadLibrary("ssh2");
        System.loadLibrary("git2");
        System.loadLibrary("puppygit");
        Log.d(TAG, "c libs loaded");
    }
    public static void load() {
        Log.d(TAG, "load() is a stub method");
    }
}
