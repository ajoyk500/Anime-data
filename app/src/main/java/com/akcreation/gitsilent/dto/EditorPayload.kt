package com.akcreation.gitsilent.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditorPayload(
    val fileIoPath: String = "",
    val firstVisibleLineNumStartFrom1: Int = 1,
    val lastEditedLineNumStartFrom1: Int = 1,
    val mergeModeOn:Boolean = false,
    val patchModeOn:Boolean = false,
    val readOnlyOn:Boolean = false,
): Parcelable
