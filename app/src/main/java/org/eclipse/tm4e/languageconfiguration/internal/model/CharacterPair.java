
package org.eclipse.tm4e.languageconfiguration.internal.model;

import androidx.annotation.NonNull;
import org.eclipse.tm4e.core.internal.utils.StringUtils;
import java.util.Objects;

public class CharacterPair {
	public final String open;
	public final String close;
	public CharacterPair(final String opening, final String closing) {
		this.open = opening;
		this.close = closing;
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CharacterPair that = (CharacterPair) o;
		return Objects.equals(open, that.open) && Objects.equals(close, that.close);
	}
	@Override
	public int hashCode() {
		return Objects.hash(open, close);
	}
	@NonNull
	@Override
	public String toString() {
		return StringUtils.toString(this, sb -> sb
				.append("open=").append(open).append(", ")
				.append("close=").append(close));
	}
}
