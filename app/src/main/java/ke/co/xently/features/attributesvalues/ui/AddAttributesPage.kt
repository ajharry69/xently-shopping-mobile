package ke.co.xently.features.attributesvalues.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.co.xently.R
import ke.co.xently.features.attributes.models.Attribute
import ke.co.xently.features.attributes.ui.LocalAttributeAutoCompleteService
import ke.co.xently.features.attributesvalues.models.AttributeValue
import ke.co.xently.features.core.javaLocale
import ke.co.xently.features.core.ui.MultiStepScreen
import ke.co.xently.features.core.ui.rememberAutoCompleteTextFieldState
import ke.co.xently.features.products.models.Product
import ke.co.xently.features.products.ui.components.AddProductAutoCompleteTextField
import ke.co.xently.ui.theme.XentlyTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddAttributesPage(
    modifier: Modifier = Modifier,
    attributes: List<AttributeValue>,
    saveDraft: (List<AttributeValue>) -> Unit,
    onPreviousClick: () -> Unit,
    onContinueClick: (List<AttributeValue>) -> Unit,
) {
    val nameAutoCompleteState = rememberAutoCompleteTextFieldState()
    val valueAutoCompleteState = rememberAutoCompleteTextFieldState()

    val attrs = remember {
        mutableStateListOf(*attributes.toTypedArray())
    }

    LaunchedEffect(attrs) {
        saveDraft(attrs)
    }

    MultiStepScreen(
        modifier = modifier,
        heading = R.string.xently_add_attributes_page_title,
        onBackClick = onPreviousClick,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onContinueClick(attrs)
                },
            ) {
                Text(text = stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val isNameAndValueProvided by remember(
                nameAutoCompleteState.query,
                valueAutoCompleteState.query,
            ) {
                derivedStateOf {
                    nameAutoCompleteState.query.isNotBlank()
                            && valueAutoCompleteState.query.isNotBlank()
                }
            }
            val savableAttribute: (AttributeValue) -> AttributeValue.LocalViewModel by rememberUpdatedState {
                it.toLocalViewModel().run {
                    copy(
                        value = valueAutoCompleteState.query.trim()
                            .lowercase(Locale.current.javaLocale),
                        attribute = attribute.copy(
                            name = nameAutoCompleteState.query.trim()
                                .lowercase(Locale.current.javaLocale),
                        ),
                    )
                }.also {
                    nameAutoCompleteState.resetQuery()
                    valueAutoCompleteState.resetQuery()
                }
            }

            val trailingIcon: (@Composable () -> Unit)? = if (isNameAndValueProvided) {
                {
                    IconButton(
                        onClick = {
                            savableAttribute(AttributeValue.LocalViewModel.default)
                                .let(attrs::add)
                        },
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.xently_content_description_add_attribute),
                        )
                    }
                }
            } else null

            AddProductAutoCompleteTextField<Attribute, Attribute>(
                state = nameAutoCompleteState,
                service = LocalAttributeAutoCompleteService.current,
                modifier = Modifier.weight(1f),
                trailingIcon = trailingIcon,
                onSearch = { query ->
                    Attribute.LocalViewModel.default.copy(name = query)
                },
                label = {
                    Text(text = stringResource(R.string.xently_search_bar_placeholder_name))
                },
                onSuggestionSelected = {
                    nameAutoCompleteState.updateQuery(it.toString())
                    if (isNameAndValueProvided) {
                        savableAttribute(AttributeValue.LocalViewModel.default)
                            .copy(attribute = it.toLocalViewModel())
                            .let(attrs::add)
                    }
                },
                suggestionContent = {
                    Text(
                        text = if (isNameAndValueProvided) {
                            AttributeValue.LocalViewModel.default.copy(
                                attribute = it.toLocalViewModel(),
                                value = valueAutoCompleteState.query,
                            ).toString()
                        } else {
                            it.toString()
                        },
                    )
                },
                supportingText = {
                    Text(text = stringResource(R.string.xently_text_field_help_text_attribute_name))
                },
            )

            AddProductAutoCompleteTextField<AttributeValue, AttributeValue>(
                state = valueAutoCompleteState,
                service = LocalAttributeValueAutoCompleteService.current,
                modifier = Modifier.weight(1f),
                trailingIcon = trailingIcon,
                onSearch = { query ->
                    AttributeValue.LocalViewModel.default.run {
                        copy(
                            value = query,
                            attribute = attribute.copy(name = nameAutoCompleteState.query),
                        )
                    }
                },
                label = {
                    Text(text = stringResource(R.string.xently_search_bar_placeholder_value))
                },
                onSuggestionSelected = {
                    valueAutoCompleteState.updateQuery(it.value)
                    if (isNameAndValueProvided) {
                        savableAttribute(it)
                            .copy(value = it.value)
                            .run {
                                if (it.attribute.name.isBlank()) {
                                    // The attribute was constructed through the query,
                                    // no need to override
                                    this
                                } else {
                                    copy(attribute = it.attribute.toLocalViewModel())
                                }
                            }
                            .let(attrs::add)
                    }
                },
                suggestionContent = {
                    Text(
                        text = if (it.attribute.toString().isBlank()) {
                            it.value
                        } else {
                            it.toString()
                        },
                    )
                },
                supportingText = {
                    Text(text = stringResource(R.string.xently_text_field_help_text_attribute_value))
                },
            )
        }

        val visible by remember {
            derivedStateOf {
                attrs.isNotEmpty()
            }
        }

        AnimatedVisibility(visible = visible, modifier = Modifier.fillMaxWidth()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for ((i, attribute) in attrs.withIndex()) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(text = attribute.toString())
                        },
                        trailingIcon = {
                            IconButton(onClick = { attrs.removeAt(i) }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(
                                        R.string.xently_content_description_remove_item,
                                        attribute,
                                    ),
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun AddAttributesPagePreview() {
    XentlyTheme {
        AddAttributesPage(
            modifier = Modifier.fillMaxSize(),
            attributes = Product.LocalViewModel.default.attributes,
            saveDraft = {},
            onPreviousClick = {},
        ) {}
    }
}