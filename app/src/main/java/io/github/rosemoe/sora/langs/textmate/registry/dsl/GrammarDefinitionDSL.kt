
package io.github.rosemoe.sora.langs.textmate.registry.dsl

import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.DefaultGrammarDefinition
import io.github.rosemoe.sora.langs.textmate.registry.model.GrammarDefinition
import org.eclipse.tm4e.core.registry.IGrammarSource
import java.nio.charset.Charset

fun languages(block: LanguageDefinitionListBuilder.() -> Unit): LanguageDefinitionListBuilder {
    return LanguageDefinitionListBuilder().also(block)
}
class LanguageDefinitionListBuilder {
    private val allBuilder = mutableListOf<LanguageDefinitionBuilder>()
    fun language(name: String, block: LanguageDefinitionBuilder.() -> Unit) {
        allBuilder.add(LanguageDefinitionBuilder(name).also(block))
    }
    fun build(): List<GrammarDefinition> =
        allBuilder.map {
            val grammarSource = IGrammarSource.fromInputStream(
                FileProviderRegistry.getInstance().tryGetInputStream(it.grammar),
                it.grammar, Charset.defaultCharset()
            )
            DefaultGrammarDefinition.withLanguageConfiguration(
                grammarSource,
                it.languageConfiguration,
                it.name,
                it.scopeName
            ).withEmbeddedLanguages(it.embeddedLanguages)
        }
}
class LanguageDefinitionBuilder(var name: String) {
    lateinit var grammar: String
    var scopeName: String? = null
    var languageConfiguration: String? = null
    var embeddedLanguages: MutableMap<String, String>? = null
    fun defaultScopeName(prefix: String = "source") {
        scopeName = "$prefix.$name"
    }
    fun embeddedLanguages(block: LanguageEmbeddedLanguagesDefinitionBuilder.() -> Unit) {
        embeddedLanguages = embeddedLanguages ?: mutableMapOf<String, String>()
        LanguageEmbeddedLanguagesDefinitionBuilder(checkNotNull(embeddedLanguages)).also(block)
    }
    fun embeddedLanguage(scopeName: String, languageName: String) {
        embeddedLanguages = embeddedLanguages ?: mutableMapOf<String, String>()
        embeddedLanguages?.put(scopeName, languageName)
    }
}
class LanguageEmbeddedLanguagesDefinitionBuilder(private val map: MutableMap<String, String>) {
    infix fun String.to(languageName: String) {
        map[this] = languageName
    }
}
