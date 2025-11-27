
package org.eclipse.tm4e.core.internal.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

final class TMParserPropertyPath extends ArrayList<Object> implements TMParser.PropertyPath {
	private static final long serialVersionUID = 1L;
	@Override
	public Iterator<Object> iterator() {
		final var it = super.iterator();
		return new Iterator<>() {
			@Override
			public Object next() {
				return it.next();
			}
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	@Override
	public int depth() {
		return size();
	}
	@Override
	public Object first() {
		if (isEmpty())
			throw new NoSuchElementException();
		return get(0);
	}
	@Override
	public Object last() {
		if (isEmpty())
			throw new NoSuchElementException();
		return get(size() - 1);
	}
	Object removeLastElement() {
		return remove(size() - 1);
	}
	@Override
	public String toString() {
		return "/" + stream()
				.map(Object::toString)
				.collect(Collectors.joining("/"));
	}
}
