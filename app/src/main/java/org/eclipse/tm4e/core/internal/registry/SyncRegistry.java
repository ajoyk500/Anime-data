
package org.eclipse.tm4e.core.internal.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.grammar.IGrammar;
import org.eclipse.tm4e.core.internal.grammar.BalancedBracketSelectors;
import org.eclipse.tm4e.core.internal.grammar.Grammar;
import org.eclipse.tm4e.core.internal.grammar.ScopeStack;
import org.eclipse.tm4e.core.internal.grammar.raw.IRawGrammar;
import org.eclipse.tm4e.core.internal.theme.StyleAttributes;
import org.eclipse.tm4e.core.internal.theme.Theme;
import org.eclipse.tm4e.core.internal.utils.ScopeNames;

public class SyncRegistry implements IGrammarRepository, IThemeProvider {
	private final Map<String, Grammar> _grammars = new HashMap<>();
	private final Map<String, @Nullable IRawGrammar> _rawGrammars = new HashMap<>();
	private final Map<String, Collection<String>> _injectionGrammars = new HashMap<>();
	private Theme _theme;
	public SyncRegistry(final Theme theme) {
		this._theme = theme;
	}
	public void setTheme(final Theme theme) {
		this._theme = theme;
	}
	public List<String> getColorMap() {
		return this._theme.getColorMap();
	}
	public void addGrammar(final IRawGrammar grammar, @Nullable final Collection<String> injectionScopeNames) {
		this._rawGrammars.put(grammar.getScopeName(), grammar);
		if (injectionScopeNames != null) {
			this._injectionGrammars.put(grammar.getScopeName(), injectionScopeNames);
		}
	}
	@Override
	public @Nullable IRawGrammar lookup(final String scopeName) {
		IRawGrammar grammar = this._rawGrammars.get(scopeName);
		if (grammar == null) {
			final var scopeNameWithoutContributor = ScopeNames.withoutContributor(scopeName);
			if (!scopeNameWithoutContributor.equals(scopeName))
				grammar = this._rawGrammars.get(scopeNameWithoutContributor);
		}
		return grammar;
	}
	@Override
	@Nullable
	public Collection<String> injections(final String targetScope) {
		return this._injectionGrammars.get(targetScope);
	}
	@Override
	public StyleAttributes getDefaults() {
		return this._theme.getDefaults();
	}
	@Nullable
	@Override
	public StyleAttributes themeMatch(final ScopeStack scopePath) {
		return this._theme.match(scopePath);
	}
	@Nullable
	public IGrammar grammarForScopeName(
			final String scopeName,
			final int initialLanguage,
			@Nullable final Map<String, Integer> embeddedLanguages,
			@Nullable final Map<String, Integer> tokenTypes,
			@Nullable final BalancedBracketSelectors balancedBracketSelectors) {
		if (!this._grammars.containsKey(scopeName)) {
			final var rawGrammar = lookup(scopeName);
			if (rawGrammar == null) {
				return null;
			}
			this._grammars.put(scopeName, new Grammar(
					scopeName,
					rawGrammar,
					initialLanguage,
					embeddedLanguages,
					tokenTypes,
					balancedBracketSelectors,
					this,
					this));
		}
		return this._grammars.get(scopeName);
	}
}
