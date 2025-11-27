
package org.eclipse.tm4e.languageconfiguration.internal.model;

import org.eclipse.jdt.annotation.Nullable;

public class IndentationRules {
	public final RegExPattern decreaseIndentPattern;
	public final RegExPattern increaseIndentPattern;
	public final @Nullable RegExPattern indentNextLinePattern;
	public final @Nullable RegExPattern unIndentedLinePattern;
	public IndentationRules(final RegExPattern decreaseIndentPattern, final RegExPattern increaseIndentPattern,
			final @Nullable RegExPattern indentNextLinePattern, final @Nullable RegExPattern unIndentedLinePattern) {
		this.decreaseIndentPattern = decreaseIndentPattern;
		this.increaseIndentPattern = increaseIndentPattern;
		this.indentNextLinePattern = indentNextLinePattern;
		this.unIndentedLinePattern = unIndentedLinePattern;
	}
}