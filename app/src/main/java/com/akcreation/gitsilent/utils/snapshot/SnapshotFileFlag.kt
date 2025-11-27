package com.akcreation.gitsilent.utils.snapshot


private const val editorFileSnapShotPrefix = "edit_file_"
private const val editorContentSnapShotPrefix = "edit_ctnt_"
private const val diffFileSnapShotPrefix = "diff_file_"
enum class SnapshotFileFlag(val flag: String) {
    editor_file_BeforeSave("${editorFileSnapShotPrefix}BS"), 
    editor_file_SimpleSafeFastSave("${editorFileSnapShotPrefix}SSFS"), 
    editor_file_NormalDoSave("${editorFileSnapShotPrefix}NDS"), 
    editor_file_OnPause("${editorFileSnapShotPrefix}OP"), 
    editor_content_SaveErrFallback("${editorContentSnapShotPrefix}SEF"),  
    editor_content_CreateSnapshotForExternalModifiedFileErrFallback("${editorContentSnapShotPrefix}CMEF"),  
    editor_content_InstantSnapshot("${editorContentSnapShotPrefix}IS"),  
    editor_content_FileNonExists_Backup("${editorContentSnapShotPrefix}FNEB"),  
    editor_content_FilePathEmptyWhenSave_Backup("${editorContentSnapShotPrefix}PEB"),  
    editor_content_SimpleSafeFastSave("${editorContentSnapShotPrefix}SSFS"),  
    editor_content_NormalDoSave("${editorContentSnapShotPrefix}NDS"),  
    editor_content_OnPause("${editorContentSnapShotPrefix}OP"),  
    editor_content_BeforeReloadFoundSrcFileChanged("${editorContentSnapShotPrefix}BRFC"),  
    editor_content_BeforeReloadFoundSrcFileChanged_ReloadByBackFromExternalDialog("${editorContentSnapShotPrefix}BRBE"),  
    diff_file_BeforeSave("${diffFileSnapShotPrefix}BS")
    ;
    fun isEditorFileSnapShot():Boolean {
        return flag.startsWith(editorFileSnapShotPrefix)
    }
    fun isEditorContentSnapShot():Boolean {
        return flag.startsWith(editorContentSnapShotPrefix)
    }
    fun isDiffFileSnapShot():Boolean {
        return flag.startsWith(diffFileSnapShotPrefix)
    }
}
