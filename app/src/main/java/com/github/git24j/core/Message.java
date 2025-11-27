package com.github.git24j.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;

public class Message {
    static native int jniPrettify(Buf out, String message, int stripComments, char commentChar);
    static native void jniTrailerArrayFree(long arr);
    static native int jniTrailerArrayGetCount(long trailerArrayPtr);
    static native long jniTrailerArrayGetTrailer(long trailerArrayPtr, int idx);
    static native String jniTrailerArrayGetTrailerBlock(long trailerArrayPtr);
    static native String jniTrailerGetKey(long trailerPtr);
    static native String jniTrailerGetValue(long trailerPtr);
    static native int jniTrailers(AtomicLong outArr, String message);
    @Nonnull
    public static TrailerArray trailers(@Nonnull String message) {
        AtomicLong outArr = new AtomicLong();
        Error.throwIfNeeded(jniTrailers(outArr, message));
        long trailerArrPtr = outArr.get();
        int n = jniTrailerArrayGetCount(trailerArrPtr);
        List<Trailer> trailers = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            long trailerPtr = jniTrailerArrayGetTrailer(trailerArrPtr, i);
            String key = jniTrailerGetKey(trailerPtr);
            String val = jniTrailerGetValue(trailerPtr);
            trailers.add(new Trailer(key, val));
        }
        String trailerBlock = jniTrailerArrayGetTrailerBlock(trailerArrPtr);
        jniTrailerArrayFree(trailerArrPtr);
        return new TrailerArray(trailers, trailerBlock);
    }
    public static String prettify(
            @Nonnull String message, boolean stripComments, char commentChar) {
        Buf out = new Buf();
        int e = jniPrettify(out, message, stripComments ? 1 : 0, commentChar);
        Error.throwIfNeeded(e);
        return out.getString().orElse("");
    }
    public static class Trailer {
        private final String _key;
        private final String _value;
        public Trailer(String key, String value) {
            _key = key;
            _value = value;
        }
        public String getKey() {
            return _key;
        }
        public String getValue() {
            return _value;
        }
    }
    public static class TrailerArray {
        private final List<Trailer> _trailers;
        private final String _trailerBlock;
        public TrailerArray(List<Trailer> trailers, String trailerBlock) {
            _trailers = trailers;
            _trailerBlock = trailerBlock;
        }
        public List<Trailer> getTrailers() {
            return _trailers;
        }
        String getTrailerBlock() {
            return _trailerBlock;
        }
    }
}
