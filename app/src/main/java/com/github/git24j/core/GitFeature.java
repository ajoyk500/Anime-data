package com.github.git24j.core;

import java.util.EnumSet;

public enum GitFeature {
    THTREADS(1 << 0),
    HTTPS(1 << 1),
    SSH(1 << 2),
    NSEC(1 << 3);
    public final int code;
    GitFeature(int code) {
        this.code = code;
    }
    private static void addIfMask(EnumSet<GitFeature> set, int maskedCode, GitFeature feature) {
        if ((maskedCode & feature.code) != 0) {
            set.add(feature);
        }
    }
    static EnumSet<GitFeature> valuesOf(int maskedCode) {
        EnumSet<GitFeature> set = EnumSet.noneOf(GitFeature.class);
        addIfMask(set, maskedCode, THTREADS);
        addIfMask(set, maskedCode, HTTPS);
        addIfMask(set, maskedCode, SSH);
        addIfMask(set, maskedCode, NSEC);
        return set;
    }
}
