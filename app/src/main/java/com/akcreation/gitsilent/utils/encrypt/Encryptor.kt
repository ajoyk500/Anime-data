package com.akcreation.gitsilent.utils.encrypt


interface Encryptor {
    fun encrypt(raw:String, key:String):String
    fun decrypt(encrypted:String, key:String):String
}
