package com.randos.data.di

import android.content.SharedPreferences
import com.randos.data.manager.SettingsManagerImpl
import com.randos.domain.manager.SettingsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object ManagerModule {

    @Provides
    fun provideSettingsManager(sharedPreferences: SharedPreferences): SettingsManager {
        return SettingsManagerImpl(sharedPreferences)
    }
}