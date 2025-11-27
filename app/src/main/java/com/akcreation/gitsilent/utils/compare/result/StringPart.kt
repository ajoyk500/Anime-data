package com.akcreation.gitsilent.utils.compare.result


class StringPart (
    val str:String,
    val modified:Boolean
) {
    override fun toString(): String {
        return "StringPart(str='$str', modified=$modified)"
    }
}
