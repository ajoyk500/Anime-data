package com.akcreation.gitsilent.screen.content.homescreen.innerpage

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.compose.AppIcon
import com.akcreation.gitsilent.compose.AppIconMonoChrome
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.utils.ActivityUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.ComposeHelper
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.baseVerticalScrollablePageModifier


const val authorMail = "luckyclover33xx@gmail.com"
const val authorMailLink = "mailto:$authorMail"

const val developerName = "Bandeapart1964"
const val developerTelegram = "https://t.me/bandeapart1964"

const val sourceCodeLink = "https://github.com/catpuppyapp/PuppyGit"
const val releasesLink = "$sourceCodeLink/releases"


@Composable
fun AboutInnerPage(
    listState: ScrollState,
    contentPadding: PaddingValues,
    openDrawer: () -> Unit,
) {

    val activityContext = LocalContext.current
    val exitApp = AppModel.exitApp

    //back handler block start
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true) }
    val backHandlerOnBack = ComposeHelper.getDoubleClickBackHandler(
        context = activityContext,
        openDrawer = openDrawer,
        exitApp = exitApp
    )
    BackHandler(enabled = isBackHandlerEnable.value, onBack = { backHandlerOnBack() })
    //back handler block end

    val appLogoEasterEggOn = rememberSaveable { mutableStateOf(false) }
    val appLogoEasterEggIconColor = remember { mutableStateOf(Color.Magenta) }

    Column(
        modifier = Modifier
            .baseVerticalScrollablePageModifier(contentPadding, listState)
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {

        // App Icon Card
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
                    if (appLogoEasterEggOn.value) {
                        AppIconMonoChrome(tint = appLogoEasterEggIconColor.value)
                    } else {
                        AppIcon()
                    }
                }

                Spacer(Modifier.height(24.dp))

                // App Name
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(8.dp))

                // Tagline
                Text(
                    text = "Learn, build, launch. All on your Android.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                // Version
                Text(
                    text = AppModel.getAppVersionNameAndCode(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Developer Card
        CardContainer {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Developer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                DeveloperItem(
                    icon = Icons.Outlined.Person,
                    title = "Made by",
                    subtitle = developerName,
                    onClick = { ActivityUtil.openUrl(activityContext, sourceCodeLink) }
                )

                Spacer(Modifier.height(16.dp))

                DeveloperItem(
                    icon = Icons.Outlined.Send,
                    title = "Telegram Channel",
                    subtitle = developerTelegram,
                    onClick = { ActivityUtil.openUrl(activityContext, developerTelegram) }
                )

                Spacer(Modifier.height(24.dp))

                // Premium Download Button
                Button(
                    onClick = { ActivityUtil.openUrl(activityContext, releasesLink) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Download,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Premium Download",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}


@Composable
private fun CardContainer(
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        content()
    }
}

@Composable
private fun DeveloperItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}