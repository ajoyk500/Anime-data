
package org.eclipse.tm4e.core.internal.oniguruma;

import org.joni.Region;

public final class OnigResult {
	private int indexInScanner;
	private final Region region;
	OnigResult(final Region region, final int indexInScanner) {
		this.region = region;
		this.indexInScanner = indexInScanner;
	}
	public int getIndex() {
		return indexInScanner;
	}
	void setIndex(final int index) {
		indexInScanner = index;
	}
	public int locationAt(final int index) {
		final int bytes = region.getBeg(index);
		return bytes > 0 ? bytes : 0;
	}
	public int count() {
		return region.getNumRegs();
	}
	public int lengthAt(final int index) {
		final int bytes = region.getEnd(index) - region.getBeg(index);
		return bytes > 0 ? bytes : 0;
	}
	@Override
	public String toString() {
		return "OnigResult [indexInScanner=" + indexInScanner + ", region=" + region + "]";
	}
}
