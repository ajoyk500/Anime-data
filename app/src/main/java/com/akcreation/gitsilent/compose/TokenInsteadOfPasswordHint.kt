package com.akcreation.gitsilent.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.style.MyStyleKt

@Composable
fun TokenInsteadOfPasswordHint() {
    MySelectionContainer {
        DefaultPaddingRow {
            Text(
                buildAnnotatedString {
                    append(stringResource(R.string.note_pat_instead_of_password))
                    append(" (")
                    withLink(LinkAnnotation.Url("https:
                        withStyle(style = SpanStyle(fontWeight = MyStyleKt.TextItem.defaultFontWeight(), color = MyStyleKt.ClickableText.getColor(), fontSize = MyStyleKt.ClickableText.fontSize)) {
                            append("GitHub")
                        }
                    }
                    append(" / ")
                    withLink(LinkAnnotation.Url("https:
                        withStyle(style = SpanStyle(fontWeight = MyStyleKt.TextItem.defaultFontWeight(), color = MyStyleKt.ClickableText.getColor(), fontSize = MyStyleKt.ClickableText.fontSize)) {
                            append("GitLab")
                        }
                    }
                    append(" / ")
                    withLink(LinkAnnotation.Url("https:
                        withStyle(style = SpanStyle(fontWeight = MyStyleKt.TextItem.defaultFontWeight(), color = MyStyleKt.ClickableText.getColor(), fontSize = MyStyleKt.ClickableText.fontSize)) {
                            append("Bitbucket")
                        }
                    }
                    append(")")
                }
            )
        }
    }
}
