
package org.eclipse.tm4e.core.internal.grammar.raw;

import org.eclipse.tm4e.core.internal.parser.PropertySettable;

public final class RawCaptures extends PropertySettable.HashMap<IRawRule> implements IRawCaptures {
	private static final long serialVersionUID = 1L;
	@Override
	public IRawRule getCapture(final String captureId) {
		return get(captureId);
	}
	@Override
	public Iterable<String> getCaptureIds() {
		return keySet();
	}
}
