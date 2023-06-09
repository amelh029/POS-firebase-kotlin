package com.example.myapplication.view.outcomes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.myapplication.R
import com.example.myapplication.utils.tools.helper.ReportsParameter
import com.example.myapplication.view.ui.theme.POSTheme
import com.example.myapplication.view.viewModel.MainViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker

class OutcomesActivity  : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    companion object {
        private const val EXTRA_REPORT = "extra_report"
        fun createInstanceForRecap(context: Context, parameters: ReportsParameter? = null) {
            val intent = Intent(context, OutcomesActivity::class.java)
            parameters?.let { intent.putExtra(EXTRA_REPORT, it) }
            context.startActivity(intent)
        }
    }

    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = MainViewModel.getMainViewModel(this)

        val report = getExtraReport() ?: ReportsParameter.createTodayOnly(true)

        setContent {
            POSTheme {
                ProvideWindowInsets(
                    windowInsetsAnimationsEnabled = true
                ) {
                    OutComesView(
                        mainViewModel = mainViewModel,
                        timePicker = buildTimePicker(),
                        datePicker = buildDatePicker(),
                        fragmentManager = supportFragmentManager,
                        parameters = report,
                        onBackClicked = {
                            onBackPressed()
                        }
                    )
                }
            }
        }
    }

    private fun buildTimePicker() = MaterialTimePicker.Builder()
        .setTitleText(R.string.select_time)

    private fun buildDatePicker() = MaterialDatePicker.Builder.datePicker()
        .setCalendarConstraints(buildConstraint())
        .setTitleText(R.string.select_date)
        .setPositiveButtonText(R.string.select)

    private fun buildConstraint() = CalendarConstraints.Builder()
        .setValidator(DateValidatorPointBackward.now())
        .build()

    private fun getExtraReport() = intent.getSerializableExtra(EXTRA_REPORT) as? ReportsParameter
}
