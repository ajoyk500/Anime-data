package com.akcreation.gitsilent.dev

import com.akcreation.gitsilent.user.UserUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.MyLog
import java.io.File

@Deprecated("[CHINESE] `UserUtil.isPro()` [CHINESE]")
private val dev_ProModeOn = false
var dev_EnableUnTestedFeature = false  
private val dev_debugModeOn = false  
fun isReleaseMode():Boolean {  
    return true  
}
fun isDebugModeOn():Boolean {
    return MyLog.getCurrentLogLevel() == "d"
}
fun proFeatureEnabled(featureFlag:Boolean):Boolean {
    return UserUtil.isPro() && (dev_EnableUnTestedFeature || featureFlag)
}
fun featureEnabled(featureFlag: Boolean):Boolean {
    return dev_EnableUnTestedFeature || featureFlag
}
fun printIncrementalSyntaxHighlightText():Boolean {
    return false
}
val submoduleTestPassed = true
val ignoreWorktreeFilesTestPassed = true
val initRepoFromFilesPageTestPassed = true
val shallowAndSingleBranchTestPassed = true  
val tagsTestPassed = true  
val detailsDiffTestPassed = true  
val reflogTestPassed = true  
val stashTestPassed = true  
val editorMergeModeTestPassed = true  
val editorSearchTestPassed = true  
val commitsDiffCommitsTestPassed = true  
val commitsDiffToLocalTestPassed = true  
val commitsTreeToTreeDiffReverseTestPassed = true  
val rebaseTestPassed = true
val resetByHashTestPassed = true
val diffToHeadTestPassed = true
val pushForceTestPassed = true
val cherrypickTestPassed = true
val createPatchTestPassed = true
val checkoutFilesTestPassed = true
fun treeToTreeBottomBarActAtLeastOneTestPassed() = cherrypickTestPassed || createPatchTestPassed || checkoutFilesTestPassed  
val applyPatchTestPassed = true
val overwriteExistWhenCreateBranchTestPassed = true
val dontCheckoutWhenCreateBranchAtCheckoutDialogTestPassed = true
val forceCheckoutTestPassed = true
val dontUpdateHeadWhenCheckoutTestPassed = true
val createRemoteTestPassed = true
val branchListPagePublishBranchTestPassed = true
val branchRenameTestPassed = true
val repoRenameTestPassed = true
val importReposFromFilesTestPassed = true
val editorFontSizeTestPassed = true  
val editorLineNumFontSizeTestPassed = true  
val editorHideOrShowLineNumTestPassed = true  
val editorEnableLineSelecteModeFromMenuTestPassed = true  
val importRepoTestPassed = true
const val soraEditorComposeTestPassed = false
const val soraEditorActivityTestPassed = false
val bug_Editor_SelectColumnRangeOfLine_Fixed = true  
val bug_Editor_GoToColumnCantHideKeyboard_Fixed = true 
val bug_Editor_WrongUpdateEditColumnIdx_Fixed = false
object FlagFileName {
    const val enableDebugMode = "debugModeOn"
    const val enableUnTestedFeature = "enableUnTestedFeatureBoom"
    @Deprecated("use `settings.editor.editCacheEnable` instead")
    const val enableEditCache = "enableEditCache"
    const val enableContentSnapshot = "enableContentSnapshot"
    const val enableFileSnapshot = "enableFileSnapshot"
    const val disableGroupDiffContentByLineNum = "disableGroupDiffContentByLineNum"
    fun flagFileExist(flagFileName:String):Boolean {
        return File(AppModel.getOrCreatePuppyGitDataUnderAllReposDir().canonicalPath, flagFileName).exists()
    }
}
