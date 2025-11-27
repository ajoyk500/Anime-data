
package org.eclipse.tm4e.languageconfiguration.internal.model;

import org.eclipse.tm4e.core.internal.utils.StringUtils;

public class IndentForEnter {
	public final String beforeEnter;
	public final String afterEnter;
	public IndentForEnter(final String beforeEnter, final String afterEnter) {
		this.beforeEnter = beforeEnter;
		this.afterEnter = afterEnter;
	}
	@Override
	public String toString() {
		return StringUtils.toString(this, sb -> sb
				.append("beforeEnter=").append(beforeEnter).append(", ")
				.append("afterEnter=").append(afterEnter));
	}
}
