package com.github.git24j.core;


public enum SortT implements IBitEnum {
    NONE(0),
    TOPOLOGICAL(1 << 0),
    TIME(1 << 1),
    REVERSE(1 << 2);
    private final int _bit;
    SortT(int bit) {
        _bit = bit;
    }
    @Override
    public int getBit() {
        return _bit;
    }
}
