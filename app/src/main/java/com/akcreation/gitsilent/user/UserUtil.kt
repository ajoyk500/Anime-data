package com.akcreation.gitsilent.user

import androidx.compose.runtime.MutableState
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.getOrElse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean

object UserUtil {
    private val TAG = "UserUtil"
    private var user = User()
    val updateUserLock = Mutex()
    private const val channelBufferSize = 50
    private val updateUserChannel = Channel<User>(capacity = channelBufferSize)
    private val updateUserJobStarted = AtomicBoolean(false)
    fun init() {
        if(!updateUserJobStarted.get()){
            updateUserJobStarted.compareAndSet(false, true)
            startUpdateUserJob()
        }
    }
    private fun startUpdateUserJob() {
        val logTag = "updateUserJob"
        doJobThenOffLoading {
            var errCountLimit = 3
            while (errCountLimit > 0) {
                try {
                    updateUserLock.withLock {
                        var newUser = updateUserChannel.receive()
                        var count = 0
                        while (count++ < channelBufferSize) {
                            val result = updateUserChannel.tryReceive()
                            if (result.isSuccess) {
                                newUser = result.getOrElse { newUser }
                            } else {  
                                break
                            }
                        }
                        user.isProState.value = newUser.isProState.value
                    }
                } catch (e: Exception) {
                    errCountLimit--
                    MyLog.e(TAG, "$logTag: update user err: ${e.stackTraceToString()}")
                }
            }
            updateUserChannel.close()
        }
    }
    fun getUserSnapshot(u:User= user):User {
        return u.copy()
    }
    fun updateUser(requireReturnChangedUser:Boolean = false, needUpdateUserByLock:Boolean=true, modifyUser:(User)->Unit):User? {
        val userForUpdate = getUserSnapshot()
        modifyUser(userForUpdate)
        if(needUpdateUserByLock) {
            doJobThenOffLoading {
                updateUserChannel.send(userForUpdate)
            }
        }
        return if(requireReturnChangedUser) getUserSnapshot(userForUpdate) else null
    }
    fun isPro(): Boolean {
        return true
    }
    fun updateUserStateToRememberXXXForPage(
        newIsProState: MutableState<Boolean>
    ) {
        user.isProState = newIsProState
    }
}
