package com.akcreation.gitsilent.utils.temp


private const val fromDiffPrefix = "diff_"
enum class TempFileFlag(val flag:String) {
    FROM_DIFF_SCREEN_REPLACE_LINES_TO_FILE("${fromDiffPrefix}_RLTF")
}
