
package org.eclipse.tm4e.languageconfiguration.internal.model;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.internal.utils.StringUtils;

public final class CompleteEnterAction extends EnterAction {
	public final String indentation;
	public CompleteEnterAction(IndentAction indentAction, @Nullable String appendText, @Nullable Integer removeText,
			final String indentation) {
		super(indentAction, appendText, removeText);
		this.indentation = indentation;
	}
	@Override
	public String toString() {
		return StringUtils.toString(this, sb -> sb
				.append("indentAction=").append(indentAction).append(", ")
				.append("appendText=").append(appendText).append(", ")
				.append("removeText=").append(removeText).append(", ")
				.append("indentation=").append(indentation));
	}
}
