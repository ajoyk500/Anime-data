package com.akcreation.gitsilent.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.ConfirmDialog
import com.akcreation.gitsilent.compose.ConfirmDialog2
import com.akcreation.gitsilent.compose.CopyScrollableColumn
import com.akcreation.gitsilent.compose.DefaultPaddingRow
import com.akcreation.gitsilent.compose.DepthTextField
import com.akcreation.gitsilent.compose.InternalFileChooser
import com.akcreation.gitsilent.compose.LoadingDialog
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.MyHorizontalDivider
import com.akcreation.gitsilent.compose.MySelectionContainer
import com.akcreation.gitsilent.compose.PasswordTextFiled
import com.akcreation.gitsilent.compose.SingleSelectList
import com.akcreation.gitsilent.compose.SingleSelection
import com.akcreation.gitsilent.compose.TokenInsteadOfPasswordHint
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.SpecialCredential
import com.akcreation.gitsilent.data.entity.CredentialEntity
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dev.dev_EnableUnTestedFeature
import com.akcreation.gitsilent.dev.shallowAndSingleBranchTestPassed
import com.akcreation.gitsilent.dto.NameAndPath
import com.akcreation.gitsilent.dto.NameAndPathType
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.title.ScrollableTitle
import com.akcreation.gitsilent.screen.shared.SharedState
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.user.UserUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.FsUtils
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier
import com.akcreation.gitsilent.utils.boolToDbInt
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.checkFileOrFolderNameAndTryCreateFile
import com.akcreation.gitsilent.utils.dbIntToBool
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.filterAndMap
import com.akcreation.gitsilent.utils.getRepoNameFromGitUrl
import com.akcreation.gitsilent.utils.isPathExists
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.akcreation.gitsilent.utils.storagepaths.StoragePathsMan
import com.akcreation.gitsilent.utils.withMainContext
import java.io.File

private const val TAG = "CloneScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneScreen(
    repoId: String,
    naviUp: () -> Boolean,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val activityContext = LocalContext.current
    val inDarkTheme = Theme.inDarkTheme
    val isEditMode = repoId.isNotBlank() && repoId != Cons.dbInvalidNonEmptyId
    val repoFromDb = mutableCustomStateOf(keyTag=stateKeyTag, keyName = "repoFromDb", initValue = RepoEntity(id = ""))
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val allRepoParentDir = AppModel.allRepoParentDir
    val gitUrl = rememberSaveable { mutableStateOf("")}
    val repoName = mutableCustomStateOf(keyTag=stateKeyTag, keyName = "repoName",  initValue = TextFieldValue(""))
    val branch = rememberSaveable { mutableStateOf("")}
    val depth = rememberSaveable { mutableStateOf("")}  
    val credentialName = mutableCustomStateOf(keyTag=stateKeyTag, keyName = "credentialName", initValue = TextFieldValue(""))
    val credentialVal = rememberSaveable { mutableStateOf("")}
    val credentialPass = rememberSaveable { mutableStateOf("")}
    val gitUrlType = rememberSaveable { mutableIntStateOf(Cons.gitUrlTypeHttp) }
    val curCredentialType = rememberSaveable { mutableIntStateOf(Cons.dbCredentialTypeHttp) }
    val allCredentialList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "allCredentialList", initValue = listOf<CredentialEntity>())
    val selectedCredential = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "selectedCredential", initValue = CredentialEntity(id = ""))
    val focusRequesterGitUrl = remember { FocusRequester() }  
    val focusRequesterRepoName = remember { FocusRequester() }  
    val focusRequesterCredentialName = remember { FocusRequester() }  
    val focusToNone = 0
    val focusToGitUrl = 1
    val focusToRepoName = 2
    val focusToCredentialName = 3
    val requireFocusTo = rememberSaveable{ mutableIntStateOf(focusToNone) }  
    val noCredential = stringResource(R.string.no_credential)
    val newCredential = stringResource(R.string.new_credential)
    val selectCredential = stringResource(R.string.select_credential)
    val matchCredentialByDomain = stringResource(R.string.match_credential_by_domain)
    val optNumNoCredential = 0  
    val optNumNewCredential = 1
    val optNumSelectCredential = 2
    val optNumMatchCredentialByDomain = 3
    val credentialRadioOptions = listOf(noCredential, newCredential, selectCredential, matchCredentialByDomain)  
    val (credentialSelectedOption, onCredentialOptionSelected) = rememberSaveable{ mutableIntStateOf(optNumNoCredential) }
    val (isRecursiveClone, onIsRecursiveCloneStateChange) = rememberSaveable { mutableStateOf(false)}
    val (isSingleBranch, onIsSingleBranchStateChange) = rememberSaveable { mutableStateOf(false)}
    val isReadyForClone = rememberSaveable { mutableStateOf(false)}
    val passwordVisible =rememberSaveable { mutableStateOf(false)}
    val showRepoNameAlreadyExistsErr = rememberSaveable { mutableStateOf(false)}
    val showCredentialNameAlreadyExistsErr =rememberSaveable { mutableStateOf(false)}
    val showRepoNameHasIllegalCharsOrTooLongErr = rememberSaveable { mutableStateOf(false)}
    val updateRepoName:(TextFieldValue)->Unit = {
        val newVal = it
        val oldVal = repoName.value
        if(oldVal.text != newVal.text) {
            showRepoNameAlreadyExistsErr.value = false
            showRepoNameHasIllegalCharsOrTooLongErr.value = false
        }
        repoName.value = newVal
    }
    val updateCredentialName:(TextFieldValue)->Unit = {
        val newVal = it
        val oldVal = credentialName.value
        if(oldVal.text != newVal.text) {
            if (showCredentialNameAlreadyExistsErr.value) {
                showCredentialNameAlreadyExistsErr.value = false
            }
        }
        credentialName.value = newVal
    }
    val focusRepoName:()->Unit = {
        val text = repoName.value.text
        repoName.value = repoName.value.copy(
            selection = TextRange(0, text.length)
        )
        requireFocusTo.intValue = focusToRepoName
    }
    val setCredentialNameExistAndFocus:()->Unit = {
        showCredentialNameAlreadyExistsErr.value=true
        val text = credentialName.value.text
        credentialName.value = credentialName.value.copy(
            selection = TextRange(0, text.length)
        )
        requireFocusTo.intValue = focusToCredentialName
    }
    val getStoragePathList = {
        val list = mutableListOf<NameAndPath>(NameAndPath(activityContext.getString(R.string.internal_storage), allRepoParentDir.canonicalPath, NameAndPathType.APP_ACCESSIBLE_STORAGES))
        list.addAll(StoragePathsMan.get().storagePaths.map { NameAndPath.genByPath(it, NameAndPathType.REPOS_STORAGE_PATH, activityContext) })
        list
    }
    val storagePathList = mutableCustomStateListOf(keyTag = stateKeyTag, keyName = "storagePathList", initValue = getStoragePathList())
    val storagePathSelectedPath = rememberSaveable { mutableStateOf(
        StoragePathsMan.get().storagePathLastSelected.let { selectedPath ->
            storagePathList.value.find { it.path == selectedPath } ?: storagePathList.value.getOrNull(0) ?: NameAndPath()
        }
    )}
    val storagePathSelectedIndex = rememberSaveable{ mutableIntStateOf(
        try {
            storagePathList.value.indexOfFirst { storagePathSelectedPath.value.path == it.path }.coerceAtLeast(0)
        }catch (_: Exception) {
            0
        }
    )}
    val showAddStoragePathDialog = rememberSaveable { mutableStateOf(false)}
    val storagePathForAdd = rememberSaveable { SharedState.fileChooser_DirPath }
    val findStoragePathItemByPath = { path:String ->
        var ret = Pair<Int, NameAndPath?>(-1, null)
        for((idx, item) in storagePathList.value.withIndex()) {
            if(item.path == path) {
                ret = Pair(idx, item)
                break
            }
        }
        ret
    }
    if(showAddStoragePathDialog.value) {
        ConfirmDialog(
            title = stringResource(R.string.add_storage_path),
            requireShowTextCompose = true,
            textCompose = {
                MySelectionContainer {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .verticalScroll(rememberScrollState())
                    ) {
                        InternalFileChooser(activityContext, path = storagePathForAdd)
                    }
                }
            },
            okBtnText = stringResource(R.string.ok),
            cancelBtnText = stringResource(R.string.cancel),
            okBtnEnabled = storagePathForAdd.value.isNotBlank(),
            onCancel = {
                showAddStoragePathDialog.value = false
                getStoragePathList().let { latestList ->
                    storagePathList.value.apply {
                        clear()
                        addAll(latestList)
                    }
                }
            },
        ) {
            showAddStoragePathDialog.value = false
            val storagePathForAdd = storagePathForAdd.value
            doJobThenOffLoading {
                try {
                    val newPathRet = FsUtils.userInputPathToCanonical(storagePathForAdd)
                    if(newPathRet.hasError()) {
                        throw RuntimeException(activityContext.getString(R.string.invalid_path))
                    }
                    val newPath = newPathRet.data!!
                    if(File(newPath).isDirectory.not()) {
                        throw RuntimeException(activityContext.getString(R.string.path_is_not_a_dir))
                    }
                    val latestList = getStoragePathList()
                    val storagePathList = storagePathList.value
                    storagePathList.clear()
                    storagePathList.addAll(latestList)
                    val spForSave = StoragePathsMan.get()
                    val (indexOfStoragePath, existedStoragePath) = findStoragePathItemByPath(newPath)
                    if(indexOfStoragePath != -1) { 
                        storagePathSelectedPath.value = existedStoragePath!!
                        storagePathSelectedIndex.intValue = indexOfStoragePath
                        spForSave.storagePathLastSelected = newPath
                    }else { 
                        val newItem = NameAndPath.genByPath(newPath, NameAndPathType.REPOS_STORAGE_PATH, activityContext)
                        storagePathList.add(newItem)
                        val newItemIndex = storagePathList.size - 1
                        storagePathSelectedIndex.intValue = newItemIndex
                        storagePathSelectedPath.value = newItem
                        spForSave.storagePaths.add(newPath)
                        spForSave.storagePathLastSelected = newPath
                    }
                    StoragePathsMan.save(spForSave)
                }catch (e: Exception) {
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    MyLog.e(TAG, "add storage path at `$TAG` err: ${e.stackTraceToString()}")
                }
            }
        }
    }
    val indexForDeleteStoragePathDialog = rememberSaveable { mutableStateOf(-1) }
    val showDeleteStoragePathListDialog = rememberSaveable { mutableStateOf(false) }
    val initDeleteStoragePathListDialog = { index:Int ->
        indexForDeleteStoragePathDialog.value = index
        showDeleteStoragePathListDialog.value = true
    }
    if(showDeleteStoragePathListDialog.value) {
        val targetPath = storagePathList.value.getOrNull(indexForDeleteStoragePathDialog.value)?.path ?: ""
        val closeDialog = { showDeleteStoragePathListDialog.value = false }
        val deleteStoragePath = j@{ index:Int ->
            if(storagePathList.value.getOrNull(index)?.type != NameAndPathType.REPOS_STORAGE_PATH) {
                Msg.requireShowLongDuration("can't remove item")
                return@j
            }
            storagePathList.value.removeAt(index)
            val spForSave = StoragePathsMan.get()
            val removedCurrent = index == storagePathSelectedIndex.intValue
            if(removedCurrent) {
                val newCurrentIndex = 0
                storagePathSelectedIndex.intValue = newCurrentIndex
                val newCurrent = storagePathList.value[newCurrentIndex]
                storagePathSelectedPath.value = newCurrent
                spForSave.storagePathLastSelected = newCurrent.path
            }
            spForSave.storagePaths.clear()
            val list = storagePathList.value.filterAndMap({ it.type == NameAndPathType.REPOS_STORAGE_PATH }) { it.path }
            if(list.isNotEmpty()) {
                spForSave.storagePaths.addAll(list)
            }
            StoragePathsMan.save(spForSave)
        }
        ConfirmDialog2(
            title = stringResource(R.string.delete),
            requireShowTextCompose = true,
            textCompose = {
                CopyScrollableColumn {
                    Text(targetPath)
                }
            },
            okBtnText = stringResource(R.string.delete),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = closeDialog
        ) {
            closeDialog()
            val targetIndex = indexForDeleteStoragePathDialog.value
            doJobThenOffLoading {
                deleteStoragePath(targetIndex)
            }
        }
    }
    val showLoadingDialog = rememberSaveable { mutableStateOf(false)}
    val doSave:()->Unit = {
        doJobThenOffLoading launch@{
            showLoadingDialog.value=true
            val repoNameText = repoName.value.text
            val repoNameCheckRet = checkFileOrFolderNameAndTryCreateFile(repoNameText, activityContext)
            if(repoNameCheckRet.hasError()) {
                Msg.requireShowLongDuration(repoNameCheckRet.msg)
                focusRepoName()
                showRepoNameHasIllegalCharsOrTooLongErr.value=true
                showLoadingDialog.value=false
                return@launch
            }
            val repoDb = AppModel.dbContainer.repoRepository
            val credentialDb = AppModel.dbContainer.credentialRepository
            val fullSavePath = File(storagePathSelectedPath.value.path, repoNameText).canonicalPath
            val isRepoNameExist = if(!isEditMode || repoNameText != repoFromDb.value.repoName) {
                repoDb.isRepoNameExist(repoNameText)
            }else {
                false
            }
            if(isRepoNameExist || isPathExists(null, fullSavePath)) {  
                focusRepoName()
                showRepoNameAlreadyExistsErr.value=true
                showLoadingDialog.value=false
                return@launch
            }
            var credentialIdForClone = ""  
            var credentialForSave:CredentialEntity? = null
            if(credentialSelectedOption==optNumNewCredential) {
                val credentialNameText = credentialName.value.text
                val isCredentialNameExist = credentialDb.isCredentialNameExist(credentialNameText)
                if(isCredentialNameExist) {
                    setCredentialNameExistAndFocus()
                    showLoadingDialog.value=false
                    return@launch
                }
                credentialForSave = CredentialEntity(
                    name = credentialNameText,
                    value = credentialVal.value,
                    pass = credentialPass.value,
                    type = curCredentialType.intValue,
                )
                credentialDb.insertWithEncrypt(credentialForSave)
                credentialIdForClone = credentialForSave.id
            } else if(credentialSelectedOption == optNumSelectCredential) {
                credentialIdForClone = selectedCredential.value.id
            } else if(credentialSelectedOption == optNumMatchCredentialByDomain) {
                credentialIdForClone = SpecialCredential.MatchByDomain.credentialId
            }
            var intDepth = 0
            var isShallow = Cons.dbCommonFalse
            if(depth.value.isNotBlank()) {
                try {  
                    intDepth = depth.value.toInt().coerceAtLeast(0)
                }catch (e:Exception) {  
                    intDepth = 0
                    Log.d(TAG,"invalid depth value '${depth.value}', will use default value '0', err=${e.localizedMessage}")
                }
                if(intDepth > 0) {  
                    isShallow = Cons.dbCommonTrue
                }
            }
            val repoForSave:RepoEntity = if(isEditMode) repoFromDb.value else RepoEntity(createBy = Cons.dbRepoCreateByClone)
            repoForSave.repoName = repoNameText
            repoForSave.fullSavePath = fullSavePath
            repoForSave.cloneUrl = gitUrl.value
            repoForSave.workStatus = Cons.dbRepoWorkStatusNotReadyNeedClone
            repoForSave.credentialIdForClone = credentialIdForClone
            repoForSave.isRecursiveCloneOn = boolToDbInt(isRecursiveClone)
            repoForSave.depth = intDepth
            repoForSave.isShallow = isShallow
            if(branch.value.isNotBlank()) {  
                repoForSave.branch=branch.value
                repoForSave.isSingleBranch=boolToDbInt(isSingleBranch)
            }else{  
                repoForSave.branch = ""
                repoForSave.isSingleBranch=Cons.dbCommonFalse  
            }
            if(isEditMode){
                repoDb.update(repoForSave)
            }else{
                repoDb.insert(repoForSave)
            }
            showLoadingDialog.value=false
            withMainContext {
                naviUp()
            }
        }
    }
    val loadingText = rememberSaveable { mutableStateOf(activityContext.getString(R.string.loading))}
    val listState = rememberScrollState()
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    val spacerPadding = 2.dp
    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    ScrollableTitle(stringResource(R.string.clone), listState, lastPosition)
                },
                navigationIcon = {
                    LongPressAbleIconBtn(
                        tooltipText = stringResource(R.string.back),
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        iconContentDesc = stringResource(R.string.back),
                        ) {
                        naviUp()
                    }
                },
                actions = {
                    LongPressAbleIconBtn(
                        tooltipText = stringResource(R.string.save),
                        icon =  Icons.Filled.Check,
                        iconContentDesc = stringResource(id = R.string.save),
                        enabled = isReadyForClone.value,
                        ) {
                        doSave()
                    }
                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
    ) { contentPadding->
        if (showLoadingDialog.value) {
            LoadingDialog(loadingText.value)
        }
        Column (
            modifier = Modifier
                .baseVerticalScrollablePageModifier(contentPadding, listState)
                .padding(bottom = MyStyleKt.Padding.PageBottom)
                .imePadding()
            ,
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MyStyleKt.defaultItemPadding)
                    .focusRequester(focusRequesterGitUrl),
                singleLine = true,
                value = gitUrl.value,
                onValueChange = {
                    gitUrl.value = it
                    val repoNameFromGitUrl = getRepoNameFromGitUrl(it)
                    if(repoNameFromGitUrl.isNotBlank() && repoName.value.text.isBlank()) {
                        updateRepoName(TextFieldValue(repoNameFromGitUrl))
                    }
                    val newGitUrlType = Libgit2Helper.getGitUrlType(it)  
                    val newCredentialType = Libgit2Helper.getCredentialTypeByGitUrlType(newGitUrlType)  
                    curCredentialType.intValue = newCredentialType
                    gitUrlType.intValue = newGitUrlType
                },
                label = {
                    Row {
                        Text(stringResource(R.string.git_url))
                        Text(text = " ("+stringResource(id = R.string.http_https_ssh)+")",
                        )
                    }
                },
                placeholder = {
                    Text(stringResource(R.string.git_url_placeholder))
                }
            )
            Spacer(modifier = Modifier.padding(spacerPadding))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MyStyleKt.defaultItemPadding)
                    .focusRequester(focusRequesterRepoName)
                ,
                value = repoName.value,
                singleLine = true,
                isError = showRepoNameAlreadyExistsErr.value || showRepoNameHasIllegalCharsOrTooLongErr.value,
                supportingText = {
                    val errMsg = if(showRepoNameAlreadyExistsErr.value) stringResource(R.string.repo_name_exists_err)
                                else if(showRepoNameHasIllegalCharsOrTooLongErr.value) stringResource(R.string.err_repo_name_has_illegal_chars_or_too_long)
                                else ""
                    if (showRepoNameAlreadyExistsErr.value || showRepoNameHasIllegalCharsOrTooLongErr.value) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = errMsg,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    val errMsg = if(showRepoNameAlreadyExistsErr.value) stringResource(R.string.repo_name_exists_err)
                                else if(showRepoNameHasIllegalCharsOrTooLongErr.value) stringResource(R.string.err_repo_name_has_illegal_chars_or_too_long)
                                else ""
                    if (showRepoNameAlreadyExistsErr.value || showRepoNameHasIllegalCharsOrTooLongErr.value) {
                        Icon(imageVector=Icons.Filled.Error,
                            contentDescription=errMsg,
                            tint = MaterialTheme.colorScheme.error)
                    }
                },
                onValueChange = {
                    updateRepoName(it)
                },
                label = {
                    Text(stringResource(R.string.repo_name))
                },
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                ,
            ) {
                val addIconSize = MyStyleKt.defaultIconSizeSmaller
                SingleSelectList(
                    outterModifier = Modifier.align(Alignment.CenterStart),
                    basePadding = { defaultHorizontalPadding -> PaddingValues(end = addIconSize + 5.dp + defaultHorizontalPadding, start = defaultHorizontalPadding) },
                    optionsList = storagePathList.value,
                    selectedOptionIndex = storagePathSelectedIndex,
                    selectedOptionValue = storagePathSelectedPath.value,
                    menuItemFormatter = { _, value ->
                        value?.name?:""
                    },
                    menuItemFormatterLine2 = { _, value ->
                        FsUtils.getPathWithInternalOrExternalPrefix(value?.path ?: "")
                    },
                    menuItemOnClick = { index, value ->
                        storagePathSelectedIndex.intValue = index
                        storagePathSelectedPath.value = value
                        StoragePathsMan.update {
                            it.storagePathLastSelected = value.path
                        }
                    },
                    menuItemTrailIcon = Icons.Filled.DeleteOutline,
                    menuItemTrailIconDescription = stringResource(R.string.trash_bin_icon_for_delete_item),
                    menuItemTrailIconEnable = {index, value->
                        index!=0
                    },
                    menuItemTrailIconOnClick = { index, value ->
                        if(index == 0) {
                            Msg.requireShowLongDuration(activityContext.getString(R.string.cant_delete_internal_storage))
                        }else {
                            initDeleteStoragePathListDialog(index)
                        }
                    }
                )
                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { showAddStoragePathDialog.value = true }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_storage_path),
                        modifier = Modifier.size(addIconSize)
                    )
                }
            }
            Spacer(modifier = Modifier.padding(spacerPadding))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MyStyleKt.defaultItemPadding),
                value = branch.value,
                singleLine = true,
                onValueChange = {
                    branch.value=it
                },
                label = {
                    Text(stringResource(R.string.branch_optional))
                },
                placeholder = {
                    Text(stringResource(R.string.branch_name))
                }
            )
            if(dev_EnableUnTestedFeature || shallowAndSingleBranchTestPassed) {
                val isPro = UserUtil.isPro()
                val enableSingleBranch =  isPro && branch.value.isNotBlank()
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(MyStyleKt.CheckoutBox.height)
                        .toggleable(
                            enabled = enableSingleBranch,
                            value = isSingleBranch,
                            onValueChange = { onIsSingleBranchStateChange(!isSingleBranch) },
                            role = Role.Checkbox
                        )
                        .padding(horizontal = MyStyleKt.defaultHorizontalPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        enabled = enableSingleBranch,
                        checked = isSingleBranch,
                        onCheckedChange = null 
                    )
                    Text(
                        text = if(isPro) stringResource(R.string.single_branch) else stringResource(R.string.single_branch_pro_only),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp),
                        color = if(enableSingleBranch) Color.Unspecified else if(inDarkTheme) MyStyleKt.TextColor.disable_DarkTheme else MyStyleKt.TextColor.disable
                    )
                }
                Spacer(modifier = Modifier.padding(spacerPadding))
                DepthTextField(depth)
                Spacer(modifier = Modifier.padding(spacerPadding))
            }
            MyHorizontalDivider(modifier = Modifier.padding(spacerPadding))
            Spacer(Modifier.height(10.dp))
            val credentialListIsEmpty = allCredentialList.value.isEmpty()
            SingleSelection(
                itemList = credentialRadioOptions,
                selected = {idx, item -> credentialSelectedOption == idx},
                text = {idx, item -> item},
                onClick = {idx, item -> onCredentialOptionSelected(idx)},
                beforeShowItem = {idx, item ->
                    if(idx == optNumNewCredential) {
                        curCredentialType.intValue = Libgit2Helper.getCredentialTypeByUrl(gitUrl.value)
                    }
                },
                skip = {idx, item ->
                    credentialListIsEmpty && idx == optNumSelectCredential
                }
            )
            if(credentialSelectedOption == optNumNewCredential) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MyStyleKt.defaultItemPadding)
                        .focusRequester(focusRequesterCredentialName)
                    ,
                    isError = showCredentialNameAlreadyExistsErr.value,
                    supportingText = {
                        if (showCredentialNameAlreadyExistsErr.value) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.credential_name_exists_err),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        if (showCredentialNameAlreadyExistsErr.value)
                            Icon(imageVector=Icons.Filled.Error,
                                contentDescription= stringResource(R.string.credential_name_exists_err),
                                tint = MaterialTheme.colorScheme.error)
                    },
                    singleLine = true,
                    value = credentialName.value,
                    onValueChange = {
                        updateCredentialName(it)
                    },
                    label = {
                        Text(stringResource(R.string.credential_name))
                    },
                    placeholder = {
                        Text(stringResource(R.string.credential_name_placeholder))
                    }
                )
                TextField(
                    modifier =
                    if(curCredentialType.intValue == Cons.dbCredentialTypeSsh) {
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 300.dp, max = 300.dp)
                            .padding(MyStyleKt.defaultItemPadding)
                    }else{
                        Modifier
                            .fillMaxWidth()
                            .padding(MyStyleKt.defaultItemPadding)
                    }
                        ,
                    singleLine = curCredentialType.intValue != Cons.dbCredentialTypeSsh,
                    value = credentialVal.value,
                    onValueChange = {
                        credentialVal.value=it
                    },
                    label = {
                        if(curCredentialType.intValue == Cons.dbCredentialTypeSsh) {
                            Text(stringResource(R.string.private_key))
                        }else{
                            Text(stringResource(R.string.username))
                        }
                    },
                    placeholder = {
                        if(curCredentialType.intValue == Cons.dbCredentialTypeSsh) {
                            Text(stringResource(R.string.paste_your_private_key_here))
                        }else{
                            Text(stringResource(R.string.username))
                        }
                    }
                )
                PasswordTextFiled(
                    password = credentialPass,
                    label = if(curCredentialType.intValue == Cons.dbCredentialTypeSsh) {
                        stringResource(R.string.passphrase_if_have)
                    }else{
                        stringResource(R.string.password)
                    },
                    placeholder = if(curCredentialType.intValue == Cons.dbCredentialTypeSsh) {
                        stringResource(R.string.input_passphrase_if_have)
                    }else{
                        stringResource(R.string.password)
                    },
                    passwordVisible = passwordVisible,
                )
                if(curCredentialType.intValue == Cons.dbCredentialTypeHttp) {
                    TokenInsteadOfPasswordHint()
                }
            }else if(credentialSelectedOption == optNumSelectCredential) {
                Spacer(Modifier.height(MyStyleKt.defaultItemPadding))
                SingleSelectList(
                    optionsList = allCredentialList.value,
                    selectedOptionIndex = null,
                    selectedOptionValue = selectedCredential.value,
                    menuItemSelected = { _, item ->
                        item.id == selectedCredential.value.id
                    },
                    menuItemFormatter = { _, item ->
                        item?.name?:""
                    },
                    menuItemOnClick = { _, item ->
                        selectedCredential.value = item
                    },
                )
            }else if(credentialSelectedOption == optNumMatchCredentialByDomain) {
                MySelectionContainer {
                    DefaultPaddingRow {
                        Text(stringResource(R.string.credential_match_by_domain_note), color = MyStyleKt.TextColor.getHighlighting(), fontWeight = FontWeight.Light)
                    }
                }
            }
        }
    }
    if(requireFocusTo.intValue==focusToGitUrl) {
        requireFocusTo.intValue=focusToNone
        focusRequesterGitUrl.requestFocus()
    }else if(requireFocusTo.intValue==focusToRepoName) {
        requireFocusTo.intValue=focusToNone
        focusRequesterRepoName.requestFocus()
    }else if(requireFocusTo.intValue==focusToCredentialName) {
        requireFocusTo.intValue=focusToNone
        focusRequesterCredentialName.requestFocus()
    }
    LaunchedEffect(Unit) {
        doJobThenOffLoading(
        ) job@{
            if (isEditMode) {  
                val repoDb = AppModel.dbContainer.repoRepository
                val credentialDb = AppModel.dbContainer.credentialRepository
                val repo = repoDb.getById(repoId)
                if(repo == null) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.repo_id_invalid))
                    return@job
                }
                gitUrlType.intValue = Libgit2Helper.getGitUrlType(repo.cloneUrl)  
                gitUrl.value = repo.cloneUrl
                repoName.value = TextFieldValue(repo.repoName)
                branch.value = repo.branch
                onIsSingleBranchStateChange(dbIntToBool(repo.isSingleBranch))
                onIsRecursiveCloneStateChange(dbIntToBool(repo.isRecursiveCloneOn))
                if (Libgit2Helper.needSetDepth(repo.depth)) {
                    depth.value = "" + repo.depth
                }
                repoFromDb.value = repo
                val storagePath = File(repo.fullSavePath).parent ?: ""
                val (selectedStoragePathIdx, selectedStoragePathItem) = findStoragePathItemByPath(storagePath)
                storagePathSelectedIndex.intValue = selectedStoragePathIdx
                storagePathSelectedPath.value = selectedStoragePathItem ?: NameAndPath.genByPath(storagePath, NameAndPathType.REPOS_STORAGE_PATH, activityContext)
                val credentialIdForClone = repo.credentialIdForClone
                if (!credentialIdForClone.isNullOrBlank()) {  
                    if(credentialIdForClone == SpecialCredential.MatchByDomain.credentialId) {
                        onCredentialOptionSelected(optNumMatchCredentialByDomain)
                    }else {
                        val credential = credentialDb.getById(credentialIdForClone)
                        if (credential == null) {  
                            onCredentialOptionSelected(optNumNoCredential)
                        } else {  
                            onCredentialOptionSelected(optNumSelectCredential)  
                            selectedCredential.value = credential
                            curCredentialType.intValue = Libgit2Helper.getCredentialTypeByUrl(repo.cloneUrl)  
                        }
                    }
                }
            } else {  
                requireFocusTo.intValue = focusToGitUrl
            }
            val credentialDb = AppModel.dbContainer.credentialRepository
            allCredentialList.value.clear()
            allCredentialList.value.addAll(credentialDb.getAll())
        }
    }
    isReadyForClone.value = ((gitUrl.value.isNotBlank() && repoName.value.text.isNotBlank())
        &&
        ((credentialSelectedOption==optNumNoCredential || credentialSelectedOption==optNumMatchCredentialByDomain)  
                || ((credentialSelectedOption==optNumNewCredential && credentialName.value.text.isNotBlank())  
                   )
                || (credentialSelectedOption==optNumSelectCredential && selectedCredential.value.id.isNotBlank() && selectedCredential.value.name.isNotBlank()))
        && !showRepoNameAlreadyExistsErr.value && !showRepoNameHasIllegalCharsOrTooLongErr.value && !showCredentialNameAlreadyExistsErr.value
        )
}
