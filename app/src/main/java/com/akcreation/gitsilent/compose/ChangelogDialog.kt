package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.developerName
import com.akcreation.gitsilent.screen.content.homescreen.innerpage.developerTelegram
import com.akcreation.gitsilent.utils.ActivityUtil


private const val appDescription = """
GitSilent is an Powerful Android Git client that lets you fully manage Git repositories directly on your phone.
It includes powerful features like cloning, committing, pushing, pulling, branch management, merging, a diff viewer, automation tasks, and SSH support.

The app is lightweight, built on Material 3 design, and provides a reliable Git GUI with features such as a crash handler, changelog viewer, background sync, and various developer tools.
"""

private val featuresList = listOf(
    "Complete Git operations support",
    "Clone, commit, push, and pull repositories",
    "Branch management and merging",
    "File editing with syntax highlighting",
    "SSH and HTTPS authentication",
    "Beautiful and intuitive Material Design UI",
    "Offline support for local operations",
)


@Composable
fun ChangelogDialog(
    onClose: () -> Unit,
) {
    val activityContext = LocalContext.current

    CopyableDialog2(
        // hide ok btn
        okCompose = {},
        onOk = {},

        cancelBtnText = stringResource(R.string.ok),
        onCancel = onClose,

        title = stringResource(R.string.app_name),
        requireShowTextCompose = true,
        textCompose = {
            ScrollableColumn {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                // App Icon
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    AppIcon()
                }

                Spacer(Modifier.height(24.dp))

                // Developer Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Developer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Made by: $developerName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(4.dp))

                    MultiLineClickableText(
                        text = "Telegram: $developerTelegram"
                    ) {
                        ActivityUtil.openUrl(activityContext, developerTelegram)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Description Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = appDescription.trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Features Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Features",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(8.dp))

                    featuresList.forEach { feature ->
                        Text(
                            text = "• $feature",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
            }
        }
    )
}