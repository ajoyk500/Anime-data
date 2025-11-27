
package org.eclipse.tm4e.languageconfiguration.internal.model;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.internal.utils.StringUtils;

public final class CommentRule {
	@Nullable
	public final String lineComment;
	@Nullable
	public final CharacterPair blockComment;
	public CommentRule(@Nullable final String lineComment, @Nullable final CharacterPair blockComment) {
		this.lineComment = lineComment;
		this.blockComment = blockComment;
	}
	@Override
	public String toString() {
		return StringUtils.toString(this, sb -> sb
				.append("lineComment=").append(lineComment).append(", ")
				.append("blockComment=").append(blockComment));
	}
}
