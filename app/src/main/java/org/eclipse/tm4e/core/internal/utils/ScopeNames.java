
package org.eclipse.tm4e.core.internal.utils;

import org.eclipse.jdt.annotation.Nullable;

public final class ScopeNames {
	public static final char CONTRIBUTOR_SEPARATOR = '@';
	public static @Nullable String getContributor(final String scopeName) {
		final int separatorAt = scopeName.indexOf(CONTRIBUTOR_SEPARATOR);
		if (separatorAt == -1) {
			return "";
		}
		return scopeName.substring(separatorAt + 1);
	}
	public static boolean hasContributor(final String scopeName) {
		return scopeName.indexOf(CONTRIBUTOR_SEPARATOR) > -1;
	}
	public static String withoutContributor(final String scopeName) {
		final int separatorAt = scopeName.indexOf(CONTRIBUTOR_SEPARATOR);
		if (separatorAt == -1) {
			return scopeName;
		}
		return scopeName.substring(0, separatorAt);
	}
	private ScopeNames() {
	}
}
