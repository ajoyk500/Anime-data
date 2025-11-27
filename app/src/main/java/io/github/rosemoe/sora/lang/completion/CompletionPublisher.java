
package io.github.rosemoe.sora.lang.completion;

import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import io.github.rosemoe.sora.annotations.UnsupportedUserUsage;
import io.github.rosemoe.sora.lang.Language;

public class CompletionPublisher {
    public final static int DEFAULT_UPDATE_THRESHOLD = 5;
    private final List<CompletionItem> items;
    private final List<CompletionItem> candidates;
    private final Lock lock;
    private final Handler handler;
    private final Runnable callback;
    private final int languageInterruptionLevel;
    private Comparator<CompletionItem> comparator;
    private int updateThreshold;
    private boolean invalid = false;
    public CompletionPublisher(@NonNull Handler handler, @NonNull Runnable callback, int languageInterruptionLevel) {
        this.handler = handler;
        this.items = new ArrayList<>();
        this.candidates = new ArrayList<>();
        lock = new ReentrantLock(true);
        updateThreshold = DEFAULT_UPDATE_THRESHOLD;
        this.callback = callback;
        this.languageInterruptionLevel = languageInterruptionLevel;
    }
    public boolean hasData() {
        return items.size() + candidates.size() > 0;
    }
    @UnsupportedUserUsage
    public List<CompletionItem> getItems() {
        return items;
    }
    public void setUpdateThreshold(int updateThreshold) {
        this.updateThreshold = updateThreshold;
    }
    public void setComparator(@Nullable Comparator<CompletionItem> comparator) {
        checkCancelled();
        if (invalid) {
            return;
        }
        this.comparator = comparator;
        if (!items.isEmpty() && comparator != null) {
            handler.post(() -> {
                if (invalid) {
                    return;
                }
                Collections.sort(items, comparator);
                callback.run();
            });
        }
    }
    public void addItems(Collection<CompletionItem> items) {
        checkCancelled();
        if (invalid) {
            return;
        }
        lock.lock();
        try {
            candidates.addAll(items);
        } finally {
            lock.unlock();
        }
        if (candidates.size() >= updateThreshold) {
            updateList();
        }
    }
    public void addItem(CompletionItem item) {
        checkCancelled();
        if (invalid) {
            return;
        }
        lock.lock();
        try {
            candidates.add(item);
        } finally {
            lock.unlock();
        }
        if (candidates.size() >= updateThreshold) {
            updateList();
        }
    }
    public void updateList() {
        updateList(false);
    }
    public void updateList(boolean forced) {
        if (invalid) {
            return;
        }
        handler.post(() -> {
            if (invalid) {
                callback.run();
                return;
            }
            var locked = false;
            if (forced) {
                lock.lock();
                locked = true;
            } else {
                locked = lock.tryLock();
            }
            if (locked) {
                try {
                    if (candidates.isEmpty()) {
                        callback.run();
                        return;
                    }
                    final var comparator = this.comparator;
                    if (comparator != null) {
                        while (!candidates.isEmpty()) {
                            var candidate = candidates.remove(0);
                            int left = 0, right = items.size();
                            var size = right;
                            while (left <= right) {
                                var mid = (left + right) / 2;
                                if (mid < 0 || mid >= size) {
                                    left = mid;
                                    break;
                                }
                                var cmp = comparator.compare(items.get(mid), candidate);
                                if (cmp < 0) {
                                    left = mid + 1;
                                } else if (cmp > 0) {
                                    right = mid - 1;
                                } else {
                                    left = mid;
                                    break;
                                }
                            }
                            left = Math.max(0, Math.min(size, left));
                            items.add(left, candidate);
                        }
                    } else {
                        items.addAll(candidates);
                        candidates.clear();
                    }
                    callback.run();
                } finally {
                    lock.unlock();
                }
            }
        });
    }
    public void cancel() {
        invalid = true;
    }
    public void checkCancelled() {
        if (Thread.interrupted() || invalid) {
            invalid = true;
            if (languageInterruptionLevel <= Language.INTERRUPTION_LEVEL_SLIGHT) {
                throw new CompletionCancelledException();
            }
        }
    }
}
