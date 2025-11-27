
package io.github.rosemoe.sora.lang.completion

import io.github.rosemoe.sora.lang.completion.snippet.CodeSnippet

data class SnippetDescription(
    val selectedLength: Int,
    val snippet: CodeSnippet,
    val deleteSelected: Boolean = true
)
