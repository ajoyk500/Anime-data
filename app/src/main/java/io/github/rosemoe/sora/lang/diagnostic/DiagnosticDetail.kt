
package io.github.rosemoe.sora.lang.diagnostic


data class DiagnosticDetail(
    val briefMessage: CharSequence,
    val detailedMessage: CharSequence? = null,
    val quickfixes: List<Quickfix>? = null,
    val extraData: Any? = null
)