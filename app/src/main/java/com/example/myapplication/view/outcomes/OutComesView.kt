package com.example.myapplication.view.outcomes


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.example.myapplication.R
import com.example.myapplication.compose.BasicAddButton
import com.example.myapplication.compose.BasicEmptyList
import com.example.myapplication.compose.BasicTopBar
import com.example.myapplication.compose.SpaceForFloatingButton
import com.example.myapplication.data.source.local.entity.room.master.Outcome
import com.example.myapplication.utils.config.DateUtils
import com.example.myapplication.utils.config.thousand
import com.example.myapplication.utils.tools.helper.ReportsParameter
import com.example.myapplication.view.viewModel.MainViewModel
import com.google.android.filament.Box
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.coroutines.launch


@Composable
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
fun OutComesView(
    mainViewModel: MainViewModel,
    timePicker: MaterialTimePicker.Builder,
    datePicker: MaterialDatePicker.Builder<Long>,
    fragmentManager: FragmentManager,
    parameters: ReportsParameter,
    onBackClicked: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(parameters) }

    val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var selectedOutcome by remember { mutableStateOf<Outcome?>(null) }

    if (modalState.currentValue == ModalBottomSheetValue.Hidden) {
        LocalSoftwareKeyboardController.current?.hide()
    }

    fun onAddClicked() {
        scope.launch {
            selectedOutcome = null
            modalState.show()
        }
    }

    fun onDateClicked() {
        val picker =
            datePicker.setSelection(DateUtils.strToDate(selectedDate.start).time)
                .build()
        picker.addOnPositiveButtonClickListener {
            val newDate = DateUtils.millisToDate(
                millis = it,
                isWithTime = true
            )
            selectedDate = selectedDate.copy(
                start = newDate,
                end = newDate
            )
            picker.dismiss()
        }
        picker.show(fragmentManager, "")
    }

    if (selectedDate.isTodayOnly()) {
        ModalBottomSheetLayout(
            sheetState = modalState,
            sheetShape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp
            ),
            sheetContent = {
                OutcomeDetail(
                    date = selectedDate.start,
                    isTodayDate = DateUtils.isDateTimeIsToday(selectedDate.start),
                    timePicker = timePicker,
                    datePicker = datePicker,
                    outcome = selectedOutcome,
                    fragmentManager = fragmentManager,
                    onSubmitOutcome = {
                        scope.launch {
                            mainViewModel.addNewOutcome(it)
                            modalState.hide()
                        }
                    }
                )
            },
            content = {
                OutcomesContent(
                    mainViewModel = mainViewModel,
                    parameters = selectedDate,
                    onAddClicked = {
                        onAddClicked()
                    },
                    onDateClicked = {
                        onDateClicked()
                    },
                    onBackClicked = onBackClicked,
                    onItemClicked = {
                        scope.launch {
                            selectedOutcome = it
                            modalState.show()
                        }
                    }
                )
            }
        )
    } else {
        OutcomesContent(
            mainViewModel = mainViewModel,
            parameters = selectedDate,
            onBackClicked = onBackClicked
        )
    }
}

@Composable
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
private fun OutcomesContent(
    mainViewModel: MainViewModel,
    parameters: ReportsParameter,
    onAddClicked: (() -> Unit)? = null,
    onDateClicked: (() -> Unit)? = null,
    onBackClicked: () -> Unit,
    onItemClicked: ((Outcome) -> Unit)? = null
) {
    Scaffold(
        topBar = {
            BasicTopBar(
                titleText = stringResource(id = R.string.outcome),
                onBackClicked = onBackClicked
            )
        },
        content = { padding ->
            Column {
                OutcomeHeader(
                    parameters = parameters,
                    onDateClicked = onDateClicked
                )
                Outcomes(
                    modifier = Modifier
                        .padding(padding),
                    mainViewModel = mainViewModel,
                    parameters = parameters,
                    onAddClicked = onAddClicked,
                    onItemClicked = onItemClicked
                )
            }
        }
    )
}

@Composable
private fun Outcomes(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    parameters: ReportsParameter,
    onAddClicked: (() -> Unit)? = null,
    onItemClicked: ((Outcome) -> Unit)? = null
) {

    val outcomes = mainViewModel.getOutcome(parameters).collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val listState = rememberLazyListState()

        if (outcomes.value.isNotEmpty()) {

            LazyColumn(
                modifier = modifier
                    .align(Alignment.TopCenter),
                state = listState
            ) {

                items(outcomes.value) {
                    OutcomeItem(
                        outcome = it,
                        isTodayOnly = parameters.isTodayOnly(),
                        onItemClicked = onItemClicked
                    )
                }

                item { SpaceForFloatingButton() }
            }
        } else {
            BasicEmptyList(imageId = R.drawable.ic_no_data, text = R.string.no_data_found)
        }

        onAddClicked?.let {
            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                visible = !listState.isScrollInProgress,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                BasicAddButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd),
                    onAddClicked = it
                )
            }
        }
    }
}

@Composable
private fun OutcomeHeader(
    parameters: ReportsParameter,
    onDateClicked: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface)
            .run {
                return@run if (onDateClicked != null) {
                    clickable {
                        onDateClicked()
                    }
                } else {
                    this
                }
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(
                    if (parameters.isTodayOnly()) R.string.date else R.string.from
                ),
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            if (!parameters.isTodayOnly()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.until),
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = DateUtils.convertDateFromDate(
                    parameters.start,
                    DateUtils.DATE_WITH_DAY_FORMAT
                ),
                style = MaterialTheme.typography.body1
            )
            if (!parameters.isTodayOnly()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = DateUtils.convertDateFromDate(
                        parameters.end,
                        DateUtils.DATE_WITH_DAY_FORMAT
                    ),
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun OutcomeItem(
    outcome: Outcome,
    isTodayOnly: Boolean,
    onItemClicked: ((Outcome) -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface)
            .run {
                return@run if (onItemClicked != null) {
                    clickable {
                        onItemClicked(outcome)
                    }
                } else { this }
            }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            if (!isTodayOnly) {
                Text(
                    text = DateUtils.convertDateFromDb(
                        date = outcome.date,
                        DateUtils.DATE_WITH_DAY_WITHOUT_YEAR_FORMAT
                    ),
                    style = MaterialTheme.typography.body2.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = DateUtils.convertDateFromDb(
                    date = outcome.date,
                    DateUtils.HOUR_AND_TIME_FORMAT
                ),
                style = MaterialTheme.typography.body2
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = outcome.name,
                style = MaterialTheme.typography.body2.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = outcome.desc,
                style = MaterialTheme.typography.body2
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = "Rp. ${outcome.total.thousand()}",
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}
