package com.akcreation.gitsilent.screen.content.homescreen.innerpage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.ConfirmDialog2
import com.akcreation.gitsilent.compose.InLineCopyIcon
import com.akcreation.gitsilent.compose.MySelectionContainer
import com.akcreation.gitsilent.compose.PullToRefreshBox
import com.akcreation.gitsilent.compose.SettingsContent
import com.akcreation.gitsilent.compose.SettingsContentSwitcher
import com.akcreation.gitsilent.compose.SettingsTitle
import com.akcreation.gitsilent.compose.SoftkeyboardVisibleListener
import com.akcreation.gitsilent.compose.SpacerRow
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.server.isHttpServerOnline
import com.akcreation.gitsilent.service.HttpService
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.ActivityUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.ComposeHelper
import com.akcreation.gitsilent.utils.Msg
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.StrListUtil
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.changeStateTriggerRefreshPage
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.genHttpHostPortStr
import com.akcreation.gitsilent.utils.parseIntOrDefault
import com.akcreation.gitsilent.utils.state.mutableCustomStateListOf
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "ServiceInnerPage"
@Composable
fun ServiceInnerPage(
    stateKeyTag:String,
    contentPadding: PaddingValues,
    needRefreshPage:MutableState<String>,
    openDrawer:()->Unit,
    exitApp:()->Unit,
    listState:ScrollState
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
    val scope = rememberCoroutineScope()
    val settingsState = mutableCustomStateOf(stateKeyTag, "settingsState", SettingsUtil.getSettingsSnapshot())
    val runningStatus = rememberSaveable { mutableStateOf(HttpService.isRunning()) }
    val updateRunningStatus = { runningStatus.value = HttpService.isRunning() }
    val launchOnAppStartup = rememberSaveable { mutableStateOf(settingsState.value.httpService.launchOnAppStartup) }
    val launchOnSystemStartUp = rememberSaveable { mutableStateOf(HttpService.launchOnSystemStartUpEnabled(activityContext)) }
    val progressNotify = rememberSaveable { mutableStateOf(settingsState.value.httpService.showNotifyWhenProgress) }
    val ipWhitelist = mutableCustomStateListOf(stateKeyTag, "ipWhitelist") { settingsState.value.httpService.ipWhiteList }
    val ipWhitelistBuf = rememberSaveable { mutableStateOf("") }
    val showSetIpWhiteListDialog = rememberSaveable { mutableStateOf(false) }
    val initSetIpWhitelistDialog = {
        ipWhitelistBuf.value = StrListUtil.listToLines(ipWhitelist.value)
        showSetIpWhiteListDialog.value = true
    }
    if(showSetIpWhiteListDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.ip_whitelist),
            requireShowTextCompose = true,
            textCompose =  {
                Column(
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        componentHeight.intValue = layoutCoordinates.size.height
                    }
                ) {
                    MySelectionContainer {
                        Column {
                            Text(stringResource(R.string.per_line_one_ip), fontWeight = FontWeight.Light)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(stringResource(R.string.if_empty_will_reject_all_requests), fontWeight = FontWeight.Light)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(stringResource(R.string.use_asterisk_to_match_all_ips), fontWeight = FontWeight.Light)
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                            ),
                        value = ipWhitelistBuf.value,
                        onValueChange = {
                            ipWhitelistBuf.value = it
                        },
                        label = {
                            Text(stringResource(R.string.list_of_ips))
                        },
                    )
                    Spacer(Modifier.height(10.dp))
                }
            },
            okBtnText = stringResource(id = R.string.save),
            cancelBtnText = stringResource(id = R.string.cancel),
            onCancel = { showSetIpWhiteListDialog.value = false }
        ) {
            showSetIpWhiteListDialog.value = false
            doJobThenOffLoading {
                val newValue = ipWhitelistBuf.value
                val newList = StrListUtil.linesToList(newValue)
                ipWhitelist.value.clear()
                ipWhitelist.value.addAll(newList)
                SettingsUtil.update {
                    it.httpService.ipWhiteList = newList
                }
                Msg.requireShow(activityContext.getString(R.string.success))
            }
        }
    }
    val tokenList = mutableCustomStateListOf(stateKeyTag, "tokenList") { settingsState.value.httpService.tokenList }
    val tokenListBuf = rememberSaveable { mutableStateOf("") }
    val showSetTokenListDialog = rememberSaveable { mutableStateOf(false) }
    val initSetTokenListDialog = {
        tokenListBuf.value = StrListUtil.listToLines(tokenList.value)
        showSetTokenListDialog.value = true
    }
    if(showSetTokenListDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.tokens),
            requireShowTextCompose = true,
            textCompose =  {
                Column(
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        componentHeight.intValue = layoutCoordinates.size.height
                    }
                ) {
                    MySelectionContainer {
                        Column {
                            Text(stringResource(R.string.per_line_one_token), fontWeight = FontWeight.Light)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(stringResource(R.string.if_empty_will_reject_all_requests), fontWeight = FontWeight.Light)
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isKeyboardCoveredComponent.value) Modifier.padding(bottom = keyboardPaddingDp.intValue.dp) else Modifier
                            ),
                        value = tokenListBuf.value,
                        onValueChange = {
                            tokenListBuf.value = it
                        },
                        label = {
                            Text(stringResource(R.string.list_of_tokens))
                        },
                    )
                    Spacer(Modifier.height(10.dp))
                }
            },
            okBtnText = stringResource(id = R.string.save),
            cancelBtnText = stringResource(id = R.string.cancel),
            onCancel = { showSetTokenListDialog.value = false }
        ) {
            showSetTokenListDialog.value = false
            doJobThenOffLoading {
                val newValue = tokenListBuf.value
                val newList = StrListUtil.linesToList(newValue)
                tokenList.value.clear()
                tokenList.value.addAll(newList)
                SettingsUtil.update {
                    it.httpService.tokenList = newList
                }
                Msg.requireShow(activityContext.getString(R.string.success))
            }
        }
    }
    val listenHost = rememberSaveable { mutableStateOf(settingsState.value.httpService.listenHost) }
    val listenHostBuf = mutableCustomStateOf(stateKeyTag, "listenHostBuf") { TextFieldValue("") }
    val showSetHostDialog = rememberSaveable { mutableStateOf(false) }
    val initSetHostDialog = {
        listenHostBuf.value = listenHost.value.let { TextFieldValue(text = it, selection = TextRange(0, it.length)) }
        showSetHostDialog.value=true
    }
    if(showSetHostDialog.value) {
        val focusRequester = remember { FocusRequester() }
        ConfirmDialog2(title = stringResource(R.string.host),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    modifier= Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .verticalScroll(rememberScrollState())
                    ,
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MyStyleKt.defaultItemPadding),
                        singleLine = true,
                        value = listenHostBuf.value,
                        onValueChange = {
                            listenHostBuf.value = it
                        },
                        label = {
                            Text(stringResource(R.string.host))
                        },
                    )
                }
            },
            okBtnText = stringResource(id = R.string.save),
            cancelBtnText = stringResource(id = R.string.cancel),
            onCancel = { showSetHostDialog.value = false }
        ) {
            showSetHostDialog.value = false
            doJobThenOffLoading {
                val newValue = listenHostBuf.value.text
                if(newValue.isBlank()) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.err_host_invalid))
                    return@doJobThenOffLoading
                }
                listenHost.value = newValue
                SettingsUtil.update {
                    it.httpService.listenHost = newValue
                }
                Msg.requireShow(activityContext.getString(R.string.saved))
            }
        }
        LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    }
    val listenPort = rememberSaveable { mutableStateOf(settingsState.value.httpService.listenPort.toString()) }
    val listenPortBuf = mutableCustomStateOf(stateKeyTag, "listenPortBuf") { TextFieldValue("") }
    val showSetPortDialog = rememberSaveable { mutableStateOf(false) }
    val initSetPortDialog = {
        listenPortBuf.value = listenPort.value.let { TextFieldValue(text = it, selection = TextRange(0, it.length)) }
        showSetPortDialog.value = true
    }
    if(showSetPortDialog.value) {
        val focusRequester = remember { FocusRequester() }
        ConfirmDialog2(title = stringResource(R.string.port),
            requireShowTextCompose = true,
            textCompose = {
                Column(
                    modifier= Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .verticalScroll(rememberScrollState())
                    ,
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MyStyleKt.defaultItemPadding),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = listenPortBuf.value,
                        onValueChange = {
                            listenPortBuf.value = it
                        },
                        label = {
                            Text(stringResource(R.string.port))
                        },
                    )
                }
            },
            okBtnText = stringResource(id = R.string.save),
            cancelBtnText = stringResource(id = R.string.cancel),
            onCancel = { showSetPortDialog.value = false }
        ) {
            showSetPortDialog.value = false
            doJobThenOffLoading {
                val newValue = parseIntOrDefault(listenPortBuf.value.text, default = null)
                if(newValue == null || newValue < 0 || newValue > 65535) {
                    Msg.requireShowLongDuration(activityContext.getString(R.string.err_port_invalid))
                    return@doJobThenOffLoading
                }
                listenPort.value = newValue.toString()
                SettingsUtil.update {
                    it.httpService.listenPort = newValue
                }
                Msg.requireShow(activityContext.getString(R.string.saved))
            }
        }
        LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    }
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true)}
    val backHandlerOnBack = ComposeHelper.getDoubleClickBackHandler(context = activityContext, openDrawer = openDrawer, exitApp= exitApp)
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {backHandlerOnBack()})
    val itemFontSize = MyStyleKt.SettingsItem.itemFontSize
    val itemDescFontSize = MyStyleKt.SettingsItem.itemDescFontSize
    PullToRefreshBox(
        contentPadding = contentPadding,
        onRefresh = { changeStateTriggerRefreshPage(needRefreshPage) }
    ) {
        Column(
            modifier = Modifier
                .baseVerticalScrollablePageModifier(contentPadding, listState)
        ) {
            SettingsContentSwitcher(
                left = {
                    val runningStatus = runningStatus.value
                    Text(stringResource(R.string.status), fontSize = itemFontSize)
                    Text(
                        text = UIHelper.getRunningStateText(activityContext, runningStatus),
                        fontSize = itemDescFontSize,
                        fontWeight = FontWeight.Light,
                        color = UIHelper.getRunningStateColor(runningStatus)
                    )
                },
                right = {
                    Switch(
                        checked = runningStatus.value,
                        onCheckedChange = null
                    )
                },
                onClick = {
                    val newValue = !runningStatus.value
                    if(newValue) {
                        HttpService.start(AppModel.realAppContext)
                    }else {
                        HttpService.stop(AppModel.realAppContext)
                    }
                    runningStatus.value = newValue
                }
            )
            SettingsContent(
                onClick = {
                    doJobThenOffLoading {
                        val requestRet = isHttpServerOnline(host = listenHost.value, port = listenPort.value)
                        if(requestRet.hasError()) {
                            Msg.requireShow(requestRet.msg)
                        }else {
                            Msg.requireShow(activityContext.getString(R.string.success))
                        }
                    }
                }
            ) {
                Column {
                    Text(stringResource(R.string.test), fontSize = itemFontSize)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(genHttpHostPortStr(listenHost.value, listenPort.value), fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
                        InLineCopyIcon {
                            clipboardManager.setText(AnnotatedString(genHttpHostPortStr(listenHost.value, listenPort.value)))
                            Msg.requireShow(activityContext.getString(R.string.copied))
                        }
                    }
                }
            }
            SettingsContent(onClick = {
                try {
                    ActivityUtil.openThisAppInfoPage(activityContext)
                }catch (e:Exception) {
                    Msg.requireShowLongDuration("err: ${e.localizedMessage}")
                    MyLog.d(TAG, "call `ActivityUtil.openThisAppInfoPage(activityContext)` from Service Page err: ${e.stackTraceToString()}")
                }
            }) {
                Column {
                    Text(stringResource(R.string.app_info), fontSize = itemFontSize)
                    Text(stringResource(R.string.intro_go_to_app_info_to_allow_autostart_and_disable_battery_optimization), fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
                }
            }
            SettingsContent(onClick = {
                ActivityUtil.openUrl(activityContext, httpServiceApiUrl)
            }) {
                Column {
                    Text(stringResource(R.string.document), fontSize = itemFontSize)
                }
            }
            SettingsTitle(stringResource(R.string.settings))
            SettingsContent(onClick = {
                initSetHostDialog()
            }) {
                Column {
                    Text(stringResource(R.string.host), fontSize = itemFontSize)
                    Text(listenHost.value, fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
                    Text(stringResource(R.string.require_restart_service), fontSize = itemDescFontSize, fontWeight = FontWeight.Light, fontStyle = FontStyle.Italic)
                }
            }
            SettingsContent(onClick = {
                initSetPortDialog()
            }) {
                Column {
                    Text(stringResource(R.string.port), fontSize = itemFontSize)
                    Text(listenPort.value, fontSize = itemDescFontSize, fontWeight = FontWeight.Light)
                    Text(stringResource(R.string.require_restart_service), fontSize = itemDescFontSize, fontWeight = FontWeight.Light, fontStyle = FontStyle.Italic)
                }
            }
            SettingsContent(onClick = {
                initSetTokenListDialog()
            }) {
                Column {
                    Text(stringResource(R.string.tokens), fontSize = itemFontSize)
                }
            }
            SettingsContent(onClick = {
                initSetIpWhitelistDialog()
            }) {
                Column {
                    Text(stringResource(R.string.ip_whitelist), fontSize = itemFontSize)
                }
            }
            SettingsContentSwitcher(
                left = {
                    Text(stringResource(R.string.launch_on_app_startup), fontSize = itemFontSize)
                },
                right = {
                    Switch(
                        checked = launchOnAppStartup.value,
                        onCheckedChange = null
                    )
                },
                onClick = {
                    val newValue = !launchOnAppStartup.value
                    launchOnAppStartup.value = newValue
                    SettingsUtil.update {
                        it.httpService.launchOnAppStartup = newValue
                    }
                }
            )
            SettingsContentSwitcher(
                left = {
                    Text(stringResource(R.string.launch_on_system_startup), fontSize = itemFontSize)
                },
                right = {
                    Switch(
                        checked = launchOnSystemStartUp.value,
                        onCheckedChange = null
                    )
                },
                onClick = {
                    val newValue = !launchOnSystemStartUp.value
                    launchOnSystemStartUp.value = newValue
                    HttpService.setLaunchOnSystemStartUp(activityContext, newValue)
                }
            )
            SettingsContentSwitcher(
                left = {
                    Text(stringResource(R.string.progress_notification), fontSize = itemFontSize)
                },
                right = {
                    Switch(
                        checked = progressNotify.value,
                        onCheckedChange = null
                    )
                },
                onClick = {
                    val newValue = !progressNotify.value
                    progressNotify.value = newValue
                    SettingsUtil.update {
                        it.httpService.showNotifyWhenProgress = newValue
                    }
                }
            )
            SpacerRow()
        }
    }
    LaunchedEffect(needRefreshPage.value) {
        settingsState.value = SettingsUtil.getSettingsSnapshot()
        updateRunningStatus()
    }
    LaunchedEffect(Unit) {
        scope.launch {
            runCatching {
                while (true) {
                    updateRunningStatus()
                    delay(1000)
                }
            }
        }
    }
    SoftkeyboardVisibleListener(
        view = view,
        isKeyboardVisible = isKeyboardVisible,
        isKeyboardCoveredComponent = isKeyboardCoveredComponent,
        componentHeight = componentHeight,
        keyboardPaddingDp = keyboardPaddingDp,
        density = density,
        skipCondition = {
            showSetTokenListDialog.value.not() && showSetIpWhiteListDialog.value.not()
        }
    )
}
