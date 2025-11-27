
package io.github.rosemoe.sora.util


interface ShareableData<T> : Cloneable {
    fun retain()
    fun release()
    fun isMutable(): Boolean
    fun toMutable(): T
}