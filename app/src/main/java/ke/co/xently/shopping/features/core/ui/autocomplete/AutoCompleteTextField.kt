package ke.co.xently.shopping.features.core.ui.autocomplete


import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ke.co.xently.shopping.features.core.ui.theme.XentlyTheme
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    shape: Shape = TextFieldDefaults.shape,
    colors: AutoCompleteTextFieldColors = AutoCompleteTextFieldDefaults.colors(),
    tonalElevation: Dp = AutoCompleteTextFieldDefaults.Elevation,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    content: @Composable (ColumnScope.() -> Unit),
) {
    val focusManager = LocalFocusManager.current

    Surface(
        shape = shape,
        color = colors.containerColor,
        contentColor = contentColorFor(colors.containerColor),
        tonalElevation = if (active) tonalElevation else 0.dp,
        modifier = modifier
            .zIndex(1f)
            .width(SearchBarMinWidth),
    ) {
        Column {
            AutoCompleteInputField(
                query = query,
                onQueryChange = onQueryChange,
                active = active,
                onActiveChange = onActiveChange,
                enabled = enabled,
                isError = isError,
                label = label,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                supportingText = supportingText,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                interactionSource = interactionSource,
            )

            AnimatedVisibility(
                visible = active,
                enter = DockedEnterTransition,
                exit = DockedExitTransition,
            ) {
                val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                val maxHeight = remember(screenHeight) {
                    screenHeight * DockedActiveTableMaxHeightScreenRatio
                }
                val minHeight = remember(maxHeight) {
                    DockedActiveTableMinHeight.coerceAtMost(maxHeight)
                }

                Column(Modifier.heightIn(min = minHeight, max = maxHeight)) {
                    Divider(color = colors.dividerColor)
                    content()
                }
            }
        }
    }

    LaunchedEffect(active) {
        if (!active) {
            // Not strictly needed according to the motion spec, but since the animation already has
            // a delay, this works around b/261632544.
            delay(AnimationDelayMillis.toLong())
            focusManager.clearFocus()
        }
    }

    BackHandler(enabled = active) {
        onActiveChange(false)
    }
}

@Composable
private fun AutoCompleteInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val focusRequester = remember { FocusRequester() }
    val searchSemantics = getString(Strings.SearchBarSearch)
    val suggestionsAvailableSemantics = getString(Strings.SuggestionsAvailable)

    TextField(
        maxLines = 1,
        singleLine = true,
        value = query,
        enabled = enabled,
        isError = isError,
        label = label,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        onValueChange = onQueryChange,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        supportingText = if (active) null else supportingText,
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused) onActiveChange(true) }
            .semantics {
                contentDescription = searchSemantics
                if (active) {
                    stateDescription = suggestionsAvailableSemantics
                }
                onClick {
                    focusRequester.requestFocus()
                    true
                }
            },
    )
}

// Measurement specs
internal val DockedActiveTableMinHeight: Dp = 240.dp
private const val DockedActiveTableMaxHeightScreenRatio: Float = 2f / 3f
internal val SearchBarMinWidth: Dp = 360.dp

// Animation specs
private const val AnimationEnterDurationMillis: Int = MotionTokens.DurationLong4.toInt()
private const val AnimationExitDurationMillis: Int = MotionTokens.DurationMedium3.toInt()
private const val AnimationDelayMillis: Int = MotionTokens.DurationShort2.toInt()
private val AnimationEnterEasing = MotionTokens.EasingEmphasizedDecelerateCubicBezier
private val AnimationExitEasing = CubicBezierEasing(0.0f, 1.0f, 0.0f, 1.0f)
private val AnimationEnterFloatSpec: FiniteAnimationSpec<Float> = tween(
    durationMillis = AnimationEnterDurationMillis,
    delayMillis = AnimationDelayMillis,
    easing = AnimationEnterEasing,
)
private val AnimationExitFloatSpec: FiniteAnimationSpec<Float> = tween(
    durationMillis = AnimationExitDurationMillis,
    delayMillis = AnimationDelayMillis,
    easing = AnimationExitEasing,
)
private val AnimationEnterSizeSpec: FiniteAnimationSpec<IntSize> = tween(
    durationMillis = AnimationEnterDurationMillis,
    delayMillis = AnimationDelayMillis,
    easing = AnimationEnterEasing,
)
private val AnimationExitSizeSpec: FiniteAnimationSpec<IntSize> = tween(
    durationMillis = AnimationExitDurationMillis,
    delayMillis = AnimationDelayMillis,
    easing = AnimationExitEasing,
)
private val DockedEnterTransition: EnterTransition =
    fadeIn(AnimationEnterFloatSpec) + expandVertically(AnimationEnterSizeSpec)
private val DockedExitTransition: ExitTransition =
    fadeOut(AnimationExitFloatSpec) + shrinkVertically(AnimationExitSizeSpec)


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun AutoCompleteTextFieldPreview() {
    XentlyTheme(wrapSurfaceHeight = true) {
        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
            AutoCompleteTextField(
                query = "suggestion",
                onQueryChange = {},
                active = false,
                onActiveChange = {},
                trailingIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                },
                label = {
                    Text(text = "Suggest")
                },
                supportingText = {
                    Text(text = "Supporting text...")
                },
            ) {
                Column {
                    List(3) {
                        ListItem(headlineContent = { Text(text = "Suggestion #${it + 1}") })
                    }
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun AutoCompleteTextFieldActivePreview() {
    XentlyTheme(wrapSurfaceHeight = true) {
        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
            AutoCompleteTextField(
                query = "suggestion",
                onQueryChange = {},
                active = true,
                onActiveChange = {},
                label = {
                    Text(text = "Suggest")
                },
                supportingText = {
                    Text(text = "Supporting text...")
                },
            ) {
                Column {
                    List(3) {
                        ListItem(headlineContent = { Text(text = "Suggestion #${it + 1}") })
                    }
                }
            }
        }
    }
}
