package com.mikohatara.manholecovermap.di

import android.content.Context
import androidx.room.Room
import com.mikohatara.manholecovermap.data.AppDatabase
import com.mikohatara.manholecovermap.data.ManholeCoverDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideManholeCoverDao(appDatabase: AppDatabase): ManholeCoverDao {
        return appDatabase.manholeCoverDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "ManholeCoverMapDatabase"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
}
