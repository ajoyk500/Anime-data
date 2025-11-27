
package io.github.rosemoe.sora.text;

import android.text.GetChars;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;
import io.github.rosemoe.sora.annotations.UnsupportedUserUsage;
import io.github.rosemoe.sora.text.bidi.BidiRequirementChecker;
import io.github.rosemoe.sora.text.bidi.TextBidi;
import io.github.rosemoe.sora.util.ShareableData;

public class ContentLine implements CharSequence, GetChars, BidiRequirementChecker, ShareableData<ContentLine> {
    private char[] value;
    private int length;
    private int rtlAffectingCount;
    private LineSeparator lineSeparator;
    private AtomicInteger refCount;
    public ContentLine() {
        this(true);
    }
    public ContentLine(@Nullable CharSequence text) {
        this(true);
        insert(0, text);
    }
    public ContentLine(@NonNull ContentLine src) {
        this(src.length + 16);
        length = src.length;
        rtlAffectingCount = src.rtlAffectingCount;
        lineSeparator = src.lineSeparator;
        System.arraycopy(src.value, 0, value, 0, length);
    }
    public ContentLine(int size) {
        length = 0;
        value = new char[size];
    }
    private ContentLine(boolean initialize) {
        if (initialize) {
            length = 0;
            value = new char[32];
        }
    }
    private void checkIndex(int index) {
        if (index < 0 || index > length) {
            throw new StringIndexOutOfBoundsException("index = " + index + ", length = " + length);
        }
    }
    private void ensureCapacity(int capacity) {
        if (value.length < capacity) {
            int newLength = value.length * 2 < capacity ? capacity + 2 : value.length * 2;
            char[] newValue = new char[newLength];
            System.arraycopy(value, 0, newValue, 0, length);
            value = newValue;
        }
    }
    @NonNull
    public ContentLine insert(int dstOffset, @Nullable CharSequence s) {
        if (s == null)
            s = "null";
        return this.insert(dstOffset, s, 0, s.length());
    }
    @NonNull
    public ContentLine insert(int dstOffset, @Nullable CharSequence s,
                              int start, int end) {
        if (s == null)
            s = "null";
        if ((dstOffset < 0) || (dstOffset > this.length()))
            throw new IndexOutOfBoundsException("dstOffset " + dstOffset);
        if ((start < 0) || (end < 0) || (start > end) || (end > s.length()))
            throw new IndexOutOfBoundsException(
                    "start " + start + ", end " + end + ", s.length() "
                            + s.length());
        int len = end - start;
        ensureCapacity(length + len);
        System.arraycopy(value, dstOffset, value, dstOffset + len,
                length - dstOffset);
        for (int i = start; i < end; i++) {
            var ch = s.charAt(i);
            value[dstOffset++] = ch;
            if (TextBidi.couldAffectRtl(ch)) {
                rtlAffectingCount++;
            }
        }
        length += len;
        return this;
    }
    @NonNull
    public ContentLine insert(int offset, char c) {
        ensureCapacity(length + 1);
        if (offset < length) {
            System.arraycopy(value, offset, value, offset + 1, length - offset);
        }
        if (TextBidi.couldAffectRtl(c)) {
            rtlAffectingCount++;
        }
        value[offset] = c;
        length += 1;
        return this;
    }
    @NonNull
    public ContentLine delete(int start, int end) {
        if (start < 0)
            throw new StringIndexOutOfBoundsException(start);
        if (end > length)
            end = length;
        if (start > end)
            throw new StringIndexOutOfBoundsException();
        int len = end - start;
        if (len > 0) {
            for (int i = start; i < end; i++) {
                if (TextBidi.couldAffectRtl(value[i])) {
                    rtlAffectingCount--;
                }
            }
            System.arraycopy(value, start + len, value, start, length - end);
            length -= len;
        }
        return this;
    }
    public boolean mayNeedBidi() {
        return rtlAffectingCount > 0;
    }
    @NonNull
    public ContentLine append(CharSequence text) {
        return this.insert(length, text);
    }
    @Override
    public int length() {
        return length;
    }
    @Override
    @UnsupportedUserUsage
    public char charAt(int index) {
        if (index >= length) {
            var separator = getLineSeparator();
            return separator.getLength() > 0 ? getLineSeparator().getContent().charAt(index - length) : '\n';
        }
        return value[index];
    }
    @Override
    @NonNull
    public ContentLine subSequence(int start, int end) {
        checkIndex(start);
        checkIndex(end);
        if (end < start) {
            throw new StringIndexOutOfBoundsException("start is greater than end");
        }
        char[] newValue = new char[end - start + 16];
        System.arraycopy(value, start, newValue, 0, end - start);
        var res = new ContentLine(false);
        res.value = newValue;
        res.length = end - start;
        if (rtlAffectingCount > 0) {
            for (int i = 0; i < res.length; i++) {
                if (TextBidi.couldAffectRtl(newValue[i])) {
                    res.rtlAffectingCount++;
                }
            }
        }
        return res;
    }
    public void appendTo(@NonNull StringBuilder sb) {
        sb.append(value, 0, length);
    }
    @Override
    @NonNull
    public String toString() {
        return new String(value, 0, length);
    }
    @NonNull
    public String toStringWithNewline() {
        if (value.length == length) {
            ensureCapacity(length + 1);
        }
        value[length] = '\n';
        return new String(value, 0, length + 1);
    }
    @NonNull
    public char[] getBackingCharArray() {
        return value;
    }
    public void getChars(int srcBegin, int srcEnd, @NonNull char[] dst, int dstBegin) {
        if (srcBegin < 0)
            throw new StringIndexOutOfBoundsException(srcBegin);
        if ((srcEnd < 0) || (srcEnd > length))
            throw new StringIndexOutOfBoundsException(srcEnd);
        if (srcBegin > srcEnd)
            throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
        System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }
    public void setLineSeparator(@Nullable LineSeparator separator) {
        this.lineSeparator = separator;
    }
    @NonNull
    public LineSeparator getLineSeparator() {
        if (lineSeparator == null) {
            return LineSeparator.NONE;
        }
        return lineSeparator;
    }
    @NonNull
    public ContentLine copy() {
        var clone = new ContentLine(false);
        clone.length = length;
        clone.value = new char[value.length];
        System.arraycopy(value, 0, clone.value, 0, length);
        clone.rtlAffectingCount = rtlAffectingCount;
        clone.lineSeparator = lineSeparator;
        return clone;
    }
    @Override
    public void retain() {
        if (refCount == null) {
            refCount = new AtomicInteger(2);
            return;
        }
        refCount.incrementAndGet();
    }
    @Override
    public void release() {
        if (refCount == null) {
            return;
        }
        int count = refCount.decrementAndGet();
        if (count < 0) {
            throw new IllegalStateException("illegal operation. There is no active owner");
        }
    }
    @Override
    public boolean isMutable() {
        return refCount == null || refCount.get() == 1;
    }
    @Override
    public ContentLine toMutable() {
        if (isMutable()) {
            return this;
        }
        return copy();
    }
}
