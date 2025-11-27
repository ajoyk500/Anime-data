
package org.eclipse.tm4e.languageconfiguration.internal.model;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.internal.utils.StringUtils;

public class EnterAction {
	public enum IndentAction {
		None,
		Indent,
		IndentOutdent,
		Outdent;
		public static IndentAction get(final @Nullable String value) {
			if (value == null) {
				return IndentAction.None;
			}
			switch (value) {
				case "none":
					return IndentAction.None;
				case "indent":
					return IndentAction.Indent;
				case "indentOutdent":
					return IndentAction.IndentOutdent;
				case "outdent":
					return IndentAction.Outdent;
				default:
					return IndentAction.None;
			}
		}
	}
	public final IndentAction indentAction;
	public @Nullable String appendText;
	public final @Nullable Integer removeText;
	public EnterAction(final IndentAction indentAction) {
		this(indentAction, null, null);
	}
	public EnterAction(final IndentAction indentAction, final @Nullable String appendText, final @Nullable Integer removeText) {
		this.indentAction = indentAction;
		this.appendText = appendText;
		this.removeText = removeText;
	}
	@Override
	public String toString() {
		return StringUtils.toString(this, sb -> sb
				.append("indentAction=").append(indentAction).append(", ")
				.append("appendText=").append(appendText).append(", ")
				.append("removeText=").append(removeText));
	}
}
