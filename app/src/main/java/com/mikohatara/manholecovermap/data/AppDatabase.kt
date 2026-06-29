package com.mikohatara.manholecovermap.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        ManholeCover::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun manholeCoverDao(): ManholeCoverDao
}
