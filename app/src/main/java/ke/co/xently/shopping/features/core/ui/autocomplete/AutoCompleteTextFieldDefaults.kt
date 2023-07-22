package ke.co.xently.shopping.features.core.ui.autocomplete

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@ExperimentalMaterial3Api
object AutoCompleteTextFieldDefaults {
    /** Default elevation for a search bar. */
    val Elevation: Dp = AutoCompleteTextFieldTokens.ContainerElevation

    @Composable
    fun colors(
        containerColor: Color = AutoCompleteTextFieldTokens.ContainerColor.toColor(),
        dividerColor: Color = AutoCompleteViewTokens.DividerColor.toColor(),
        inputFieldColors: TextFieldColors = inputFieldColors(),
    ): AutoCompleteTextFieldColors = AutoCompleteTextFieldColors(
        containerColor = containerColor,
        dividerColor = dividerColor,
        inputFieldColors = inputFieldColors,
    )

    @Composable
    fun inputFieldColors(
        focusedTextColor: Color = AutoCompleteTextFieldTokens.InputTextColor.toColor(),
        unfocusedTextColor: Color = AutoCompleteTextFieldTokens.InputTextColor.toColor(),
        disabledTextColor: Color = FilledTextFieldTokens.DisabledInputColor.toColor()
            .copy(alpha = FilledTextFieldTokens.DisabledInputOpacity),
        cursorColor: Color = FilledTextFieldTokens.CaretColor.toColor(),
        selectionColors: TextSelectionColors = LocalTextSelectionColors.current,
        focusedLeadingIconColor: Color = AutoCompleteTextFieldTokens.LeadingIconColor.toColor(),
        unfocusedLeadingIconColor: Color = AutoCompleteTextFieldTokens.LeadingIconColor.toColor(),
        disabledLeadingIconColor: Color = FilledTextFieldTokens.DisabledLeadingIconColor
            .toColor().copy(alpha = FilledTextFieldTokens.DisabledLeadingIconOpacity),
        focusedTrailingIconColor: Color = AutoCompleteTextFieldTokens.TrailingIconColor.toColor(),
        unfocusedTrailingIconColor: Color = AutoCompleteTextFieldTokens.TrailingIconColor.toColor(),
        disabledTrailingIconColor: Color = FilledTextFieldTokens.DisabledTrailingIconColor
            .toColor().copy(alpha = FilledTextFieldTokens.DisabledTrailingIconOpacity),
        focusedPlaceholderColor: Color = AutoCompleteTextFieldTokens.SupportingTextColor.toColor(),
        unfocusedPlaceholderColor: Color = AutoCompleteTextFieldTokens.SupportingTextColor.toColor(),
        disabledPlaceholderColor: Color = FilledTextFieldTokens.DisabledInputColor.toColor()
            .copy(alpha = FilledTextFieldTokens.DisabledInputOpacity),
    ): TextFieldColors =
        TextFieldDefaults.colors(
            focusedTextColor = focusedTextColor,
            unfocusedTextColor = unfocusedTextColor,
            disabledTextColor = disabledTextColor,
            cursorColor = cursorColor,
            selectionColors = selectionColors,
            focusedLeadingIconColor = focusedLeadingIconColor,
            unfocusedLeadingIconColor = unfocusedLeadingIconColor,
            disabledLeadingIconColor = disabledLeadingIconColor,
            focusedTrailingIconColor = focusedTrailingIconColor,
            unfocusedTrailingIconColor = unfocusedTrailingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
            focusedPlaceholderColor = focusedPlaceholderColor,
            unfocusedPlaceholderColor = unfocusedPlaceholderColor,
            disabledPlaceholderColor = disabledPlaceholderColor,
        )
}