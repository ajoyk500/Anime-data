package com.akcreation.gitsilent.utils


inline fun <T> List<T>.forEachIndexedBetter(
    foreach: (idx:Int, T)->Unit
) {
    for(idx in indices) {
        val value = getOrNull(idx) ?: continue
        foreach(idx, value)
    }
}
inline fun <T> List<T>.forEachBetter(
    foreach: (T)->Unit
) {
    for(idx in indices) {
        val value = getOrNull(idx) ?: continue
        foreach(value)
    }
}
inline fun <T> Array<T>.forEachBetter(
    foreach: (T)->Unit
) {
    for(idx in indices) {
        val value = getOrNull(idx) ?: continue
        foreach(value)
    }
}
inline fun <K, V> Map<K, V>.forEachBetter(
    foreach: (K, V)->Unit
) {
    for(key in keys) {
        val value = get(key) ?: continue
        foreach(key, value)
    }
}
fun <T, R> List<T>.filterAndMap(
    predicate:(T)->Boolean,
    transform:(T)->R,
):List<R> {
    val ret = mutableListOf<R>()
    forEachBetter {
        if(predicate(it)) {
            ret.add(transform(it))
        }
    }
    return ret
}
