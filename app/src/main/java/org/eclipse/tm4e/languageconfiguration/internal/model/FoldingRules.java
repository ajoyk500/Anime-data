
package org.eclipse.tm4e.languageconfiguration.internal.model;

import org.eclipse.tm4e.core.internal.utils.StringUtils;

public final class FoldingRules {
	public final boolean offSide;
	public final RegExPattern markersStart;
	public final RegExPattern markersEnd;
	public FoldingRules(final boolean offSide, final RegExPattern markersStart, final RegExPattern markersEnd) {
		this.offSide = offSide;
		this.markersStart = markersStart;
		this.markersEnd = markersEnd;
	}
	@Override
	public String toString() {
		return StringUtils.toString(this, sb -> sb
				.append("markersStart=").append(markersStart).append(", ")
				.append("markersEnd=").append(markersEnd).append(", ")
				.append("offSide=").append(offSide));
	}
}
