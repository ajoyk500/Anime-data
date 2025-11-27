package com.akcreation.gitsilent.utils.cache


interface CacheStore { 
    fun getARandomKey():String
    fun setThenReturnKey(value: Any):String
    fun set(key:String, value:Any):Any?
    fun get(key:String):Any?
    fun getOrDefault(key: String, default: ()->Any): Any
    fun getOrPut(key: String, default: ()->Any): Any
    fun<T> getByType(key:String):T?
    fun<T:Any> getOrDefaultByType(key:String, default:()->T):T
    fun<T:Any> getOrPutByType(key:String, default:()->T):T
    fun<T> getByTypeThenDel(key:String):T?
    fun del(key:String):Any?
    fun getThenDel(key:String):Any?
    fun updateKey(oldKey:String, newKey:String, requireDelOldKey:Boolean)
    fun clear()
    fun clearByKeyPrefix(keyPrefix:String)
    fun clearByPredicate(predicate:(key:String)->Boolean)
}
