package com.akcreation.gitsilent.constants


enum class SingleSendHandleMethod(val code: String) {
    NEED_ASK("1"),
    EDIT("2"),
    IMPORT("3");
    companion object {
        fun fromCode(code: String): SingleSendHandleMethod? {
            return entries.find { it.code == code }
        }
    }
}
