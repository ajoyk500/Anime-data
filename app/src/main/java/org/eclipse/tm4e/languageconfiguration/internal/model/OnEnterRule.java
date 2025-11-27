
package org.eclipse.tm4e.languageconfiguration.internal.model;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.TMException;
import org.eclipse.tm4e.core.internal.utils.StringUtils;

public final class OnEnterRule {
	public final RegExPattern beforeText;
	public final @Nullable RegExPattern afterText;
	public final @Nullable RegExPattern previousLineText;
	public final EnterAction action;
	public OnEnterRule(final RegExPattern beforeText, final @Nullable RegExPattern afterText, final @Nullable RegExPattern previousLineText,
			final EnterAction action) {
		this.beforeText = beforeText;
		this.afterText = afterText;
		this.previousLineText = previousLineText;
		this.action = action;
	}
	OnEnterRule(final String beforeText, final @Nullable String afterText, final @Nullable String previousLineText,
			final EnterAction action) {
		this.beforeText = RegExPattern.of(beforeText);
		this.afterText = afterText == null ? null : RegExPattern.of(afterText);
		this.previousLineText = previousLineText == null ? null : RegExPattern.of(previousLineText);
		this.action = action;
	}
	@Override
	public String toString() {
		return StringUtils.toString(this, sb -> sb
				.append("beforeText=").append(beforeText).append(", ")
				.append("afterText=").append(afterText).append(", ")
				.append("previousLineText=").append(previousLineText).append(", ")
				.append("action=").append(action));
	}
}
