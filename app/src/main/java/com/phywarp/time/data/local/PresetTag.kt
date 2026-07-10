package com.phywarp.time.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preset_tags")
data class PresetTag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val orderIndex: Int = 0,
    val color: Int? = null // 预留颜色字段
)
