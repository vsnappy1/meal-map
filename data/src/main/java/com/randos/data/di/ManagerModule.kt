package com.randos.data.di

import com.randos.data.manager.GroceryListManagerImpl
import com.randos.data.manager.SettingsManagerImpl
import com.randos.domain.manager.GroceryListManager
import com.randos.domain.manager.SettingsManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ManagerModule {

    @Binds
    abstract fun provideSettingsManager(settingsManagerImpl: SettingsManagerImpl): SettingsManager

    @Binds
    abstract fun provideGroceryListManager(groceryListManagerImpl: GroceryListManagerImpl): GroceryListManager
}
