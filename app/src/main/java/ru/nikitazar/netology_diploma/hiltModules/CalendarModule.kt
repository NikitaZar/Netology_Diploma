package ru.nikitazar.netology_diploma.hiltModules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CalendarModule {

    const val HOUR_PER_DAY = 24
    const val MINUTE_PER_HOUR = 60
    const val SEC_PER_MINUTE = 60
    const val MS_PER_SEC = 1000

    @Provides
    @Singleton
    fun provideCalendar() = Calendar.getInstance()
}