package com.akcreation.gitsilent.utils.snapshot

import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.fileeditor.texteditor.state.TextEditorState
import com.akcreation.gitsilent.screen.shared.FuckSafFile

interface SnapshotCreator{
    fun createSnapshotByContentAndGetResult(
        srcFileName: String,
        fileContent: String?,
        editorState: TextEditorState,
        trueUseContentFalseUseEditorState: Boolean,
        flag: SnapshotFileFlag
    ): Ret<Pair<String, String>?>
    fun createSnapshotByFileAndGetResult(srcFile: FuckSafFile, flag:SnapshotFileFlag):Ret<Pair<String,String>?>
}
