
package org.eclipse.tm4e.core.internal.parser;

import java.io.Reader;
import java.util.List;
import java.util.Map;

public interface TMParser {
	interface ObjectFactory<T extends PropertySettable<?>> {
		T createRoot();
		PropertySettable<?> createChild(PropertyPath path, final Class<?> sourceType);
	}
	interface PropertyPath extends Iterable<Object> {
		Object first();
		Object get(int index);
		Object last();
		int depth();
	}
	<T extends PropertySettable<?>> T parse(Reader source, ObjectFactory<T> factory) throws Exception;
}
