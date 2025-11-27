package com.akcreation.gitsilent.server

import kotlinx.serialization.Serializable

fun createSuccessResult(msg: String="", data: Map<String, String> = mapOf()): Result {
    return Result(Code.success, msg, data)
}
fun createErrResult(msg:String, data: Map<String, String> = mapOf()): Result {
    return Result(Code.err, msg, data)
}
@Serializable
data class Result(
    val code:Int = Code.success,  
    val msg:String = "",  
    val data:Map<String, String>,  
)
object Code {
    const val success = 0
    const val err=100
    const val err_RepoBusy = 101 
}
