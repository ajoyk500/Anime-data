package com.akcreation.gitsilent.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akcreation.gitsilent.git.DrawCommitNode
import com.akcreation.gitsilent.utils.forEachIndexedBetter

@Composable
fun PrintNodesInfo(
    title:String,
    nodes:List<DrawCommitNode>,
    appendEndNewLine:Boolean,
) {
    val thickness = remember {5.dp}
    val spacerHeight = remember {10.dp}
    Text("$title\n", fontWeight = FontWeight.ExtraBold)
    nodes.forEachIndexedBetter { idx, it->
        MyHorizontalDivider(thickness = thickness, color = DrawCommitNode.getNodeColorByIndex(idx))
        Spacer(Modifier.height(spacerHeight))
        Text(it.toStringForView())
    }
    if(appendEndNewLine) {
        Text("\n")
    }
}
