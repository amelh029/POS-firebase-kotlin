package com.example.myapplication.view.outcomes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.example.myapplication.R
import com.example.myapplication.compose.BasicEditText
import com.example.myapplication.compose.PrimaryButtonView
import com.example.myapplication.data.source.local.entity.room.master.Outcome
import com.example.myapplication.utils.config.DateUtils
import com.example.myapplication.view.ui.ThousandAndSuggestionVisualTransformation
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker

@Composable
@ExperimentalComposeUiApi
fun OutcomeDetail(
    date: String,
    isTodayDate: Boolean,
    timePicker: MaterialTimePicker.Builder,
    datePicker: MaterialDatePicker.Builder<Long>,
    outcome: Outcome? = null,
    fragmentManager: FragmentManager,
    onSubmitOutcome: (Outcome) -> Unit,
) {

    val isNewOutcome = outcome == null
    var selectedDateTime by remember { mutableStateOf(outcome?.date ?: date) }
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var total by remember { mutableStateOf(0L) }

    LaunchedEffect(key1 = "$date $outcome") {
        selectedDateTime = outcome?.date ?: date
        name = outcome?.name ?: ""
        desc = outcome?.desc ?: ""
        total = outcome?.total ?: 0L
    }

    fun submitOutcome(currentDate: String) {
        onSubmitOutcome(
            outcome?.copy(
                name = name,
                desc = desc,
                price = total,
                date = currentDate
            ) ?: Outcome(
                name = name,
                desc = desc,
                price = total,
                date = currentDate
            )
        )
        name = ""
        desc = ""
        total = 0L
    }

    fun selectTime() {
        val hourMinute = DateUtils.strToHourAndMinute(selectedDateTime)
        val picker = timePicker.setHour(hourMinute.first)
            .setMinute(hourMinute.second)
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .build()
        picker.addOnPositiveButtonClickListener {
            selectedDateTime = DateUtils.strDateTimeReplaceTime(
                dateTime = selectedDateTime,
                hour = picker.hour,
                minute = picker.minute
            )
            picker.dismiss()
            if (isNewOutcome) submitOutcome(selectedDateTime)
        }
        picker.show(fragmentManager, "")
    }

    fun selectDate() {
        val time = DateUtils.strToDate(selectedDateTime).time
        val picker =
            datePicker.setSelection(time)
                .build()
        picker.addOnPositiveButtonClickListener {
            val newDate = DateUtils.millisToDate(
                millis = it,
                isWithTime = true
            )
            selectedDateTime = DateUtils.strDateTimeReplaceDate(
                oldDate = selectedDateTime,
                newDate = newDate
            )
            picker.dismiss()
            selectTime()
        }
        picker.show(fragmentManager, "")
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        val title = if (isTodayDate || !isNewOutcome) {
            DateUtils.convertDateFromDb(
                date = selectedDateTime,
                format = DateUtils.DATE_WITH_DAY_AND_TIME_FORMAT
            )
        } else {
            DateUtils.convertDateFromDb(
                date = selectedDateTime,
                format = DateUtils.DATE_WITH_DAY_FORMAT
            )
        }
        Text(
            modifier = Modifier
                .run {
                    return@run if (!isNewOutcome) {
                        clickable {
                            selectDate()
                        }
                    } else { this }
                },
            text = title,
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicEditText(
            value = name,
            placeHolder = stringResource(R.string.outcome_title),
            onValueChange = {
                name = it
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicEditText(
            value = desc,
            placeHolder = stringResource(R.string.deskripsi_optional),
            onValueChange = {
                desc = it
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicEditText(
            value = if (total == 0L) "" else total.toString(),
            keyboardType = KeyboardType.Number,
            visualTransformation = ThousandAndSuggestionVisualTransformation(false),
            placeHolder = stringResource(R.string.total_outcome),
            onValueChange = {
                total = it.toLongOrNull() ?: 0L
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButtonView(
            modifier = Modifier
                .fillMaxWidth(),
            buttonText = stringResource(
                id = if (isNewOutcome) R.string.adding else R.string.save
            ),
            isEnabled = name.isNotEmpty() && total != 0L,
            onClick = {
                if (isTodayDate || !isNewOutcome)
                    submitOutcome(selectedDateTime)
                else
                    selectTime()
            }
        )
    }
}
