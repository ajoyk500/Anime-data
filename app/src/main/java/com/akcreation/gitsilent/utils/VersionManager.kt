package com.akcreation.gitsilent.utils

import java.io.File

fun readVersionFromFile(file: File):String{
    try{
        if(file.exists()) {
            return file.inputStream().bufferedReader().readLine().trim()
        }else {
            return ""
        }
    }catch (e:Exception) {
        return ""
    }
}
fun writeVersionToFile(file:File, version:String) {
    file.outputStream().writer().use {
        it.write(version)
    }
}
fun readIntVersionFromFile(file: File):Int{
    val v = readVersionFromFile(file)
    if(v.isEmpty()) {
        return -1
    }
    return try {
        v.toInt()
    }catch (_:Exception) {
        -1
    }
}
fun writeIntVersionToFile(file:File, version:Int) {
    writeVersionToFile(file, ""+version)
}
