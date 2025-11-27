
package org.eclipse.tm4e.core.internal.utils;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractListeners<LISTENER, EVENT> {
	private final Set<LISTENER> listeners = new CopyOnWriteArraySet<>();
    public boolean add(final LISTENER listener) {
        return listeners.add(listener);
    }
	public int count() {
		return listeners.size();
	}
	public void dispatchEvent(final EVENT e) {
		listeners.forEach(l -> dispatchEvent(e, l));
	}
	public abstract void dispatchEvent(EVENT e, LISTENER l);
	public boolean isEmpty() {
		return listeners.isEmpty();
	}
	public boolean isNotEmpty() {
		return !listeners.isEmpty();
	}
    public boolean remove(final LISTENER listener) {
        return listeners.remove(listener);
    }
	public void removeAll() {
		listeners.clear();
	}
}