
package io.github.rosemoe.sora.lang.completion.snippet


data class PlaceholderDefinition @JvmOverloads constructor(
    var id: Int,
    var choices: List<String>? = null,
    var elements: List<PlaceHolderElement> = mutableListOf(), 
    var transform: Transform? = null
) {
    internal var text: String? = null
}
