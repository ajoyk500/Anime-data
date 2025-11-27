package com.akcreation.gitsilent.utils.state

import androidx.compose.runtime.saveable.Saver
import com.akcreation.gitsilent.utils.cache.Cache

data class Holder<T>(var key: String, var data:T)
fun <T> getSaver():Saver<Holder<T>, String> {
    return Saver(
        save = { holder ->
            Cache.set(holder.key, holder)
            holder.key
        },
        restore = { key ->
            val holder = Cache.getByType<Holder<T>>(key)
            holder
        }
    )
}
fun <T : Any> getHolder(keyTag:String, keyName:String, data: T):Holder<T> {
    val holder = Holder<T>(key = genKey(keyTag, keyName), data = data)
    return holder
}
fun genKey(keyTag:String, keyName:String):String {
    return "$keyTag:$keyName"
}
