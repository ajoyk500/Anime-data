
package org.eclipse.tm4e.languageconfiguration.internal.model;

import androidx.annotation.NonNull;
import java.util.List;
import org.eclipse.tm4e.core.internal.utils.StringUtils;

public final class AutoClosingPairConditional extends AutoClosingPair {
	public final List<String> notIn;
	public AutoClosingPairConditional(final String open, final String close, final List<String> notIn) {
		super(open, close);
		this.notIn = notIn;
	}
	@NonNull
	@Override
	public String toString() {
		return StringUtils.toString(this, sb -> sb
				.append("open=").append(open).append(", ")
				.append("close=").append(close).append(", ")
				.append("notIn=").append(notIn));
	}
}
