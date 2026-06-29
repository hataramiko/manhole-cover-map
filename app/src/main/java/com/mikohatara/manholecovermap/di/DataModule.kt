package com.mikohatara.manholecovermap.di

import com.mikohatara.manholecovermap.data.ManholeCoverRepository
import com.mikohatara.manholecovermap.data.OfflineManholeCoverRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsManholeCoverRepository(
        manholeCoverRepository: OfflineManholeCoverRepository
    ): ManholeCoverRepository
}
