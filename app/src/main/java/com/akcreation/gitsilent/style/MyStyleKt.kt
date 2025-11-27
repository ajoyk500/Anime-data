package com.akcreation.gitsilent.style

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akcreation.gitsilent.dto.DeviceWidthHeight
import com.akcreation.gitsilent.ui.theme.Theme
import com.akcreation.gitsilent.utils.UIHelper
import com.akcreation.gitsilent.utils.fabBasePadding

object MyStyleKt {
    val emptySpanStyle = SpanStyle()
    val defaultItemPadding = 10.dp
    val defaultItemPaddingValues = PaddingValues(defaultItemPadding)
    val defaultHorizontalPadding = defaultItemPadding
    val defaultIconSize = 40.dp
    val defaultIconSizeSmaller = 30.dp
    val defaultIconSizeLarger = 60.dp
    val trailIconSize = 24.dp
    val trailIconSplitSpacerWidth = 20.dp
    val defaultInLineIconSize = 16.dp
    val defaultInLineIconsPressedCircleSize = 24.dp
    val defaultLongPressAbleIconBtnPressedCircleSize = 40.0.dp
    const val defaultMultiLineTextFieldMaxLines = 6
    object TextItem {
        fun defaultFontWeight(): FontWeight? {
            return null
        }
    }
    object ClickableText{
        @Composable
        fun getStyle() = LocalTextStyle.current;  
        private val color_light = Color(0xFF004FD7)
        private val color_dark = Color(0xFF0096FF)
        fun getColor(inDarkTheme: Boolean = Theme.inDarkTheme) = if(inDarkTheme) color_dark else color_light
        fun getErrColor() = TextColor.error()
        val minClickableSize = 25.dp
        val modifierNoPadding = Modifier.defaultMinSize(minClickableSize)
        val modifier = modifierNoPadding
        val fontSize = 16.sp
    }
    object ChangeListItemColor {
        val changeTypeAdded = Color(0xFF117C21)
        val changeTypeAdded_darkTheme = Color(0xFF78ab78)
        val changeTypeModified = Color(0xFF2B6FC2)
        val changeTypeModified_darkTheme = Color(0xFF5A9AD9)
        val changeTypeDeleted = Color(0xFFBE4040)
        val changeTypeDeleted_darkTheme = Color(0xFFBE4040)
        val changeTypeConflict = Color(0xFF8A40BE)
        val changeTypeConflict_darkTheme = Color(0xFF8E40BE)
        fun getConflictColor(inDarkTheme: Boolean = Theme.inDarkTheme): Color {
            return if(inDarkTheme) changeTypeConflict_darkTheme else changeTypeConflict
        }
    }
    object Diff {
        val hunkHeaderColorBgInDarkTheme = Color(0x368BB3DC)
        val hunkHeaderColorBgInLightTheme = Color(0x8098ABD5)
        val lineNum_forDiffInLightTheme = Color(0xFF5B5B5B)
        val lineNum_forDiffInDarkTheme = Color(0xFFA6A6A6)
        val hasMatchedAddedLineBgColorForDiffInLightTheme = Color(0x362E752E)
        val hasMatchedAddedLineBgColorForDiffInDarkTheme = Color(0x570C540C)
        val hasMatchedDeletedLineBgColorForDiffInLightTheme = Color(0x366E1F1F)
        val hasMatchedDeletedLineBgColorForDiffInDarkTheme = Color(0x57540C0C)
        val addedLineBgColorForDiffInLightTheme = Color(0x5C4A934A)
        val addedLineBgColorForDiffInDarkTheme = Color(0xC41D591D)
        val deletedLineBgColorForDiffInLightTheme = Color(0x5C965353)
        val deletedLineBgColorForDiffInDarkTheme = Color(0xC4621D1D)
        fun lineNumColorForDiff(inDarkTheme:Boolean = Theme.inDarkTheme):Color {
            return if(inDarkTheme) lineNum_forDiffInDarkTheme else lineNum_forDiffInLightTheme
        }
    }
    object IconColor {
        val disable = Color(0xFFB2B2B2)
        val disable_DarkTheme = Color(0xFF505050)
    }
    object TextColor {
        val enable = Color.Unspecified
        val disable = IconColor.disable
        val disable_DarkTheme = IconColor.disable_DarkTheme
        private val highlighting_green_light = Color(0xFF00790A)
        private val highlighting_green_dark = Color(0xFF1FAB26)
        fun getHighlighting(inDarkTheme: Boolean = Theme.inDarkTheme) = if(inDarkTheme) highlighting_green_dark else highlighting_green_light
        private val lineNum_forEditorInLightTheme = Color.Gray
        private val lineNum_forEditorInDarkTheme = Color(0xFF5D5D5D)
        private val lineNum_focused_forEditorInLightTheme = Color.DarkGray
        private val lineNum_focused_forEditorInDarkTheme = Color(0xFF949494)
        private val lineNumBg_forEditorInLightTheme = Color(0x65E7E7E7)
        private val lineNumBg_forEditorInDarkTheme = Color(0x1B414141)
        val fontColor = Color(0xFF2D2D2D)
        val darkThemeFontColor = Color(0xFFADADAD)
        private val err_light = Color(0xFFA40000)
        private val err_dark = Color(0xFFCE6161)
        fun error() = if(Theme.inDarkTheme) err_dark else err_light
        fun danger() = error()
        fun lineNumColor(inDarkTheme: Boolean = Theme.inDarkTheme, isFocused: Boolean):Color {
            return if(isFocused) {
                if(inDarkTheme) lineNum_focused_forEditorInDarkTheme else lineNum_focused_forEditorInLightTheme
            }else {
                if(inDarkTheme) lineNum_forEditorInDarkTheme else lineNum_forEditorInLightTheme
            }
        }
        fun lineNumBgColor(inDarkTheme:Boolean = Theme.inDarkTheme):Color {
            return if(inDarkTheme) lineNumBg_forEditorInDarkTheme else lineNumBg_forEditorInLightTheme
        }
    }
    object TextSize {
        val default = 16.sp
        val lineNumSize = 10.sp
        val medium = 18.sp
    }
    object Editor {
        val highlightingBgColor = Color(0xFFFFEB3B)
    }
    object BottomBar{
        val height=60.dp
        val outsideContentPadding = height+20.dp
        val fabBottomPadding = height
    }
    object Fab {
        @Composable
        fun getFabModifierForEditor(isMultipleSelectionMode:Boolean, isPortrait:Boolean):Modifier {
            val naviPadding = UIHelper.getNaviBarsPadding()
            val kbOffset = naviPadding.calculateBottomPadding().value.toInt().let { if(it == 0) 0 else -it }
            val kbHeight = UIHelper.getSoftkeyboardHeightInDp(offsetInDp = kbOffset)
            return addNavPaddingIfNeed(
                isPortrait,
                Modifier.fabBasePadding().then(
                    if(kbHeight.value > 0) {  
                        Modifier.padding(bottom = kbHeight)
                    }else {  
                        if(isMultipleSelectionMode) Modifier.padding(bottom = BottomBar.height) else Modifier
                    }
                )
            )
        }
        @Composable
        fun getFabModifier(isPortrait:Boolean, deviceWidthHeight: DeviceWidthHeight):Modifier {
            return addNavPaddingIfNeed(isPortrait, Modifier.fabBasePadding().padding(bottom = BottomBar.fabBottomPadding, end = 30.dp))
        }
        private fun addNavPaddingIfNeed(isPortrait:Boolean, modifier:Modifier = Modifier):Modifier {
            return if(isPortrait) {
                modifier
            }else {
                modifier
            }
        }
    }
    object RadioOptions{
        val minHeight=30.dp
        val middleHeight=35.dp
        val largeHeight=60.dp
    }
    object Title {
        val lineHeight = 20.dp
        val firstLineFontSize = 18.sp
        val firstLineFontSizeSmall = 15.sp
        val secondLineFontSize = 12.sp
        val clickableTitleMinWidth = 200.dp
    }
    object SettingsItem {
        val itemFontSize = 20.sp
        val itemDescFontSize = 15.sp
        val switcherIconSize = 60.dp
        val selectorWidth = 160.dp
        private fun Modifier.switcherLeftItemPadding():Modifier {
            return padding(end = switcherIconSize + 5.dp)
        }
        private fun Modifier.selectorLeftItemPadding():Modifier {
            return padding(end = selectorWidth + 5.dp)
        }
        val switcherLeftBaseModifier = Modifier.switcherLeftItemPadding().fillMaxWidth()
        val switcherRightBaseModifier = Modifier.width(switcherIconSize)
        val selectorLeftBaseModifier = Modifier.selectorLeftItemPadding().fillMaxWidth()
        val selectorRightBaseModifier = Modifier.width(selectorWidth)
    }
    object Padding {
        val PageBottom = 50.dp
        val firstLineTopPaddingValuesInDp = 5.dp
    }
    object TextSelectionColor {
        val customTextSelectionColors_cursorHandleVisible = TextSelectionColors(
            handleColor = Color(0x854B6CC6),  
            backgroundColor = Color(0x756A86D1),  
        )
    }
    object Icon {
        val size = 25.dp
        val modifier = Modifier.size(size)
    }
    object CheckoutBox {
        val height = 40.dp
    }
    object TopBar {
        val dropDownMenuTopPaddingSize = 70.dp
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun getColors() = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun getColorsSimple() = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    }
    object BottomSheet{
        const val skipPartiallyExpanded = true
    }
     object DropDownMenu {
         val minWidth = 60.dp
         @Composable
         fun selectedItemColor() = MaterialTheme.colorScheme.primary;
         @Composable
         fun selectedItemContainerColor() = MaterialTheme.colorScheme.primaryContainer;
    }
    object ToggleButton {
        fun defaultShape() = RectangleShape;
        @Composable
        fun defaultColors(): IconToggleButtonColors {
            return IconButtonDefaults.filledTonalIconToggleButtonColors().copy(
                containerColor = Color.Transparent,
                checkedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        }
    }
    object ConflictBlock {
        private val conflictOursBlockBgColorInDarkThem =  Theme.Orange.copy(alpha = 0.1f)
        private val conflictOursBlockBgColorInLightThem =  Theme.Orange.copy(alpha = 0.2f)
        fun getConflictOursBlockBgColor(inDarkTheme: Boolean = Theme.inDarkTheme):Color {
            return if(inDarkTheme) conflictOursBlockBgColorInDarkThem else conflictOursBlockBgColorInLightThem
        }
        private val conflictTheirsBgColorInDarkTheme = Color.Magenta.copy(alpha = 0.1f)
        private val conflictTheirsBgColorInLightTheme = Color.Magenta.copy(alpha = 0.2f)
        fun getConflictTheirsBlockBgColor(inDarkTheme: Boolean = Theme.inDarkTheme):Color {
            return if(inDarkTheme) conflictTheirsBgColorInDarkTheme else conflictTheirsBgColorInLightTheme
        }
        private val conflictStartLineBgColorInDarkTheme = Theme.Orange.copy(alpha = 0.2f)
        private val conflictStartLineBgColorInLightTheme = Theme.Orange.copy(alpha = 0.4f)
        fun getConflictStartLineBgColor(inDarkTheme: Boolean = Theme.inDarkTheme):Color {
            return if(inDarkTheme) conflictStartLineBgColorInDarkTheme else conflictStartLineBgColorInLightTheme
        }
        private val conflictSplitLineBgColorInDarkTheme = Theme.darkLightBlue.copy(alpha = 0.4f)
        private val conflictSplitLineBgColorInLightTheme = Color.Blue.copy(alpha = 0.2f)
        fun getConflictSplitLineBgColor(inDarkTheme: Boolean = Theme.inDarkTheme):Color {
            return if(inDarkTheme) conflictSplitLineBgColorInDarkTheme else conflictSplitLineBgColorInLightTheme
        }
        private val conflictEndLineBgColorInDarkTheme = Color.Magenta.copy(alpha = 0.2f)
        private val conflictEndLineBgColorInLightTheme = Color.Magenta.copy(alpha = 0.4f)
        fun getConflictEndLineBgColor(inDarkTheme: Boolean = Theme.inDarkTheme):Color {
            return if(inDarkTheme) conflictEndLineBgColorInDarkTheme else conflictEndLineBgColorInLightTheme
        }
        private val acceptOursIconColorInDarkTheme = Theme.Orange.copy(.4f)
        private val acceptOursIconColorInLightTheme = Theme.Orange.copy(.8f)
        fun getAcceptOursIconColor(inDarkTheme: Boolean = Theme.inDarkTheme):Color {
            return if(inDarkTheme) acceptOursIconColorInDarkTheme else acceptOursIconColorInLightTheme
        }
        private val acceptTheirsIconColorInDarkTheme = Color.Magenta.copy(.4f)
        private val acceptTheirsIconColorInLightTheme = Color.Magenta.copy(.8f)
        fun getAcceptTheirsIconColor(inDarkTheme: Boolean = Theme.inDarkTheme):Color {
            return if(inDarkTheme) acceptTheirsIconColorInDarkTheme else acceptTheirsIconColorInLightTheme
        }
        private val acceptBothIconColorInDarkTheme = Theme.darkLightBlue.copy(alpha = 0.6f)
        private val acceptBothIconColorInLightTheme = Color.Blue.copy(.8f)
        fun getAcceptBothIconColor(inDarkTheme: Boolean = Theme.inDarkTheme):Color {
            return if(inDarkTheme) acceptBothIconColorInDarkTheme else acceptBothIconColorInLightTheme
        }
        private val rejectBothIconColorInDarkTheme = Color.Red.copy(.4f)
        private val rejectBothIconColorInLightTheme = Color.Red.copy(.8f)
        fun getRejectBothIconColor(inDarkTheme: Boolean = Theme.inDarkTheme):Color {
            return if(inDarkTheme) rejectBothIconColorInDarkTheme else rejectBothIconColorInLightTheme
        }
    }
    object LastClickedItem {
        private val bgColorDark = Color.DarkGray.copy(alpha = .2f)
        private val bgColorLight = Color.LightGray.copy(alpha = .2f)
        fun getBgColor(inDarkTheme: Boolean) = if(inDarkTheme) bgColorDark else bgColorLight
        private val editorLastClickedLineBgColorDark = Color(0x20484848)
        private val editorLastClickedLineBgColorLight = Color(0x2AB9B9B9)
        fun getEditorLastClickedLineBgColor(inDarkTheme: Boolean) = if(inDarkTheme) editorLastClickedLineBgColorDark else editorLastClickedLineBgColorLight
    }
}
