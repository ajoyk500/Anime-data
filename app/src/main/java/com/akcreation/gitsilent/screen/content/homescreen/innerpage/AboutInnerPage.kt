package com.akcreation.gitsilent.screen.content.homescreen.innerpage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.compose.AppIcon
import com.akcreation.gitsilent.compose.AppIconMonoChrome
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.utils.ActivityUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.ComposeHelper
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier


const val authorMail = "luckyclover33xx@gmail.com"
const val authorMailLink = "mailto:$authorMail"

const val madeBy = "Made by Bandeapart1964 of catpuppyapp"
const val madeByLink = "https://github.com/Bandeapart1964"

const val sourceCodeLink = "https://github.com/catpuppyapp/PuppyGit"
const val privacyPolicyLink = "$sourceCodeLink/blob/main/PrivacyPolicy.md"
const val discussionLink = "$sourceCodeLink/discussions"
const val reportBugsLink = "$sourceCodeLink/issues/new"
const val faqLink = "$sourceCodeLink/blob/main/FAQ.md"
const val httpServiceApiUrl = "$sourceCodeLink/blob/main/http_service_api.md"
const val automationDocUrl = "$sourceCodeLink/blob/main/automation_doc.md"

const val donateLink = "https://github.com/catpuppyapp/PuppyGit/blob/main/donate.md"


private data class Link(
    val title: String,
    val link: String,
)


@Composable
fun AboutInnerPage(
    listState:ScrollState,
    contentPadding: PaddingValues,
    openDrawer:() -> Unit,
){

    val activityContext = LocalContext.current
    val exitApp = AppModel.exitApp

    val donateLink = Link(title = "💖 "+stringResource(R.string.donate)+" 💖", link = donateLink)


    //back handler block start
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true) }
    val backHandlerOnBack = ComposeHelper.getDoubleClickBackHandler(context = activityContext, openDrawer = openDrawer, exitApp= exitApp)
    //注册BackHandler，拦截返回键，实现双击返回和返回上级目录
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {backHandlerOnBack()})
    //back handler block end

    val appLogoEasterEggOn = rememberSaveable { mutableStateOf(false) }
    val appLogoEasterEggIconColor = remember { mutableStateOf(Color.Magenta) }

    Column(
        modifier = Modifier
            .baseVerticalScrollablePageModifier(contentPadding, listState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // App Icon etc
        CardContainer {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Icon with Easter Egg
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            if (appLogoEasterEggOn.value) {
                                appLogoEasterEggIconColor.value = UIHelper.randomRainbowColor()
                            } else {
                                appLogoEasterEggOn.value = true
                            }
                        },
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    if(appLogoEasterEggOn.value) {
                        AppIconMonoChrome(tint = appLogoEasterEggIconColor.value)
                    } else {
                        AppIcon()
                    }
                }

                Spacer(Modifier.height(24.dp))

                // App Name and Version
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = AppModel.getAppVersionNameAndCode(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                // Made By
                TextButton(
                    onClick = {
                        ActivityUtil.openUrl(activityContext, madeByLink)
                    }
                ) {
                    Text(
                        text = madeBy,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                    )
                }

                // donate link
                TextButton(
                    onClick = {
                        ActivityUtil.openUrl(activityContext, donateLink.link)
                    }
                ) {
                    Text(
                        text = donateLink.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = MyStyleKt.TextSize.medium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}


@Composable
private fun CardContainer(
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        content()
    }
}