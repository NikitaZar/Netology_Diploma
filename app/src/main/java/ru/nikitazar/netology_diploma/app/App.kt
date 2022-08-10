package ru.nikitazar.netology_diploma.app

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.auth.AppAuth
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    private val appScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var auth: AppAuth

    override fun onCreate() {
        super.onCreate()
        setupAuth()

        val MAPKIT_API_KEY = applicationContext.getString(R.string.MAPKIT_API_KEY)
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
    }

    private fun setupAuth() {
        appScope.launch {
            auth.sendPushToken()
        }
    }
}