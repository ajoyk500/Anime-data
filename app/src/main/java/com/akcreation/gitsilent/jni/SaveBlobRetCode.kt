package com.akcreation.gitsilent.jni


enum class SaveBlobRetCode(val code:Int) {
    SUCCESS(0),
    ERR_CAST_SAVE_PATH_TO_C_STR_FAILED(-1),
    ERR_RESOLVE_TREE_FAILED(-2),
    ERR_RESOLVE_ENTRY_FAILED(-3),
    ERR_RESOLVE_BLOB_FAILED(-4),
    ;
    companion object {
        fun fromCode(code: Int): SaveBlobRetCode? {
            return SaveBlobRetCode.entries.find { it.code == code }
        }
    }
}
