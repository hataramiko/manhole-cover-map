package com.mikohatara.manholecovermap.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ManholeCoverDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertManholeCover(manholeCover: ManholeCover): Long

    @Update
    suspend fun updateManholeCover(manholeCover: ManholeCover)

    @Delete
    suspend fun deleteManholeCover(manholeCover: ManholeCover)

    @Query("SELECT * from manhole_covers ORDER BY country ASC")
    fun getAllManholeCovers(): Flow<List<ManholeCover>>

    @Query("SELECT * from manhole_covers WHERE id = :id")
    fun getManholeCover(id: Int): Flow<ManholeCover?>
}
