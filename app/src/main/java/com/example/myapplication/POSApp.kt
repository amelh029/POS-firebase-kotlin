package com.example.myapplication

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.myapplication.data.source.repository.SettingRepository
import com.example.myapplication.data.source.repository.implimentation.SettingRepositoryImpl
import com.example.myapplication.utils.config.DateUtils
import com.example.myapplication.view.viewModel.ApplicationViewModel
import java.util.*

class POSApp :Application() {

    private lateinit var viewModel: ApplicationViewModel

    override fun onCreate() {
        super.onCreate()

        setupViewModel()
        setupTheme()
        setupLocale()
    }

    private fun setupViewModel() {
        val settingRepository = SettingRepositoryImpl.getDataStoreInstance(applicationContext)
        viewModel = ApplicationViewModel(this, settingRepository)
    }

    private fun setupTheme() {
        viewModel.getDarkMode {
            val delegate = if (it){
                AppCompatDelegate.MODE_NIGHT_YES
            }else{
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(delegate)
        }
    }

    private fun setupLocale() {
        val config = Configuration()
        config.setLocale(DateUtils.locale)
        Locale.setDefault(DateUtils.locale)
        val res = applicationContext.resources
        res.updateConfiguration(config, res.displayMetrics)
    }
}