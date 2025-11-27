package com.akcreation.gitsilent.utils

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.dto.DeviceWidthHeight
import com.akcreation.gitsilent.fileeditor.texteditor.view.ExpectConflictStrDto
import com.akcreation.gitsilent.git.PuppyLine
import com.akcreation.gitsilent.git.PuppyLineOriginType
import com.akcreation.gitsilent.R
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.style.MyStyleKt
import com.akcreation.gitsilent.ui.theme.Theme
import com.github.git24j.core.Diff
import com.github.git24j.core.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val TAG = "UIHelper"
object UIHelper {
    const val bottomBarDividerPlaceHolder = "bottomBarDividerPlaceHolder_13195755"
    object Size {
        @Composable
        fun height(configuration:Configuration = AppModel.getCurActivityConfig()): Int {
            return configuration.screenHeightDp
        }
        @Composable
        fun width(configuration:Configuration = AppModel.getCurActivityConfig()): Int {
            return configuration.screenWidthDp
        }
        @Composable
        fun heightDp(configuration:Configuration = AppModel.getCurActivityConfig()): Dp {
            return height(configuration).dp
        }
        @Composable
        fun widthDp(configuration:Configuration = AppModel.getCurActivityConfig()):Dp {
            return width(configuration).dp
        }
        @Composable
        fun editorVirtualSpace(configuration:Configuration = AppModel.getCurActivityConfig()):Pair<Dp, Dp> {
            return Pair(widthDp(configuration), (height(configuration)-100).dp)
        }
        @Composable
        fun getImeHeightInDp(density:Density = LocalDensity.current):Dp {
            val imeHeightInDp = with(density) { WindowInsets.ime.getBottom(this).toDp() }
            return imeHeightInDp
        }
    }
    fun getTitleColor(enabled: Boolean, inDarkTheme: Boolean = Theme.inDarkTheme) = if(enabled) Color.Unspecified else getDisableBtnColor(inDarkTheme);
    fun getFontColor(inDarkTheme:Boolean=Theme.inDarkTheme): Color {
        return if(inDarkTheme) MyStyleKt.TextColor.darkThemeFontColor else MyStyleKt.TextColor.fontColor
    }
    fun getDisableBtnColor(inDarkTheme: Boolean=Theme.inDarkTheme):Color {
        return if(inDarkTheme) MyStyleKt.IconColor.disable_DarkTheme else MyStyleKt.IconColor.disable
    }
    fun getDisableTextColor(inDarkTheme: Boolean=Theme.inDarkTheme):Color {
        return if(inDarkTheme) MyStyleKt.TextColor.disable_DarkTheme else MyStyleKt.TextColor.disable
    }
    fun getSecondaryFontColor(inDarkTheme:Boolean=Theme.inDarkTheme): Color {
        return if(inDarkTheme) Color.DarkGray else Color.Gray
    }
    @Composable
    fun getIconEnableColorOrNull(enable:Boolean):Color? {
        if(!enable) return null
        val color = if(Theme.inDarkTheme) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primary
        val enableColor = if(Theme.inDarkTheme) Color(red=(color.red+0.3f).coerceAtMost(1f), green = (color.green+0.3f).coerceAtMost(1f), blue = (color.blue+0.3f).coerceAtMost(1f), alpha = 1f) else Color(red=color.red, green = color.green, blue = (color.blue+0.5f).coerceAtMost(1f), alpha = 1f)
        return enableColor
    }
    fun<T> doSelectSpan(itemIdxOfItemList:Int, item: T, selectedItems:List<T>, itemList:List<T>, switchItemSelected:(T)->Unit, selectIfNotInSelectedListElseNoop:(T)->Unit) {
        if(selectedItems.isEmpty() || itemIdxOfItemList<0 || itemIdxOfItemList>itemList.lastIndex) {
            switchItemSelected(item)
            return
        }
        val lastSelectedItem = selectedItems.last()
        val lastSelectedItemIndexOfItemList = itemList.indexOf(lastSelectedItem)
        if(lastSelectedItemIndexOfItemList == -1) {
            switchItemSelected(item)
            return
        }
        if(lastSelectedItemIndexOfItemList == itemIdxOfItemList) {
            return
        }
        val startIndex = Math.min(lastSelectedItemIndexOfItemList, itemIdxOfItemList)
        val endIndexExclusive = Math.max(lastSelectedItemIndexOfItemList, itemIdxOfItemList) + 1
        if(startIndex >= endIndexExclusive
            || startIndex<0 || startIndex>itemList.lastIndex
            || endIndexExclusive<0 || endIndexExclusive>itemList.size
        ) {
            return
        }
        for(i in startIndex..<endIndexExclusive) {
            selectIfNotInSelectedListElseNoop(itemList[i])  
        }
    }
    fun<T> selectIfNotInSelectedListElseNoop(
        item: T,
        selectedItems:MutableList<T>,
        contains:(srcList:List<T>, T)->Boolean = {list, i -> list.contains(i)}
    ) {
        if(!contains(selectedItems, item)) {
            selectedItems.add(item)
        }
    }
    fun<T> selectIfNotInSelectedListElseRemove(
        item:T,
        selectedItems:MutableList<T>,
        contains:(srcList:List<T>, curItem:T)->Boolean = {srcList, curItem -> srcList.contains(curItem)},
        remove:(srcList:MutableList<T>, curItem:T) -> Boolean = {srcList, curItem -> srcList.remove(curItem)}
    ) {
        if(contains(selectedItems, item)) {
            remove(selectedItems, item)
        }else{
            selectedItems.add(item)
        }
    }
    fun scrollToItem(coroutineScope: CoroutineScope, listState: LazyListState, index:Int, animation:Boolean = false) {
        val index = index.coerceAtLeast(0)
        coroutineScope.launch {
            if(animation) {
                listState.animateScrollToItem(index)
            }else {
                listState.scrollToItem(index)
            }
        }
    }
    fun scrollToItem(coroutineScope: CoroutineScope, listState: LazyStaggeredGridState, index:Int, animation:Boolean = false) {
        val index = index.coerceAtLeast(0)
        coroutineScope.launch {
            if(animation) {
                listState.animateScrollToItem(index)
            }else {
                listState.scrollToItem(index)
            }
        }
    }
    fun scrollTo(coroutineScope: CoroutineScope, listState: ScrollState, index:Int, animation:Boolean = false) {
        val index = index.coerceAtLeast(0)
        coroutineScope.launch {
            if(animation) {
                listState.animateScrollTo(index)
            }else {
                listState.scrollTo(index)
            }
        }
    }
    fun <T> scrollByPredicate(scope:CoroutineScope, list: List<T>, listState:LazyListState, offset:Int = Cons.scrollToItemOffset, animation: Boolean = false, predicate:(idx:Int, item:T)->Boolean) {
        for((idx, item) in list.withIndex()) {
            if(predicate(idx, item)) {
                scrollToItem(scope, listState, idx+offset, animation)
                break
            }
        }
    }
    fun switchBetweenTopAndLastVisiblePosition(coroutineScope: CoroutineScope, listState: LazyListState, lastPosition:MutableState<Int>)  {
        val lastVisibleLine = listState.firstVisibleItemIndex
        val notAtTop = lastVisibleLine != 0
        val position = if(notAtTop) {
            lastPosition.value = lastVisibleLine
            0
        } else {
            lastPosition.value
        }
        scrollToItem(coroutineScope, listState, position)
    }
    fun switchBetweenTopAndLastVisiblePosition(coroutineScope: CoroutineScope, listState: LazyStaggeredGridState, lastPosition:MutableState<Int>)  {
        val lastVisibleLine = listState.firstVisibleItemIndex
        val notAtTop = lastVisibleLine != 0
        val position = if(notAtTop) {
            lastPosition.value = lastVisibleLine
            0
        } else {
            lastPosition.value
        }
        scrollToItem(coroutineScope, listState, position)
    }
    fun switchBetweenTopAndLastVisiblePosition(coroutineScope: CoroutineScope, listState: ScrollState, lastPosition:MutableState<Int>)  {
        val lastVisibleLine = listState.value
        val notAtTop = lastVisibleLine != 0
        val position = if(notAtTop) {
            lastPosition.value = lastVisibleLine
            0
        } else {
            lastPosition.value
        }
        scrollTo(coroutineScope, listState, position)
    }
    fun getHighlightingTimeInMills(): Long {
        return 800L
    }
    @Composable
    fun getHighlightingBackgroundColor(baseColor:Color = MaterialTheme.colorScheme.inversePrimary): Color {
        return baseColor.copy(alpha = 0.4f)
    }
    @Composable
    fun defaultCardColor():Color {
        return if(Theme.inDarkTheme) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.surfaceDim
    }
    fun getBackgroundColorForMergeConflictSplitText(
        text: String,
        settings: AppSettings,
        expectConflictStrDto: ExpectConflictStrDto,
        oursBgColor:Color,
        theirsBgColor:Color,
        startLineBgColor:Color,
        splitLineBgColor:Color,
        endLineBgColor:Color,
        normalBgColor:Color = Color.Unspecified,
    ): Color {
        val nextExpectConflictStr = expectConflictStrDto.getNextExpectConflictStr()
        val curExpectConflictStr = expectConflictStrDto.curConflictStr
        val curExpectConflictStrMatched = expectConflictStrDto.curConflictStrMatched
        val (curExpect, nextExcept) = expectConflictStrDto.getCurAndNextExpect()
        val retColor = if(curExpectConflictStrMatched) {
            if(text.startsWith(nextExpectConflictStr)) {
                expectConflictStrDto.curConflictStr = if(nextExcept==0) settings.editor.conflictStartStr else if(nextExcept==1) settings.editor.conflictSplitStr else settings.editor.conflictEndStr
                if(nextExcept==2) {
                    expectConflictStrDto.reset()
                }
                if(nextExcept==0) startLineBgColor else if(nextExcept==1) splitLineBgColor else endLineBgColor
            }else {
                if(curExpect==0) oursBgColor else theirsBgColor
            }
        }else {
            if(text.startsWith(curExpectConflictStr)) {
                expectConflictStrDto.curConflictStrMatched = true
                if(curExpect==0) startLineBgColor else if(curExpect==1) splitLineBgColor else endLineBgColor
            }else {
                normalBgColor
            }
        }
        return retColor
    }
    fun getCheckBoxByState(state: Boolean) = if (state) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank
    fun getIconForSwitcher(state: Boolean) = if (state) Icons.Filled.ToggleOn else Icons.Filled.ToggleOff
    fun getColorForSwitcher(state: Boolean) = if (state) Color(0xFF0090FF) else if(Theme.inDarkTheme) Color.LightGray else Color.Gray
    fun getChangeListTitleColor(repoStateValue: Int):Color {
        return if(repoStateValue == Repository.StateT.MERGE.bit || repoStateValue == Repository.StateT.REBASE_MERGE.bit || repoStateValue == Repository.StateT.CHERRYPICK.bit) MyStyleKt.TextColor.danger() else Color.Unspecified
    }
    fun getDividerColor(inDarkTheme: Boolean = Theme.inDarkTheme):Color {
        return if (inDarkTheme) Color.DarkGray.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.2f)
    }
    fun getChangeTypeColor(type: String):Color {
        if(type == Cons.gitStatusNew) {
            return if(Theme.inDarkTheme) MyStyleKt.ChangeListItemColor.changeTypeAdded_darkTheme else MyStyleKt.ChangeListItemColor.changeTypeAdded
        }
        if(type == Cons.gitStatusDeleted) {
            return if(Theme.inDarkTheme) MyStyleKt.ChangeListItemColor.changeTypeDeleted_darkTheme else MyStyleKt.ChangeListItemColor.changeTypeDeleted
        }
        if(type == Cons.gitStatusModified) {
            return if(Theme.inDarkTheme) MyStyleKt.ChangeListItemColor.changeTypeModified_darkTheme else MyStyleKt.ChangeListItemColor.changeTypeModified
        }
        if(type == Cons.gitStatusConflict) {
            return if(Theme.inDarkTheme) MyStyleKt.ChangeListItemColor.changeTypeConflict_darkTheme else MyStyleKt.ChangeListItemColor.changeTypeConflict
        }
        return Color.Unspecified
    }
    @Composable
    fun getDeviceWidthHeightInDp(configuration: Configuration = AppModel.getCurActivityConfig()): DeviceWidthHeight {
        return DeviceWidthHeight(configuration.screenWidthDp.toFloat(), configuration.screenHeightDp.toFloat())
    }
    fun getRepoItemWidth(): Float {
        return 392f
    }
    fun getRepoItemsCountEachRow(screenWidth: Float):Int {
        val count = screenWidth / getRepoItemWidth()
        return count.toInt().coerceAtLeast(1)
    }
    fun getLastClickedColor(): Color {
        return MyStyleKt.LastClickedItem.getBgColor(Theme.inDarkTheme)
    }
    fun getRunningStateColor(runningStatus: Boolean?):Color {
        return if (runningStatus == null) MyStyleKt.TextColor.error() else if (runningStatus) MyStyleKt.TextColor.getHighlighting() else Color.Unspecified
    }
    fun getRunningStateText(context: Context, runningStatus: Boolean?):String{
        return if (runningStatus == null) context.getString(R.string.unknown_state) else if (runningStatus) context.getString(R.string.running) else context.getString(R.string.stopped)
    }
    fun getTextForSwitcher(context: Context, runningStatus: Boolean?):String {
        return if (runningStatus == true) context.getString(R.string.enable) else context.getString(R.string.disable)
    }
    fun getCardButtonTextColor(enabled: Boolean, inDarkTheme: Boolean):Color {
        return if (enabled) MyStyleKt.TextColor.enable else if (inDarkTheme) MyStyleKt.TextColor.disable_DarkTheme else MyStyleKt.TextColor.disable
    }
    @Composable
    fun isRtlLayout(): Boolean {
        return LocalLayoutDirection.current == LayoutDirection.Rtl
    }
    fun spToPx(sp: TextUnit, density: Density):Float {
        return with(density) { sp.toPx() }
    }
    fun spToPx(sp: Float, density: Density): Float {
        return spToPx(sp.sp, density)
    }
    fun spToPx(sp: Int, density: Density): Float {
        return spToPx(sp.sp, density)
    }
    fun dpToPx(dp: Dp, density:Density): Float {
        return with(density) { dp.toPx() }
    }
    fun dpToPx(dp:Float, density:Density): Float {
        return dpToPx(dp.dp, density)
    }
    fun dpToPx(dp:Int, density:Density): Float {
        return dpToPx(dp.dp, density)
    }
    fun pxToDp(px:Float, density:Density): Dp {
        return with(density) { px.toDp() }
    }
    fun pxToDp(px:Int, density:Density): Dp {
        return with(density) { px.toDp() }
    }
    fun pxToDpAtLeast0(px:Int, density:Density): Dp {
        return with(density) { px.coerceAtLeast(0).toDp() }
    }
    fun getLuckyOffset(indexToPx:Boolean, screenWidthInPx:Float, screenHeightInPx:Float):Float {
        val base = (screenWidthInPx + screenHeightInPx)
        return base * 0.2f
    }
    fun guessLineHeight(fontSizeInPx: Float): Float {
        return fontSizeInPx * 1.5f
    }
    @Composable
    fun isPortrait(configuration: Configuration = AppModel.getCurActivityConfig()):Boolean {
        return configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }
    fun getHunkColor(inDarkTheme: Boolean = Theme.inDarkTheme) = if(inDarkTheme) MyStyleKt.Diff.hunkHeaderColorBgInDarkTheme else MyStyleKt.Diff.hunkHeaderColorBgInLightTheme;
    @Composable
    fun getSoftkeyboardHeightInDp(density: Density = LocalDensity.current, offsetInDp: Int = 0): Dp {
        val keyboardHeight = WindowInsets.ime.getBottom(density).coerceAtLeast(0)
        return with(density) { (keyboardHeight.toDp().value.toInt() + offsetInDp).coerceAtLeast(0).dp }
    }
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun isSoftkeyboardVisible():Boolean {
        return WindowInsets.isImeVisible
    }
    @Composable
    fun getNaviBarsPadding(
        density: Density = LocalDensity.current,
        direction: LayoutDirection=LocalLayoutDirection.current,
    ): PaddingValues {
        val naviBarsTopHeight = WindowInsets.navigationBars.getTop(density).coerceAtLeast(0)
        val naviBarsBottomHeight = WindowInsets.navigationBars.getBottom(density).coerceAtLeast(0)
        val naviBarsLeftHeight = WindowInsets.navigationBars.getLeft(density, direction).coerceAtLeast(0)
        val naviBarsRightHeight = WindowInsets.navigationBars.getRight(density, direction).coerceAtLeast(0)
        return with(density) {
            PaddingValues(
                top = naviBarsTopHeight.toDp(),
                bottom = naviBarsBottomHeight.toDp(),
                start = naviBarsLeftHeight.toDp(),
                end = naviBarsRightHeight.toDp(),
            )
        }
    }
    fun getRandomColor(
        alpha:Int? = null,
        red:Int? = null,
        green:Int? = null,
        blue:Int? = null
    ):Color {
        val alpha = alpha ?: Random.nextInt(256) 
        val red = red ?: Random.nextInt(256)   
        val green = green ?: Random.nextInt(256) 
        val blue = blue ?: Random.nextInt(256)  
        return Color(
            alpha = alpha,
            red = red,
            green = green,
            blue = blue,
        )
    }
    fun getRandomColorForDrawNode(
        alpha:Int,
        range: IntRange = 40..201,
    ):Color {
        val red = Random.nextInt(range.start, range.endInclusive)
        val green = Random.nextInt(range.start, range.endInclusive)
        val blue = Random.nextInt(range.start, range.endInclusive)
        return Color(
            alpha = alpha,
            red = red,
            green = green,
            blue = blue,
        )
    }
    fun getMatchedTextBgColorForDiff(inDarkTheme:Boolean = Theme.inDarkTheme, line: PuppyLine):Color {
        if(line.originType == PuppyLineOriginType.ADDITION) {  
            return if(inDarkTheme) MyStyleKt.Diff.hasMatchedAddedLineBgColorForDiffInDarkTheme else MyStyleKt.Diff.hasMatchedAddedLineBgColorForDiffInLightTheme
        }else if(line.originType == PuppyLineOriginType.DELETION) {  
            return if(inDarkTheme) MyStyleKt.Diff.hasMatchedDeletedLineBgColorForDiffInDarkTheme else MyStyleKt.Diff.hasMatchedDeletedLineBgColorForDiffInLightTheme
        }else if(line.originType == PuppyLineOriginType.HUNK_HDR) {  
            return Color.Gray
        }else if(line.originType == PuppyLineOriginType.CONTEXT) {  
            return Color.Unspecified
        }else if(line.originType == PuppyLineOriginType.CONTEXT_EOFNL) {  
            return Color.Unspecified
        }else if(line.originType == PuppyLineOriginType.ADD_EOFNL) {  
            return Color.Unspecified
        }else if(line.originType == PuppyLineOriginType.DEL_EOFNL) {  
            return Color.Unspecified
        }else {  
            return Color.Unspecified
        }
    }
    fun getDiffLineBgColor(line:PuppyLine, inDarkTheme: Boolean):Color{
        if(line.originType == PuppyLineOriginType.ADDITION) {  
            return if(inDarkTheme) MyStyleKt.Diff.addedLineBgColorForDiffInDarkTheme else MyStyleKt.Diff.addedLineBgColorForDiffInLightTheme
        }else if(line.originType == PuppyLineOriginType.DELETION) {  
            return if(inDarkTheme) MyStyleKt.Diff.deletedLineBgColorForDiffInDarkTheme else MyStyleKt.Diff.deletedLineBgColorForDiffInLightTheme
        }else if(line.originType == PuppyLineOriginType.HUNK_HDR) {  
            return Color.Gray
        }else if(line.originType == PuppyLineOriginType.CONTEXT) {  
            return Color.Unspecified
        }else if(line.originType == PuppyLineOriginType.CONTEXT_EOFNL) {  
            return Color.Unspecified
        }else if(line.originType == PuppyLineOriginType.ADD_EOFNL) {  
            return Color.Unspecified
        }else if(line.originType == PuppyLineOriginType.DEL_EOFNL) {  
            return Color.Unspecified
        }else {  
            return Color.Unspecified
        }
    }
    fun getDiffLineTextColor(line:PuppyLine, inDarkTheme:Boolean):Color{
        if(line.originType == Diff.Line.OriginType.ADDITION.toString()) {  
            return UIHelper.getFontColor(inDarkTheme)
        }else if(line.originType == Diff.Line.OriginType.DELETION.toString()) {  
            return UIHelper.getFontColor(inDarkTheme)
        }else if(line.originType == Diff.Line.OriginType.CONTEXT.toString()) {  
            return UIHelper.getFontColor(inDarkTheme)
        }else if(line.originType == Diff.Line.OriginType.HUNK_HDR.toString()) {  
            return UIHelper.getFontColor(inDarkTheme)
        }else if(line.originType == Diff.Line.OriginType.CONTEXT_EOFNL.toString()) {  
            return UIHelper.getSecondaryFontColor(inDarkTheme)
        }else if(line.originType == Diff.Line.OriginType.ADD_EOFNL.toString()) {  
            return UIHelper.getSecondaryFontColor(inDarkTheme)
        }else if(line.originType == Diff.Line.OriginType.DEL_EOFNL.toString()) {  
            return UIHelper.getSecondaryFontColor(inDarkTheme)
        }else {  
            return Color.Unspecified
        }
    }
    @Composable
    fun getDiffLineTypeStr(line:PuppyLine): String {
        if(line.originType == Diff.Line.OriginType.ADDITION.toString()) {
            return stringResource(R.string.diff_line_type_add)
        }else if(line.originType == Diff.Line.OriginType.DELETION.toString()) {
            return stringResource(R.string.diff_line_type_del)
        }else if(line.originType == Diff.Line.OriginType.HUNK_HDR.toString()) {
            return stringResource(R.string.diff_line_type_hunk_header)
        }else if(line.originType == Diff.Line.OriginType.CONTEXT.toString()) {
            return stringResource(R.string.diff_line_type_context)
        }else if(line.originType == Diff.Line.OriginType.CONTEXT_EOFNL.toString()) {
            return ""
        }else if(line.originType == Diff.Line.OriginType.ADD_EOFNL.toString()) {
            return stringResource(R.string.diff_line_type_add)
        }else if(line.originType == Diff.Line.OriginType.DEL_EOFNL.toString()) {
            return stringResource(R.string.diff_line_type_del)
        }else {
            return ""
        }
    }
    fun randomRainbowColor(index: Int = Random.nextInt(from = 0, until = 7)):Color {
        return if(index == 0) {
            Color.Red
        }else if(index == 1) {
            Color(0xFFA84C00)
        }else if(index == 2) {
            Color.Yellow
        }else if(index == 3) {
            Color.Green
        }else if(index == 4) {
            Color.Cyan
        }else if(index == 5) {
            Color.Blue
        }else {
            Color.Magenta
        }
    }
}
