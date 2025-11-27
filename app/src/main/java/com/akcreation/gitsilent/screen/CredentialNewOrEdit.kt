package com.akcreation.gitsilent.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.LoadingDialog
import com.akcreation.gitsilent.compose.LongPressAbleIconBtn
import com.akcreation.gitsilent.compose.PasswordTextFiled
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.CredentialEntity
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.homescreen.scaffold.title.ScrollableTitle
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier
import com.akcreation.gitsilent.utils.cache.Cache
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.state.mutableCustomStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "CredentialNewOrEdit"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialNewOrEdit(
    credentialId: String?,  
    naviUp: () -> Unit,
) {
    val stateKeyTag = Cache.getSubPageKey(TAG)
    val activityContext = LocalContext.current
    val isEditMode = rememberSaveable { mutableStateOf(false)}
    val repoFromDb = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "repoFromDb", initValue = RepoEntity(id = ""))
    val homeTopBarScrollBehavior = AppModel.homeTopBarScrollBehavior
    val branch = rememberSaveable { mutableStateOf("")}
    val depth = rememberSaveable { mutableStateOf("")}  
    val credentialName = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "credentialName", initValue = TextFieldValue(""))
    val credentialVal = rememberSaveable { mutableStateOf("")}
    val credentialPass = rememberSaveable { mutableStateOf("")}
    val credentialType = rememberSaveable{mutableIntStateOf(Cons.dbCredentialTypeHttp)}
    val credentialInThisPage = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "credentialInThisPage", initValue = CredentialEntity(id = ""))
    val oldCredential = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "oldCredential", initValue = CredentialEntity(id = ""))
    val oldPassIsEmpty = rememberSaveable { mutableStateOf(false) }
    val focusRequesterCredentialName = remember { FocusRequester() }  
    val focusToNone = 0
    val focusToCredentialName = 3;
    val requireFocusTo = rememberSaveable{mutableIntStateOf(focusToNone)}  
    val httpOrHttps = stringResource(R.string.http_https)
    val ssh = stringResource(R.string.ssh)
    val optNumHttp = 0
    val optNumSsh = 1
    val radioOptions = listOf(httpOrHttps, ssh)  
    val credentialSelectedOption = rememberSaveable{mutableIntStateOf(optNumHttp)}
    val isReadyForSave = rememberSaveable { mutableStateOf(false)}
    val passwordVisible = rememberSaveable { mutableStateOf(false)}
    val showCredentialNameAlreadyExistsErr = rememberSaveable { mutableStateOf(false)}
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
    val setCredentialNameExistAndFocus:()->Unit = {
        showCredentialNameAlreadyExistsErr.value=true
        val text = credentialName.value.text
        credentialName.value = credentialName.value.copy(
            selection = TextRange(0, text.length)
        )
        requireFocusTo.intValue = focusToCredentialName
    }
    val showLoadingDialog = rememberSaveable { mutableStateOf(false)}
    val doSave:()->Unit = {
        doJobThenOffLoading launch@{
            showLoadingDialog.value=true
            val credentialDb = AppModel.dbContainer.credentialRepository
            val credentialNameText = credentialName.value.text
            val oldCredentialName = oldCredential.value.name
            if(!isEditMode.value || credentialNameText != oldCredentialName) {  
                val isCredentialNameExist = credentialDb.isCredentialNameExist(credentialNameText)
                if(isCredentialNameExist) {  
                    setCredentialNameExistAndFocus()  
                    showLoadingDialog.value=false
                    return@launch
                }
            }
            val credentialForSave = if(isEditMode.value) credentialInThisPage.value else CredentialEntity()
            credentialForSave.name = credentialNameText
            credentialForSave.value = credentialVal.value
            credentialForSave.pass = credentialPass.value
            credentialForSave.type = credentialType.intValue
            if(isEditMode.value) {
                val oldPass = oldCredential.value.pass
                val newPass = credentialForSave.pass
                if(oldPass != newPass) {  
                    credentialDb.encryptPassIfNeed(credentialForSave, AppModel.masterPassword.value)
                }
                credentialDb.update(credentialForSave)
            }else{  
                credentialDb.insertWithEncrypt(credentialForSave)
            }
            showLoadingDialog.value=false
            withContext(Dispatchers.Main) {
                naviUp()
            }
        }
    }
    val loadingText = rememberSaveable { mutableStateOf(activityContext.getString(R.string.loading))}
    val listState = rememberScrollState()
    val lastPosition = rememberSaveable { mutableStateOf(0) }
    Scaffold(
        modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = MyStyleKt.TopBar.getColors(),
                title = {
                    val titleText = if(isEditMode.value){
                        stringResource(R.string.edit_credential)
                    }else{
                        stringResource(R.string.new_credential)
                    }
                    ScrollableTitle(titleText, listState, lastPosition)
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
                        enabled = isReadyForSave.value,
                        ) {
                        doSave()
                    }
                },
                scrollBehavior = homeTopBarScrollBehavior,
            )
        },
    ){contentPadding->
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
                maxLines = MyStyleKt.defaultMultiLineTextFieldMaxLines,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MyStyleKt.defaultItemPadding)
                        ,
                value = credentialVal.value,
                onValueChange = {
                    credentialVal.value=it
                },
                label = {
                    Text(stringResource(R.string.username_or_private_key))
                },
            )
            PasswordTextFiled(
                password = credentialPass,
                label = stringResource(R.string.password_or_passphrase),
                passwordVisible = passwordVisible,
                canSwitchPasswordVisible = !isEditMode.value || oldPassIsEmpty.value
            )
            if(isEditMode.value && oldPassIsEmpty.value.not()) {
                Row(modifier = Modifier.padding(horizontal = 10.dp)) {
                    Text(
                        text = stringResource(R.string.don_t_touch_the_password_passphrase_if_you_don_t_want_to_update_it),
                        fontWeight = FontWeight.Light,
                        color = MyStyleKt.TextColor.getHighlighting()
                    )
                }
            }
        }
    }
    if(requireFocusTo.intValue==focusToCredentialName) {
        requireFocusTo.intValue=focusToNone
        focusRequesterCredentialName.requestFocus()
    }
    LaunchedEffect(Unit) {
        doJobThenOffLoading(
        ) job@{
            if (credentialId != null && credentialId.isNotBlank() && credentialId != "null") {  
                isEditMode.value = true
                val credentialDb = AppModel.dbContainer.credentialRepository
                credentialInThisPage.value = credentialDb.getById(credentialId)?:return@job
                oldCredential.value = credentialInThisPage.value.copy()  
                oldPassIsEmpty.value = oldCredential.value.pass.isEmpty()
                credentialType.intValue=credentialInThisPage.value.type
                credentialSelectedOption.intValue = if(credentialType.intValue == Cons.dbCredentialTypeHttp) optNumHttp else optNumSsh  
                credentialName.value = TextFieldValue(credentialInThisPage.value.name)
                credentialVal.value = credentialInThisPage.value.value;
                credentialPass.value = credentialInThisPage.value.pass
            } else {  
                isEditMode.value = false
                requireFocusTo.intValue = focusToCredentialName
            }
        }
    }
    isReadyForSave.value = (
            (!showCredentialNameAlreadyExistsErr.value)
           && (credentialName.value.text.isNotBlank())
            )
}
