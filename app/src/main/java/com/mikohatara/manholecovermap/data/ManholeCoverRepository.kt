package com.mikohatara.manholecovermap.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ManholeCoverRepository {
    suspend fun addManholeCover(manholeCover: ManholeCover): Long

    suspend fun updateManholeCover(manholeCover: ManholeCover)

    suspend fun deleteManholeCover(manholeCover: ManholeCover)

    fun getAllManholeCoversStream(): Flow<List<ManholeCover>>

    fun getManholeCoverStream(id: Int): Flow<ManholeCover?>
}

class OfflineManholeCoverRepository @Inject constructor(
    private val manholeCoverDao: ManholeCoverDao
) : ManholeCoverRepository {
    override suspend fun addManholeCover(manholeCover: ManholeCover) = manholeCoverDao
        .insertManholeCover(manholeCover)

    override suspend fun updateManholeCover(manholeCover: ManholeCover) = manholeCoverDao
        .updateManholeCover(manholeCover)

    override suspend fun deleteManholeCover(manholeCover: ManholeCover) = manholeCoverDao
        .deleteManholeCover(manholeCover)

    override fun getAllManholeCoversStream(): Flow<List<ManholeCover>> = manholeCoverDao
        .getAllManholeCovers()

    override fun getManholeCoverStream(id: Int): Flow<ManholeCover?> {
        return manholeCoverDao.getManholeCover(id).map { it }
    }
}
