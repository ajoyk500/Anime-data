
package io.github.rosemoe.sora.text


fun Content.batchEdit(block: (Content) -> Unit): Content {
    this.beginBatchEdit()
    block(this)
    this.endBatchEdit()
    return this
}