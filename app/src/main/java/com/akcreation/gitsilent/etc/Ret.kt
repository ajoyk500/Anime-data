package com.akcreation.gitsilent.etc


class Ret<T> private constructor(initData:T){
    object ErrCode {  
        val default = 0
        val headIsNull = 1
        val repoStateIsNotNone = 2
        val targetCommitNotFound = 3
        val checkoutTreeError = 4
        val refIsNull = 5
        val checkoutSuccessButDetacheHeadFailedByNewCommitInvalid = 6
        val hasConflictsNotStaged = 7
        val indexIsEmpty = 8
        val branchAlreadyExists = 9
        val usernameOrEmailIsBlank = 10
        val invalidOid = 11
        val resolveCommitErr = 12
        val resolveReferenceError = 13
        val fastforwardTooManyHeads = 14
        val targetRefNotFound = 15
        val newTargetRefIsNull = 16
        val mergeFailedByCreateCommitFaild = 17
        val mergeFailedByGetRepoHeadCommitFaild = 18
        val mergeFailedByAfterMergeHasConfilts = 19  
        val mergeFailedByConfigIsFfOnlyButCantFfMustMerge = 20  
        val mergeFailedByRepoStateIsNotNone = 21
        val createCommitFailedByGetRepoHeadCommitFaild = 22
        val usernameIsBlank = 23
        val emailIsBlank = 24
        val resolveRemotePrefixFromRemoteBranchFullRefSpecFailed = 25
        val remoteIsBlank = 26
        val refspecIsBlank = 27
        val resolveRemoteFailed = 28
        val deleteBranchErr = 29
        val openFileFailed = 30
        val doesntSupportAndroidVersion = 31
        val openFolderFailed = 32
        val createFolderFailed = 33
        val srcListIsEmpty = 34
        val targetIsFileButExpectDir = 35
        val resetErr = 36
        val resolveRevspecFailed = 37
        val unshallowRepoErr = 38
        val headDetached = 39
        val doActForItemErr = 40
        val noSuchElement = 41
        val invalidIdxForList = 42
        val saveFileErr = 43
        val rebaseFailedByRepoStateIsNotNone = 44
        val alreadyUpToDate = 45
    }
    object SuccessCode {  
        val default=1000
        val upToDate=1001
        val openFileWithEditMode=1002
        val openFileWithViewMode=1003
        val fileContentIsEmptyNeedNotCreateSnapshot=1004
        val fastForwardSuccess=1005
    }
    var code = SuccessCode.default
    var msg = ""
    var data:T = initData
    var exception:Exception?=null
    fun hasError():Boolean {
        return code < SuccessCode.default
    }
    fun success():Boolean {
        return !hasError()
    }
    fun<O> copyWithNewData(newData:O? = null):Ret<O?> {
        return create(
            data = newData,
            msg = msg,
            code = code,
            exception = exception
        )
    }
    companion object {
        fun <T>createErrorDefaultDataNull(errMsg:String, data:T? =null, errCode:Int=ErrCode.default, exception: Exception?=null):Ret<T?> {
            return create(data, errMsg, errCode, exception)
        }
        fun <T>createErrorDefaultDataNull(data:T? =null, errMsg:String, errCode:Int=ErrCode.default, exception: Exception?=null):Ret<T?> {
            return create(data, errMsg, errCode, exception)
        }
        fun <T>createSuccessDefaultDataNull(data:T? =null, successMsg:String="", successCode:Int=SuccessCode.default):Ret<T?> {
            return create(data, successMsg, successCode, exception=null)
        }
        fun <T>createError(data:T, errMsg:String, errCode:Int=ErrCode.default, exception: Exception?=null):Ret<T> {
            return create(data, errMsg, errCode, exception)
        }
        fun <T>createSuccess(data:T, successMsg:String="", successCode:Int=SuccessCode.default):Ret<T> {
            return create(data, successMsg, successCode, exception=null)
        }
        fun <T>create(data:T, msg:String, code:Int, exception: Exception?):Ret<T> {
            val r = Ret(data)
            r.data=data
            r.msg=msg
            r.code=code
            r.exception=exception
            return  r
        }
    }
}
