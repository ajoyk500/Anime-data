package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RepoCardTitleText(repoName:String) {
    Text(
        text = repoName,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(
            start = 10.dp,
            top = 2.dp,
            bottom = 0.dp,
            end = 1.dp
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
