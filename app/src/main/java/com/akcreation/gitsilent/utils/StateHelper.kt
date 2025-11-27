package com.akcreation.gitsilent.utils

import androidx.compose.runtime.MutableState
import com.akcreation.gitsilent.utils.cache.Cache

private val storage = Cache
enum class StateRequestType {
    invalid,
    forceReload,
    withRepoId,
    requireGoToTop,
    indexToWorkTree_CommitAll,
    headToIndex_CommitAll,
    jumpAfterImport,
    goToParentAndScrollToItem,
}
fun changeStateTriggerRefreshPage(needRefresh: MutableState<String>, requestType:StateRequestType = StateRequestType.invalid, data:Any? =null, newStateValue: String = getShortUUID()) {
    setStateWithRequestData(state = needRefresh, requestType=requestType, data = data, newStateValue = newStateValue)
}
fun setStateWithRequestData(state: MutableState<String>, requestType:StateRequestType = StateRequestType.invalid, data:Any? =null, newStateValue:String = getShortUUID()) {
    if(requestType != StateRequestType.invalid) {
        storage.set(newStateValue, Pair(requestType, data))
    }
    state.value = newStateValue
}
fun<T> getRequestDataByState(stateValue:String, getThenDel:Boolean = true):Pair<StateRequestType, T?> {
    if(stateValue.isBlank()) {
        return Pair(StateRequestType.invalid, null)
    }
    val requestTypeAndData = if(getThenDel) {
        storage.getByTypeThenDel<Pair<StateRequestType, T?>>(stateValue)
    } else {
        storage.getByType<Pair<StateRequestType, T?>>(stateValue)
    }
    return requestTypeAndData ?: Pair(StateRequestType.invalid, null)
}
fun delRequestDataByState(stateValue:String) {
    if(stateValue.isBlank()) {
        return
    }
    storage.del(stateValue)
}
