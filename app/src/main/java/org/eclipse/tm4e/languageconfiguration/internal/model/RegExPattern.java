
package org.eclipse.tm4e.languageconfiguration.internal.model;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.TMException;
import org.eclipse.tm4e.core.internal.oniguruma.OnigRegExp;
import org.eclipse.tm4e.core.internal.oniguruma.OnigString;

public abstract class RegExPattern {
	private static final class JavaRegExPattern extends RegExPattern {
		final Pattern pattern;
		JavaRegExPattern(final String pattern, final @Nullable String flags) throws PatternSyntaxException {
			this.pattern = Pattern.compile(flags == null ? pattern : pattern + "(?" + flags + ")");
		}
		@Override
		public boolean matchesFully(final String text) {
			return pattern.matcher(text).matches();
		}
		@Override
		public boolean matchesPartially(final String text) {
			return pattern.matcher(text).find();
		}
		@Override
		public String pattern() {
			return pattern.pattern();
		}
	}
	private static final class OnigRegExPattern extends RegExPattern {
		final OnigRegExp regex;
		OnigRegExPattern(final String pattern, final @Nullable String flags) throws PatternSyntaxException {
			this.regex = new OnigRegExp(pattern, flags != null && flags.contains("i"));
		}
		@Override
		public boolean matchesFully(final String text) {
			final var result = regex.search(OnigString.of(text), 0);
			return result != null && result.count() == 1 && result.lengthAt(0) == text.length();
		}
		@Override
		public boolean matchesPartially(final String text) {
			return regex.search(OnigString.of(text), 0) != null;
		}
		@Override
		public String pattern() {
			return regex.pattern();
		}
	}
	public static RegExPattern of(final String pattern) {
		return of(pattern, null);
	}
	public static RegExPattern of(final String pattern, final @Nullable String flags) {
		try {
			return new JavaRegExPattern(pattern, flags);
		} catch (Exception ex) {
			return new OnigRegExPattern(pattern, flags);
		}
	}
	public static @Nullable RegExPattern ofNullable(final @Nullable String pattern) {
		return ofNullable(pattern, null);
	}
	public static @Nullable RegExPattern ofNullable(final @Nullable String pattern, final @Nullable String flags) {
		if (pattern != null) {
			try {
				return new JavaRegExPattern(pattern, flags);
			} catch (Exception ex) {
				try {
					return new OnigRegExPattern(pattern, flags);
				} catch (Exception ex1) {
					ex1.printStackTrace();
				}
			}
		}
		return null;
	}
	public abstract boolean matchesFully(String text);
	public abstract boolean matchesPartially(String text);
	public abstract String pattern();
	@Override
	public String toString() {
		return pattern();
	}
}
