package com.akcreation.gitsilent.dto

import androidx.compose.runtime.Composable

data class MenuTextItem (
    val text:String,
    val prependContent:(@Composable ()->Unit)? = null,
    val appendContent:(@Composable ()->Unit)? = null,
    val closeMenuAfterClick:()->Boolean = {true},
    val enabled:()->Boolean = {true},
    val visible:()->Boolean = {true},
    val onClick:()->Unit,
)
