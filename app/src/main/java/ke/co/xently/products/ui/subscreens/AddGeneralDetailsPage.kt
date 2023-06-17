package ke.co.xently.products.ui.subscreens

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import ke.co.xently.R
import ke.co.xently.products.models.Product
import ke.co.xently.products.ui.components.AddProductPage
import ke.co.xently.ui.javaLocale
import java.math.BigDecimal
import java.time.Clock
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGeneralDetailsPage(
    modifier: Modifier = Modifier,
    product: Product,
    onPreviousClick: () -> Unit,
    onContinueClick: (Product) -> Unit,
) {
    val context = LocalContext.current
    val dateFormat = remember(context) {
        DateFormat.getDateFormat(context)
    }
    val timeFormat = remember(context) {
        DateFormat.getTimeFormat(context)
    }
    val zoneOffset = remember(product.datePurchased) {
        try {
            ZoneOffset.from(product.datePurchased)
        } catch (ex: DateTimeException) {
            ZoneOffset.ofHours(3)
        }
    }
    val datePurchasedEpochSeconds = remember(zoneOffset) {
        product.datePurchased.toEpochSecond(zoneOffset)
    }
    var packCount by remember(product.packCount) {
        mutableStateOf(TextFieldValue(product.packCount.toString()))
    }
    var unitPrice by remember(product.unitPrice) {
        mutableStateOf(
            TextFieldValue(product.unitPrice.takeIf { it > BigDecimal.ZERO }?.toString() ?: "")
        )
    }
    var datePurchasedInput by remember(dateFormat, datePurchasedEpochSeconds) {
        val instant = Instant.ofEpochSecond(datePurchasedEpochSeconds)
        mutableStateOf(TextFieldValue(dateFormat.format(Date.from(instant))))
    }
    var timePurchasedInput by remember(timeFormat, datePurchasedEpochSeconds) {
        val instant = Instant.ofEpochSecond(datePurchasedEpochSeconds)
        mutableStateOf(TextFieldValue(timeFormat.format(Date.from(instant))))
    }
    val datePurchasedState = rememberDatePickerState(
        initialSelectedDateMillis = datePurchasedEpochSeconds * 1000,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Instant.now().atOffset(ZoneOffset.UTC)
                    .toEpochSecond() * 1000
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year <= Instant.now().atOffset(ZoneOffset.UTC).year
            }
        }
    )
    val timePurchasedState = rememberTimePickerState(
        initialHour = product.datePurchased.hour,
        initialMinute = product.datePurchased.minute,
    )

    var showDatePicker by remember {
        mutableStateOf(false)
    }
    AnimatedVisibility(visible = showDatePicker) {
        val onConfirmButtonClick = {
            val instant = datePurchasedState.selectedDateMillis?.let {
                Instant.ofEpochMilli(it)
            } ?: Instant.now()
            datePurchasedInput = TextFieldValue(dateFormat.format(Date.from(instant)))
            showDatePicker = false
        }
        DatePickerDialog(
            onDismissRequest = onConfirmButtonClick,
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(
                        text = stringResource(R.string.xently_date_time_picker_confirm_button_label)
                            .uppercase(Locale.current.javaLocale),
                    )
                }
            },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.xently_content_description_pick_date_of_purchase),
                    modifier = Modifier.padding(top = 16.dp),
                )
                Divider()
                DatePicker(state = datePurchasedState)
            }
        }
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }
    AnimatedVisibility(visible = showTimePicker) {
        val onConfirmButtonClick = {
            val instant = datePurchasedState.selectedDateMillis?.let {
                Instant.ofEpochMilli(it)
            } ?: Instant.now()

            val localTime =
                LocalTime.of(timePurchasedState.hour, timePurchasedState.minute)
            val localDate = LocalDate.now(Clock.fixed(instant, zoneOffset))

            val date =
                Date.from(instant.plusSeconds(localTime.toEpochSecond(localDate, zoneOffset)))
            timePurchasedInput = TextFieldValue(timeFormat.format(date))
            showTimePicker = false
        }
        DatePickerDialog(
            onDismissRequest = onConfirmButtonClick,
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(
                        text = stringResource(R.string.xently_date_time_picker_confirm_button_label)
                            .uppercase(Locale.current.javaLocale),
                    )
                }
            },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.xently_content_description_pick_time_of_purchase),
                    modifier = Modifier.padding(top = 16.dp),
                )
                Divider()
                TimePicker(state = timePurchasedState, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    AddProductPage(
        modifier = modifier,
        heading = R.string.xently_general_details_page_title,
        onBackClick = onPreviousClick,
        continueButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    product.toLocalViewModel().copy(
                        packCount = packCount.text.toIntOrNull() ?: 1,
                        unitPrice = unitPrice.text.toBigDecimalOrNull()
                            ?: BigDecimal.ONE, // TODO: Ensure value is provided
                    ).let(onContinueClick)
                },
            ) {
                Text(stringResource(R.string.xently_button_label_continue))
            }
        },
    ) {
        TextField(
            value = packCount,
            onValueChange = { packCount = it },
            label = {
                Text(stringResource(R.string.xently_text_field_label_pack_count))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        )
        TextField(
            value = unitPrice,
            onValueChange = { unitPrice = it },
            label = {
                Text(stringResource(R.string.xently_text_field_label_unit_price))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextField(
                readOnly = true,
                value = datePurchasedInput,
                modifier = Modifier.weight(1f),
                label = {
                    Text(text = stringResource(R.string.xently_text_field_label_date_purchased))
                },
                onValueChange = {},
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.xently_content_description_pick_date_of_purchase)
                        )
                    }
                },
            )
            TextField(
                readOnly = true,
                value = timePurchasedInput,
                modifier = Modifier.weight(1f),
                label = {
                    Text(text = stringResource(R.string.xently_text_field_label_time_purchased))
                },
                onValueChange = {},
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.xently_content_description_pick_time_of_purchase)
                        )
                    }
                },
            )
        }
    }
}