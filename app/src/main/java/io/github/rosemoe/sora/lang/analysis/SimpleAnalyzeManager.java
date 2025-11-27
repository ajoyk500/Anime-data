
package io.github.rosemoe.sora.lang.analysis;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;

public abstract class SimpleAnalyzeManager<V> implements AnalyzeManager {
    private final static String LOG_TAG = "SimpleAnalyzeManager";
    private static int sThreadId = 0;
    private final Object lock = new Object();
    private StyleReceiver receiver;
    private volatile ContentReference ref;
    private Bundle extraArguments;
    private volatile long newestRequestId;
    private AnalyzeThread thread;
    private V data;
    private synchronized static int nextThreadId() {
        sThreadId++;
        return sThreadId;
    }
    @Override
    public void setReceiver(@Nullable StyleReceiver receiver) {
        this.receiver = receiver;
    }
    @Override
    public void reset(@NonNull ContentReference content, @NonNull Bundle extraArguments, @Nullable StyleReceiver receiver) {
        this.ref = content;
        this.extraArguments = extraArguments;
        rerun(receiver);
    }
    @Override
    public void insert(@NonNull CharPosition start, @NonNull CharPosition end, @NonNull CharSequence insertedContent, @Nullable StyleReceiver receiver) {
        rerun(receiver);
    }
    @Override
    public void delete(@NonNull CharPosition start, @NonNull CharPosition end, @NonNull CharSequence deletedContent, @Nullable StyleReceiver receiver) {
        rerun(receiver);
    }
    @Override
    public synchronized void rerun(@Nullable StyleReceiver receiver) {
        newestRequestId++;
        if (thread == null || !thread.isAlive()) {
            Log.v(LOG_TAG, "Starting a new thread for analysis");
            thread = new AnalyzeThread();
            thread.setDaemon(true);
            thread.setName("SplAnalyzer-" + nextThreadId());
            thread.start();
        }
        synchronized (lock) {
            lock.notify();
        }
    }
    @Override
    public void destroy() {
        ref = null;
        extraArguments = null;
        newestRequestId = 0;
        data = null;
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        thread = null;
        receiver = null;
    }
    public Bundle getExtraArguments() {
        return extraArguments;
    }
    @Nullable
    public V getData() {
        return data;
    }
    protected abstract Styles analyze(StringBuilder text, Delegate<V> delegate);
    private class AnalyzeThread extends Thread {
        private final StringBuilder textContainer = new StringBuilder();
        @Override
        public void run() {
            Log.v(LOG_TAG, "Analyze thread started");
            try {
                while (!isInterrupted()) {
                    var text = ref;
                    if (text != null) {
                        var requestId = 0L;
                        Styles result = null;
                        V newData = null;
                        do {
                            text = ref;
                            if (text == null) {
                                break;
                            }
                            requestId = newestRequestId;
                            var delegate = new Delegate<V>(requestId);
                            textContainer.setLength(0);
                            textContainer.ensureCapacity(text.length());
                            for (int i = 0; i < text.getLineCount() && requestId == newestRequestId; i++) {
                                if (i != 0) {
                                    textContainer.append(text.getLineSeparator(i - 1));
                                }
                                text.appendLineTo(textContainer, i);
                            }
                            result = analyze(textContainer, delegate);
                            newData = delegate.data;
                        } while (requestId != newestRequestId);
                        final var receiver = SimpleAnalyzeManager.this.receiver;
                        if (receiver != null && result != null) {
                            receiver.setStyles(SimpleAnalyzeManager.this, result);
                        }
                        data = newData;
                    }
                    synchronized (lock) {
                        lock.wait();
                    }
                }
            } catch (InterruptedException e) {
                Log.v(LOG_TAG, "Thread is interrupted.");
            } catch (Exception e) {
                Log.e(LOG_TAG, "Unexpected exception is thrown in the thread.", e);
            }
        }
    }
    public final class Delegate<T> {
        private final long myRequestId;
        private T data;
        public Delegate(long requestId) {
            myRequestId = requestId;
        }
        public void setData(T value) {
            data = value;
        }
        public boolean isCancelled() {
            return myRequestId != newestRequestId;
        }
    }
}
