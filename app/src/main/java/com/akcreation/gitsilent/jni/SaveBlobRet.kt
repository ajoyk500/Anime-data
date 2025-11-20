package com.akcreation.gitsilent.jni


data class SaveBlobRet (
    val code: SaveBlobRetCode = SaveBlobRetCode.SUCCESS,
    val savePath: String="",
)
