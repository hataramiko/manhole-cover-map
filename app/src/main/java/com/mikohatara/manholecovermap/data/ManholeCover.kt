package com.mikohatara.manholecovermap.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manhole_covers")
data class ManholeCover(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "image_path") val imagePath: String?,
    @ColumnInfo(name = "country") val country: String?,
    @ColumnInfo(name = "region") val region: String?,
    @ColumnInfo(name = "city") val city: String?,
)
