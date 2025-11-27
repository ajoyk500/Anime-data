package com.akcreation.gitsilent.screen.content.homescreen.innerpage

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.ClearMasterPasswordDialog
import com.akcreation.gitsilent.compose.ClickableText
import com.akcreation.gitsilent.compose.CommitMsgTemplateDialog
import com.akcreation.gitsilent.compose.ConfirmDialog2
import com.akcreation.gitsilent.compose.ConfirmDialog3
import com.akcreation.gitsilent.compose.CopyableDialog
import com.akcreation.gitsilent.compose.DefaultPaddingText
import com.akcreation.gitsilent.compose.MyCheckBox
import com.akcreation.gitsilent.compose.MySelectionContainer
import com.akcreation.gitsilent.compose.PasswordTextFiled
import com.akcreation.gitsilent.compose.ScrollableColumn
import com.akcreation.gitsilent.compose.ScrollableRow
import com.akcreation.gitsilent.compose.SettingsContent
import com.akcreation.gitsilent.compose.SettingsContentSelector
import com.akcreation.gitsilent.compose.SettingsContentSwitcher
import com.akcreation.gitsilent.compose.SettingsTitle
import com.akcreation.gitsilent.compose.SingleSelectList
import com.akcreation.gitsilent.compose.SoftkeyboardVisibleListener
import com.akcreation.gitsilent.compose.SpacerRow
import com.akcreation.gitsilent.compose.TwoLineSettingsItem
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.StrCons
import com.akcreation.gitsilent.dev.DevFeature
import com.akcreation.gitsilent.dev.DevItem
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.settings.SettingsCons
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.ActivityUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.ComposeHelper
import com.akcreation.gitsilent.utils.EditCache
import com.akcreation.gitsilent.utils.HashUtil
import com.akcreation.gitsilent.utils.LanguageUtil
import com.akcreation.gitsilent.utils.Lg2HomeUtils
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.StrListUtil
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.encrypt.MasterPassUtil
import com.akcreation.gitsilent.utils.fileopenhistory.FileOpenHistoryMan
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.formatMinutesToUtc
import com.akcreation.gitsilent.utils.getInvalidTimeZoneOffsetErrMsg
import com.akcreation.gitsilent.utils.getValidTimeZoneOffsetRangeInMinutes
import com.akcreation.gitsilent.utils.isValidOffsetInMinutes
import com.akcreation.gitsilent.utils.pref.PrefMan
import com.akcreation.gitsilent.utils.pref.PrefUtil
import com.akcreation.gitsilent.utils.replaceStringResList
import com.akcreation.gitsilent.utils.snapshot.SnapshotUtil
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import com.akcreation.gitsilent.utils.storagepaths.StoragePathsMan

private const val TAG = "SettingsInnerPage"
private val trailIconWidth = MyStyleKt.defaultLongPressAbleIconBtnPressedCircleSize
@Composable
fun SettingsInnerPage(
    stateKeyTag:String,
    contentPadding: PaddingValues,
    needRefreshPage:MutableState<String>,
    openDrawer:()->Unit,
    exitApp:()->Unit,
    listState:ScrollState,
    goToFilesPage:(path:String)->Unit,
){
    val stateKeyTag = Cache.getComponentKey(stateKeyTag, TAG)
    val view = LocalView.current
    val density = LocalDensity.current
    val isKeyboardVisible = rememberSaveable { mutableStateOf(false) }
    val isKeyboardCoveredComponent = rememberSaveable { mutableStateOf(false) }
    val componentHeight = rememberSaveable { mutableIntStateOf(0) }
    val keyboardPaddingDp = rememberSaveable { mutableIntStateOf(0) }
    val activityContext = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val settingsState = mutableCustomStateOf(stateKeyTag, "settingsState", SettingsUtil.getSettingsSnapshot())
    val themeList = Theme.themeList
    val selectedTheme = rememberSaveable { mutableIntStateOf(PrefMan.getInt(activityContext, PrefMan.Key.theme, Theme.defaultThemeValue)) }
    val languageList = LanguageUtil.languageCodeList
    val selectedLanguage = rememberSaveable { mutableStateOf(LanguageUtil.getLangCode(activityContext)) }
    val logLevelList = MyLog.logLevelList
    val selectedLogLevel = rememberSaveable { mutableStateOf(MyLog.getCurrentLogLevel()) }
    val enableEditCache = rememberSaveable { mutableStateOf(settingsState.value.editor.editCacheEnable) }
    val showNaviButtons = rememberSaveable { mutableStateOf(settingsState.value.showNaviButtons) }
    val syntaxHighlightEnabled = rememberSaveable { mutableStateOf(settingsState.value.editor.syntaxHighlightEnabled) }
    val syntaxHighlightEnabled_DiffScreen = rememberSaveable { mutableStateOf(settingsState.value.diff.syntaxHighlightEnabled) }
    val useSystemFonts = rememberSaveable { mutableStateOf(settingsState.value.editor.useSystemFonts) }
    val useSystemFonts_DiffScreen = rememberSaveable { mutableStateOf(settingsState.value.diff.useSystemFonts) }
    val devModeOn = rememberSaveable { mutableStateOf(PrefUtil.getDevMode(activityContext)) }
    val enableSnapshot_File = rememberSaveable { mutableStateOf(settingsState.value.editor.enableFileSnapshot) }
    val enableSnapshot_Content = rememberSaveable { mutableStateOf(settingsState.value.editor.enableContentSnapshot) }
    val diff_CreateSnapShotForOriginFileBeforeSave = rememberSaveable { mutableStateOf(settingsState.value.diff.createSnapShotForOriginFileBeforeSave) }
    val pullWithRebase = rememberSaveable { mutableStateOf(settingsState.value.globalGitConfig.pullWithRebase) }
    val fileAssociationList = mutableCustomStateListOf(stateKeyTag, "fileAssociationList") { settingsState.value.editor.fileAssociationList }
    val fileAssociationListBuf = rememberSaveable { mutableStateOf("") }
    val showSetFileAssociationDialog = rememberSaveable { mutableStateOf(false) }
    val initSetFileAssociationDialog = {
        fileAssociationListBuf.value = StrListUtil.listToLines(fileAssociationList.value)
        showSetFileAssociationDialog.value = true
    }
    if(showSetFileAssociationDialog.value) {
        val closeDialog = { showSetFileAssociationDialog.value = false }
        val cancelText = stringResource(R.string.cancel)
        ConfirmDialog3(
            title = stringResource(R.string.file_association),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        componentHeight.intValue = layoutCoordinates.size.height
                    }
                ) {
                    MySelectionContainer {
                        Text(stringResource(R.string.file_association_note), fontWeight = FontWeight.Light)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                            ),
                        value = fileAssociationListBuf.value,
                        onValueChange = {
                            fileAssociationListBuf.value = it
                        },
                        label = {
                            Text(stringResource(R.string.file_name_patterns))
                        },
                    )
                    Spacer(Modifier.height(10.dp))
                }
            },
            okBtnText = stringResource(R.string.save),
            cancelBtnText = cancelText,
            onCancel = closeDialog,
            customCancel = {
                ScrollableRow {
                    TextButton(
                        onClick = {
                            fileAssociationListBuf.value = StrListUtil.listToLines(SettingsCons.editor_defaultFileAssociationList)
                        }
                    ) {
                        Text(stringResource(R.string.reset), color = MyStyleKt.TextColor.danger())
                    }
                    TextButton(
                        onClick = closeDialog
                    ) {
                        Text(cancelText)
                    }
                }
            }
        ) {
            showSetFileAssociationDialog.value = false
            doJobThenOffLoading {
                val newValue = fileAssociationListBuf.value
                val newList = StrListUtil.linesToList(newValue)
                fileAssociationList.value.clear()
                fileAssociationList.value.addAll(newList)
                SettingsUtil.update {
                    it.editor.fileAssociationList = newList
                }
                Msg.requireShow(activityContext.getString(R.string.success))
            }
        }
    }
    val showCommitMsgTemplateDialog = rememberSaveable { mutableStateOf(false) }
    if(showCommitMsgTemplateDialog.value) {
        CommitMsgTemplateDialog(
            stateKeyTag = stateKeyTag,
            closeDialog = { showCommitMsgTemplateDialog.value = false }
        )
    }
    val showCleanDialog = rememberSaveable { mutableStateOf(false) }
    val cleanCacheFolder = rememberSaveable { mutableStateOf(true) }
    val cleanEditCache = rememberSaveable { mutableStateOf(true) }
    val cleanSnapshot = rememberSaveable { mutableStateOf(true) }
    val cleanLog = rememberSaveable { mutableStateOf(true) }
    val cleanStoragePath = rememberSaveable { mutableStateOf(false) }
    val cleanFileOpenHistory = rememberSaveable { mutableStateOf(false) }
    val allowUnknownHosts = rememberSaveable { mutableStateOf(settingsState.value.sshSetting.allowUnknownHosts) }
    val httpSslVerify = rememberSaveable { mutableStateOf(settingsState.value.httpSetting.sslVerify) }
    val showForgetHostKeysDialog = rememberSaveable { mutableStateOf(false) }
    if(showForgetHostKeysDialog.value) {
        ConfirmDialog3(
            requireShowTitleCompose = true,
            titleCompose = {},
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(
                            text = stringResource(R.string.after_forgetting_the_host_keys_may_ask_confirm_again),
                            fontSize = MyStyleKt.TextSize.medium
                        )
                    }
                }
            },
            okBtnText = stringResource(R.string.forget),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = { showForgetHostKeysDialog.value = false }
        ) {
            showForgetHostKeysDialog.value = false
            doJobThenOffLoading {
                try {
                    Lg2HomeUtils.resetUserKnownHostFile()
                    Msg.requireShow(activityContext.getString(R.string.success))
                }catch (e:Exception) {
                    Msg.requireShowLongDuration(e.localizedMessage ?:"err")
                    MyLog.e(TAG, "ForgetHostKeysDialog err: ${e.stackTraceToString()}")
                }
            }
        }
    }
    val showImportSslCertsDialog = rememberSaveable { mutableStateOf(false) }
    if(showImportSslCertsDialog.value) {
        ConfirmDialog3(
            requireShowTitleCompose = true,
            titleCompose = {},
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MySelectionContainer {
                        Text(
                            text = stringResource(R.string.import_ssl_certs_intro_text),
                            fontSize = MyStyleKt.TextSize.medium
                        )
                    }
                }
            },
            onCancel = {
                showImportSslCertsDialog.value = false
            },
            customCancel = {
                IconButton(
                    onClick = {
                        goToFilesPage(AppModel.certUserDir.canonicalPath)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Folder,
                        contentDescription = "a folder icon for go to user's cert folder"
                    )
                }
            }
        ) {
            showImportSslCertsDialog.value = false
        }
    }
    val updateMasterPassFailedListStr = rememberSaveable { mutableStateOf("") }
    val showFailedUpdateMasterPasswordsCredentialList = rememberSaveable { mutableStateOf(false) }
    if(showFailedUpdateMasterPasswordsCredentialList.value) {
        CopyableDialog(
            title = stringResource(R.string.warn),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    Text(stringResource(R.string.below_credential_password_update_failed))
                    Spacer(Modifier.height(10.dp))
                    Text(updateMasterPassFailedListStr.value, fontWeight = FontWeight.ExtraBold)
                }
            },
            onCancel = { showFailedUpdateMasterPasswordsCredentialList.value = false }
        ) {
            showFailedUpdateMasterPasswordsCredentialList.value = false
            doJobThenOffLoading {
                clipboardManager.setText(AnnotatedString(updateMasterPassFailedListStr.value))
                Msg.requireShow(activityContext.getString(R.string.copied))
            }
        }
    }
    val showSetTimeZoneDialog = rememberSaveable { mutableStateOf(false) }
    val timeZone_followSystem = rememberSaveable { mutableStateOf(settingsState.value.timeZone.followSystem) }
    val timeZone_followSystemBuf = rememberSaveable { mutableStateOf(timeZone_followSystem.value) }
    val timeZone_offsetInMinute = rememberSaveable { mutableStateOf(settingsState.value.timeZone.offsetInMinutes) }
    val timeZone_offsetInMinuteBuf = rememberSaveable { mutableStateOf(timeZone_offsetInMinute.value) }
    val getTimeZoneStr = {
        try {
            val offsetInMinutes = if(timeZone_followSystem.value) {
                AppModel.getSystemTimeZoneOffsetInMinutesCached()
            } else {
                val offsetInMinuteFromSettings = timeZone_offsetInMinute.value.trim().toInt()
                if(isValidOffsetInMinutes(offsetInMinuteFromSettings)){
                    offsetInMinuteFromSettings
                } else{
                    timeZone_offsetInMinute.value = ""
                    SettingsUtil.update {
                        it.timeZone.offsetInMinutes = ""
                    }
                    val errMsg = getInvalidTimeZoneOffsetErrMsg(offsetInMinuteFromSettings)
                    MyLog.e(TAG, "#getTimeZoneStr err: $errMsg")
                    throw RuntimeException(errMsg)
                }
            }
            val offsetInUtcFormat = formatMinutesToUtc(offsetInMinutes)
            if(timeZone_followSystem.value) {
                "${activityContext.getString(R.string.follow_system)} ($offsetInUtcFormat)"
            }else {
                offsetInUtcFormat
            }
        }catch (_:Exception) {
            activityContext.getString(R.string.default_timezone_rule_description)
        }
    }
    val initSetTimeZoneDialog = {
        timeZone_offsetInMinuteBuf.value = timeZone_offsetInMinute.value
        timeZone_followSystemBuf.value = timeZone_followSystem.value
        showSetTimeZoneDialog.value = true
    }
    if(showSetTimeZoneDialog.value) {
        ConfirmDialog2(title = stringResource(R.string.timezone),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    modifier= Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                    ,
                ) {
                    MyCheckBox(stringResource(R.string.follow_system), timeZone_followSystemBuf)
                    if(timeZone_followSystemBuf.value.not()) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MyStyleKt.defaultItemPadding)
                            ,
                            singleLine = true,
                            value = timeZone_offsetInMinuteBuf.value,
                            onValueChange = {
                                timeZone_offsetInMinuteBuf.value=it
                            },
                            label = {
                                Text(stringResource(R.string.offset_in_minutes))
                            },
                        )
                        MySelectionContainer {
                            Column(modifier= Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)) {
                                    Text(stringResource(R.string.timezone_offset_example), fontWeight = FontWeight.Light)
                                }
                                Row(modifier = Modifier.padding(top = 5.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)) {
                                    Text(stringResource(R.string.set_timezone_leave_empty_note), color = MyStyleKt.TextColor.getHighlighting())
                                }
                            }
                        }
                        Column(
                            modifier= Modifier
                                .fillMaxWidth()
                                .padding(end = 10.dp)
                            ,
                            horizontalAlignment = Alignment.End
                        ) {
                            ClickableText(
                                text = stringResource(R.string.get_system_timezone_offset),
                                modifier = MyStyleKt.ClickableText.modifier.clickable {
                                    try {
                                        timeZone_offsetInMinuteBuf.value = AppModel.getSystemTimeZoneOffsetInMinutesCached().toString()
                                    } catch (e: Exception) {
                                        Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                                        MyLog.e(TAG, "#SetTimeZoneOffsetDialog: get system time zone offset err: ${e.stackTraceToString()}")
                                    }
                                },
                                fontWeight = FontWeight.Light
                            )
                            Spacer(Modifier.height(15.dp))
                            ClickableText(
                                text = stringResource(R.string.clear),
                                modifier = MyStyleKt.ClickableText.modifier.clickable {
                                    timeZone_offsetInMinuteBuf.value = ""
                                },
                                fontWeight = FontWeight.Light
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            },
            okBtnText = stringResource(id = R.string.save),
            cancelBtnText = stringResource(id = R.string.cancel),
            onCancel = { showSetTimeZoneDialog.value = false }
        ) {
            showSetTimeZoneDialog.value = false
            doJobThenOffLoading {
                val newOffset = try {
                    val offsetMinutes = timeZone_offsetInMinuteBuf.value.trim().toInt()
                    if(isValidOffsetInMinutes(offsetMinutes)) {
                        offsetMinutes.toString()
                    }else {
                        Msg.requireShowLongDuration("invalid offset, should in ${getValidTimeZoneOffsetRangeInMinutes()}")
                        val errMsg = getInvalidTimeZoneOffsetErrMsg(offsetMinutes)
                        MyLog.e(TAG, "user input invalid timezone offset: $errMsg")
                        throw RuntimeException(errMsg)
                    }
                }catch (_:Exception) {
                    ""
                }
                val newFollowSystem = timeZone_followSystemBuf.value
                if(newOffset != timeZone_offsetInMinute.value || newFollowSystem != timeZone_followSystem.value) {
                    timeZone_offsetInMinute.value = newOffset
                    timeZone_followSystem.value = newFollowSystem
                    val settingsUpdated = SettingsUtil.update(requireReturnSnapshotOfUpdatedSettings = true) {
                        it.timeZone.offsetInMinutes = newOffset
                        it.timeZone.followSystem = newFollowSystem
                    }!!
                    AppModel.reloadTimeZone(settingsUpdated)
                }
            }
        }
    }
    val oldMasterPassword = rememberSaveable { mutableStateOf("") }
    val newMasterPassword = rememberSaveable { mutableStateOf("") }
    val oldMasterPasswordErrMsg = rememberSaveable { mutableStateOf("") }
    val newMasterPasswordErrMsg = rememberSaveable { mutableStateOf("") }
    val oldMasterPasswordVisible = rememberSaveable { mutableStateOf(false) }
    val newMasterPasswordVisible = rememberSaveable { mutableStateOf(false) }
    val masterPassEnabled = rememberSaveable { mutableStateOf(AppModel.masterPasswordEnabled()) }
    val masterPassStatus = rememberSaveable { mutableStateOf(if(masterPassEnabled.value) activityContext.getString(R.string.enabled) else activityContext.getString(R.string.disabled)) }
    val showSetMasterPasswordDialog = rememberSaveable { mutableStateOf(false) }
    if (showSetMasterPasswordDialog.value)  {
        val requireOldPass = masterPassEnabled.value  
        ConfirmDialog2(
            title = stringResource(R.string.set_master_password),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    if(requireOldPass) {
                        PasswordTextFiled(oldMasterPassword, oldMasterPasswordVisible, stringResource(R.string.old_password), errMsg = oldMasterPasswordErrMsg)
                    }
                    PasswordTextFiled(newMasterPassword, newMasterPasswordVisible, stringResource(R.string.new_password), errMsg = newMasterPasswordErrMsg)
                    MySelectionContainer {
                        Column {
                            DefaultPaddingText(stringResource(R.string.leave_new_password_empty_if_dont_want_to_use_master_password), color = MyStyleKt.TextColor.getHighlighting())
                            if(newMasterPassword.value.isNotEmpty()) {
                                Spacer(Modifier.height(10.dp))
                                DefaultPaddingText(stringResource(R.string.please_make_sure_you_can_remember_your_master_password), color = MyStyleKt.TextColor.danger())
                            }
                        }
                    }
                }
            },
            okBtnText = stringResource(R.string.save),
            onCancel = {showSetMasterPasswordDialog.value = false}
        ) {
            doJobThenOffLoading job@{
                try {
                    val oldPass  = if(requireOldPass) {  
                        if(oldMasterPassword.value.isEmpty()){
                            oldMasterPasswordErrMsg.value = activityContext.getString(R.string.require_old_password)
                            return@job
                        }
                        if(HashUtil.verify(oldMasterPassword.value, settingsState.value.masterPasswordHash).not()) {
                            oldMasterPasswordErrMsg.value = activityContext.getString(R.string.wrong_password)
                            return@job
                        }
                        oldMasterPassword.value
                    }else {  
                        AppModel.masterPassword.value
                    }
                    showSetMasterPasswordDialog.value = false
                    val updatingStr = activityContext.getString(R.string.updating)
                    Msg.requireShow(updatingStr)
                    val newPass = newMasterPassword.value
                    if(newPass == oldPass) {
                        Msg.requireShow(activityContext.getString(R.string.old_and_new_passwords_are_the_same))
                        return@job
                    }
                    if(!MasterPassUtil.goodToSave(newPass)) {
                        Msg.requireShow(activityContext.getString(R.string.encode_new_password_failed_plz_try_another_one))
                        return@job
                    }
                    masterPassStatus.value = updatingStr
                    val credentialDb = AppModel.dbContainer.credentialRepository
                    val failedList = credentialDb.updateMasterPassword(oldPass, newPass)
                    val newSettings = MasterPassUtil.save(AppModel.realAppContext, newPass)
                    if(failedList.isEmpty()) { 
                        Msg.requireShow(activityContext.getString(R.string.success))
                    }else {  
                        val suffix = ", "
                        val sb = StringBuilder()
                        for (i in failedList) {
                            sb.append(i).append(suffix)
                        }
                        updateMasterPassFailedListStr.value = sb.removeSuffix(suffix).toString()
                        showFailedUpdateMasterPasswordsCredentialList.value = true
                    }
                    masterPassEnabled.value = AppModel.masterPasswordEnabled()
                    masterPassStatus.value = activityContext.getString(if(masterPassEnabled.value) { if(requireOldPass) R.string.updated else R.string.enabled } else R.string.disabled)
                    settingsState.value = newSettings
                }catch (e:Exception) {
                    Msg.requireShowLongDuration("err: "+e.localizedMessage)
                    MyLog.e(TAG, "SetMasterPasswordDialog err: ${e.stackTraceToString()}")
                }
            }
        }
    }
    val showClearMasterPasswordDialog = rememberSaveable { mutableStateOf(false) }
    if(showClearMasterPasswordDialog.value) {
        ClearMasterPasswordDialog(
            onCancel = {showClearMasterPasswordDialog.value = false},
            onOk = {
                showClearMasterPasswordDialog.value = false
                masterPassEnabled.value = false
                masterPassStatus.value = activityContext.getString(R.string.disabled)
                Msg.requireShow(activityContext.getString(R.string.success))
            }
        )
    }
    if(showCleanDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.clean),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {
                    MyCheckBox(stringResource(R.string.log), cleanLog)
                    MyCheckBox(stringResource(R.string.cache), cleanCacheFolder)
                    MyCheckBox(stringResource(R.string.edit_cache), cleanEditCache)
                    MyCheckBox(stringResource(R.string.all_snapshots), cleanSnapshot)  
                    MyCheckBox(stringResource(R.string.storage_paths), cleanStoragePath)
                    if(cleanStoragePath.value) {
                        MySelectionContainer {
                            DefaultPaddingText(stringResource(R.string.the_storage_path_are_the_paths_you_chosen_and_added_when_cloning_repo))
                        }
                    }
                    MyCheckBox(stringResource(R.string.file_opened_history), cleanFileOpenHistory)
                    if(cleanFileOpenHistory.value) {
                        MySelectionContainer {
                            DefaultPaddingText(stringResource(R.string.this_include_editor_opened_files_history_and_their_last_edited_position))
                        }
                    }
                }
            },
            okBtnText = stringResource(R.string.clean),
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showCleanDialog.value = false}
        ) {
            showCleanDialog.value=false
            doJobThenOffLoading {
                Msg.requireShow(activityContext.getString(R.string.cleaning))
                if(cleanLog.value) {
                    try {
                        AppModel.getOrCreateLogDir().deleteRecursively()
                        AppModel.getOrCreateLogDir()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean log err: ${e.stackTraceToString()}")
                    }
                }
                if(cleanCacheFolder.value) {
                    try {
                        AppModel.externalCacheDir.deleteRecursively()
                        AppModel.externalCacheDir.mkdirs()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean external cache dir err: ${e.stackTraceToString()}")
                    }
                    try {
                        AppModel.innerCacheDir?.deleteRecursively()
                        AppModel.innerCacheDir?.mkdirs()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean inner cache dir err: ${e.stackTraceToString()}")
                    }
                }
                if(cleanEditCache.value) {
                    try {
                        AppModel.getOrCreateEditCacheDir().deleteRecursively()
                        AppModel.getOrCreateEditCacheDir()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean edit cache err: ${e.stackTraceToString()}")
                    }
                }
                if(cleanSnapshot.value) {
                    try {
                        AppModel.getOrCreateFileSnapshotDir().deleteRecursively()
                        AppModel.getOrCreateFileSnapshotDir()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean file and content snapshot err: ${e.stackTraceToString()}")
                    }
                }
                if(cleanStoragePath.value) {
                    try {
                        StoragePathsMan.reset()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean storage paths err: ${e.stackTraceToString()}")
                    }
                }
                if(cleanFileOpenHistory.value) {
                    try {
                        FileOpenHistoryMan.reset()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "clean file opened history err: ${e.stackTraceToString()}")
                    }
                }
                Msg.requireShow(activityContext.getString(R.string.done))
            }
        }
    }
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true)}
    val backHandlerOnBack = ComposeHelper.getDoubleClickBackHandler(context = activityContext, openDrawer = openDrawer, exitApp= exitApp)
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {backHandlerOnBack()})
    val itemFontSize = MyStyleKt.SettingsItem.itemFontSize
    val itemDescFontSize = MyStyleKt.SettingsItem.itemDescFontSize
    val trailFolderIcon = Icons.Filled.Folder
    val trailFolderIconTooltipText = stringResource(R.string.show_in_files)
    @Composable
    fun TwoLineTrailingFolderItem(text1:String, text2:String, trailIconOnClick:()->Unit) {
        TwoLineSettingsItem(
            text1 = text1,
            text1FontSize = itemFontSize,
            text2 = text2,
            text2FontSize = itemDescFontSize,
            trailIcon = trailFolderIcon,
            trailIconTooltipText = trailFolderIconTooltipText,
            trailIconWidth = trailIconWidth,
            trailIconOnClick = trailIconOnClick
        )
    }
    Column(
        modifier = Modifier
            .baseVerticalScrollablePageModifier(contentPadding, listState)
    ) {
        SettingsTitle(stringResource(R.string.general))
        SettingsContentSelector(
            left = {
                Text(stringResource(R.string.theme), fontSize = itemFontSize)
            },
            right = {
                SingleSelectList(
                    optionsList = themeList,
                    selectedOptionIndex = null,
                    selectedOptionValue = selectedTheme.intValue,
                    menuItemSelected = {_, value -> value == selectedTheme.intValue },
                    menuItemFormatter = {_, value -> Theme.getThemeTextByCode(value, activityContext)},
                    menuItemOnClick = { _, value ->
                        selectedTheme.intValue = value
                        Theme.updateThemeValue(activityContext, value)
                    }
                )
            }
        )
        SettingsContentSelector(
            left = {
                Text(stringResource(R.string.language), fontSize = itemFontSize)
                Text(stringResource(R.string.require_restart_app), fontSize = itemDescFontSize, fontWeight = FontWeight.Light, fontStyle = FontStyle.Italic)
            },
            right = {
                SingleSelectList(
                    optionsList = languageList,
                    selectedOptionIndex = null,
                    selectedOptionValue = selectedLanguage.value,
                    menuItemOnClick = { index, value ->
                        selectedLanguage.value = value
                        if(value != LanguageUtil.getLangCode(activityContext)) {
                            LanguageUtil.setLangCode(activityContext, value)
                        }
                    },
                    menuItemSelected = {index, value ->
                        value == selectedLanguage.value
                    },
                    menuItemFormatter = { index, value ->
                        LanguageUtil.getLanguageTextByCode(value?:"", activityContext)
                    }
                )
            }
        )
        SettingsContentSelector(
            left = {
                TwoLineTrailingFolderItem(
                    text1 = stringResource(R.string.log_level),
                    text2 = "",
                    trailIconOnClick = {
                        goToFilesPage(AppModel.getOrCreateLogDir().canonicalPath)
                    }
                )
            },
            right = {
                SingleSelectList(
                    optionsList = logLevelList,
                    selectedOptionIndex = null,
                    selectedOptionValue = selectedLogLevel.value,
                    menuItemOnClick = { index, value ->
                        selectedLogLevel.value = value
                        if(value != MyLog.getCurrentLogLevel()) {
                            MyLog.setLogLevel(value.get(0))
                            PrefMan.set(activityContext, PrefMan.Key.logLevel, value)
                        }
                    },
                    menuItemSelected = {index, value ->
                        value == selectedLogLevel.value
                    },
                    menuItemFormatter = { index, value ->
                        MyLog.getTextByLogLevel(value?:"", activityContext)
                    }
                )
            }
        )
        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.dynamic_color_scheme), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = Theme.dynamicColor.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                Theme.updateDynamicColor(activityContext, !Theme.dynamicColor.value)
            }
        )
        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.dev_mode), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = devModeOn.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !devModeOn.value
                devModeOn.value = newValue
                AppModel.devModeOn = newValue
                PrefUtil.setDevMode(activityContext, newValue)
            }
        )
        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.go_to_top_bottom_buttons), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = showNaviButtons.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !showNaviButtons.value
                showNaviButtons.value = newValue
                SettingsUtil.update {
                    it.showNaviButtons = newValue
                }
            }
        )
        SettingsContent(onClick = {
            initSetTimeZoneDialog()
        }) {
            Column {
                Text(stringResource(R.string.timezone), fontSize = itemFontSize)
                Text(getTimeZoneStr(), fontSize = itemDescFontSize, fontWeight = FontWeight.Light, color = MyStyleKt.TextColor.getHighlighting())
            }
        }
        SettingsContent(onClick = {
            showCommitMsgTemplateDialog.value = true
        }) {
            Text(stringResource(R.string.commit_msg_template), fontSize = itemFontSize)
        }
        SettingsContent(onClick = {
            showCleanDialog.value = true
        }) {
            Text(stringResource(R.string.clean), fontSize = itemFontSize)
        }
        SettingsTitle(stringResource(R.string.editor))
        SettingsContent(onClick = {
            initSetFileAssociationDialog()
        }) {
            Text(stringResource(R.string.file_association), fontSize = itemFontSize)
        }
        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.syntax_highlighting), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = syntaxHighlightEnabled.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !syntaxHighlightEnabled.value
                syntaxHighlightEnabled.value = newValue
                SettingsUtil.update {
                    it.editor.syntaxHighlightEnabled = newValue
                }
            }
        )
        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.use_system_fonts), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = useSystemFonts.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !useSystemFonts.value
                useSystemFonts.value = newValue
                SettingsUtil.update {
                    it.editor.useSystemFonts = newValue
                }
            }
        )
        SettingsContentSwitcher(
            left = {
                TwoLineTrailingFolderItem(
                    text1 = stringResource(R.string.file_snapshot),
                    text2 = stringResource(R.string.file_snapshot_desc),
                    trailIconOnClick = {
                        goToFilesPage(AppModel.getOrCreateFileSnapshotDir().canonicalPath)
                    }
                )
            },
            right = {
                Switch(
                    checked = enableSnapshot_File.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !enableSnapshot_File.value
                enableSnapshot_File.value = newValue
                SettingsUtil.update {
                    it.editor.enableFileSnapshot = newValue
                }
                SnapshotUtil.update_enableFileSnapshotForEditor(newValue)
            }
        )
        SettingsContentSwitcher(
            left = {
                TwoLineTrailingFolderItem(
                    text1 = stringResource(R.string.content_snapshot),
                    text2 = stringResource(R.string.content_snapshot_desc),
                    trailIconOnClick = {
                        goToFilesPage(AppModel.getOrCreateFileSnapshotDir().canonicalPath)
                    }
                )
            },
            right = {
                Switch(
                    checked = enableSnapshot_Content.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !enableSnapshot_Content.value
                enableSnapshot_Content.value = newValue
                SettingsUtil.update {
                    it.editor.enableContentSnapshot = newValue
                }
                SnapshotUtil.update_enableContentSnapshotForEditor(newValue)
            }
        )
        SettingsContentSwitcher(
            left = {
                TwoLineTrailingFolderItem(
                    text1 = stringResource(R.string.edit_cache),
                    text2 = replaceStringResList(stringResource(R.string.cache_your_input_into_editcache_dir_path), listOf("${Cons.defalutPuppyGitDataUnderAllReposDirName}/${Cons.defaultEditCacheDirName}")),
                    trailIconOnClick = {
                        goToFilesPage(AppModel.getOrCreateEditCacheDir().canonicalPath)
                    }
                )
            },
            right = {
                Switch(
                    checked = enableEditCache.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !enableEditCache.value
                enableEditCache.value = newValue
                val settings = SettingsUtil.update(requireReturnSnapshotOfUpdatedSettings = true) {
                    it.editor.editCacheEnable = newValue
                }!!
                EditCache.init(enableCache = newValue, cacheDir = AppModel.getOrCreateEditCacheDir(), keepInDays = settings.editor.editCacheKeepInDays)
            }
        )
        SettingsTitle(stringResource(R.string.diff))
        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.syntax_highlighting), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = syntaxHighlightEnabled_DiffScreen.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !syntaxHighlightEnabled_DiffScreen.value
                syntaxHighlightEnabled_DiffScreen.value = newValue
                SettingsUtil.update {
                    it.diff.syntaxHighlightEnabled = newValue
                }
            }
        )
        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.use_system_fonts), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = useSystemFonts_DiffScreen.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !useSystemFonts_DiffScreen.value
                useSystemFonts_DiffScreen.value = newValue
                SettingsUtil.update {
                    it.diff.useSystemFonts = newValue
                }
            }
        )
        SettingsContentSwitcher(
            left = {
                TwoLineTrailingFolderItem(
                    text1 = stringResource(R.string.file_snapshot),
                    text2 = stringResource(R.string.file_snapshot_desc),
                    trailIconOnClick = {
                        goToFilesPage(AppModel.getOrCreateFileSnapshotDir().canonicalPath)
                    }
                )
            },
            right = {
                Switch(
                    checked = diff_CreateSnapShotForOriginFileBeforeSave.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !diff_CreateSnapShotForOriginFileBeforeSave.value
                diff_CreateSnapShotForOriginFileBeforeSave.value = newValue
                SettingsUtil.update {
                    it.diff.createSnapShotForOriginFileBeforeSave = newValue
                }
                SnapshotUtil.update_enableFileSnapshotForDiff(newValue)
            }
        )
        SettingsTitle(StrCons.git)
        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.pull_with_rebase), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = pullWithRebase.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !pullWithRebase.value
                pullWithRebase.value = newValue
                SettingsUtil.update {
                    it.globalGitConfig.pullWithRebase = newValue
                }
            }
        )
        SettingsTitle(stringResource(R.string.ssh))
        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.allow_unknown_hosts), fontSize = itemFontSize)
                Text(stringResource(R.string.if_enable_will_allow_unknown_hosts_as_default_else_will_ask), fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
            },
            right = {
                Switch(
                    checked = allowUnknownHosts.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !allowUnknownHosts.value
                allowUnknownHosts.value = newValue
                SettingsUtil.update {
                    it.sshSetting.allowUnknownHosts = newValue
                }
            }
        )
        SettingsContent(onClick = {
            showForgetHostKeysDialog.value = true
        }) {
            Column {
                Text(stringResource(R.string.forget_hostkeys), fontSize = itemFontSize)
            }
        }
        SettingsTitle(stringResource(R.string.http_https))
        SettingsContentSwitcher(
            left = {
                Text(stringResource(R.string.ssl_verify), fontSize = itemFontSize)
            },
            right = {
                Switch(
                    checked = httpSslVerify.value,
                    onCheckedChange = null
                )
            },
            onClick = {
                val newValue = !httpSslVerify.value
                httpSslVerify.value = newValue
                SettingsUtil.update {
                    it.httpSetting.sslVerify = newValue
                }
            }
        )
        SettingsContent(onClick = {
            showImportSslCertsDialog.value = true
        }) {
            Column {
                Text(stringResource(R.string.import_ssl_certs), fontSize = itemFontSize)
            }
        }
        SettingsContent(onClick = {
            goToFilesPage(AppModel.certUserDir.canonicalPath)
        }) {
            Column {
                Text(stringResource(R.string.manage_ssl_certs), fontSize = itemFontSize)
            }
        }
        SettingsTitle(stringResource(R.string.master_password))
        SettingsContent(onClick = {
            oldMasterPassword.value = ""
            newMasterPassword.value = ""
            oldMasterPasswordErrMsg.value = ""
            newMasterPasswordErrMsg.value = ""
            oldMasterPasswordVisible.value = false
            newMasterPasswordVisible.value = false
            showSetMasterPasswordDialog.value = true
        }) {
            Column {
                Text(stringResource(R.string.set_master_password), fontSize = itemFontSize)
                Text(masterPassStatus.value, fontSize = itemDescFontSize, fontWeight = FontWeight.Light, color = if(masterPassEnabled.value) MyStyleKt.TextColor.getHighlighting() else MyStyleKt.TextColor.danger())
                Text(stringResource(R.string.if_set_will_require_master_password_when_launching_app), fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
            }
        }
        if(masterPassEnabled.value) {
            SettingsContent(onClick = {
                showClearMasterPasswordDialog.value = true
            }) {
                Column {
                    Text(stringResource(R.string.i_forgot_my_master_password), fontSize = itemFontSize)
                }
            }
        }
        SettingsTitle(stringResource(R.string.permissions))
        SettingsContent(onClick = {
            ActivityUtil.getManageStoragePermissionOrShowFailedMsg(activityContext)
        }) {
            Column {
                Text(stringResource(R.string.manage_storage), fontSize = itemFontSize)
                Text(stringResource(R.string.if_you_want_to_clone_repo_into_external_storage_this_permission_is_required), fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
            }
        }
        if(devModeOn.value) {
            SettingsTitle("Dev Zone")
            DevFeature.settingsItemList.forEachBetter {
                DevBooleanSettingsItem(
                    item = it,
                    context = activityContext,
                    itemFontSize = itemFontSize,
                    itemDescFontSize = itemDescFontSize,
                )
            }
            SettingsContent(onClick = {
                throw RuntimeException("App Crashed For Test Purpose")
            }) {
                Column {
                    Text("Crash App", fontSize = itemFontSize)
                }
            }
        }
        SpacerRow()
    }
    LaunchedEffect(needRefreshPage.value) {
        settingsState.value = SettingsUtil.getSettingsSnapshot()
    }
    SoftkeyboardVisibleListener(
        view = view,
        isKeyboardVisible = isKeyboardVisible,
        isKeyboardCoveredComponent = isKeyboardCoveredComponent,
        componentHeight = componentHeight,
        keyboardPaddingDp = keyboardPaddingDp,
        density = density,
        skipCondition = {
            showSetFileAssociationDialog.value.not()
        }
    )
}
@Composable
private fun DevBooleanSettingsItem(
    item: DevItem<Boolean>,
    context: Context,
    itemFontSize: TextUnit,
    itemDescFontSize: TextUnit,
) {
    SettingsContentSwitcher(
        left = {
            Text(item.text, fontSize = itemFontSize)
            item.desc.let {
                if(it.isNotBlank()) {
                    Text(it, fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
                }
            }
        },
        right = {
            Switch(
                checked = item.state.value,
                onCheckedChange = null
            )
        },
        onClick = {
            item.update(!item.state.value, context)
        }
    )
}
