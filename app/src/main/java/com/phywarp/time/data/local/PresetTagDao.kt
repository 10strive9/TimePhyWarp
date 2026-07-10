package com.phywarp.time.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetTagDao {
    @Insert
    suspend fun insertTag(tag: PresetTag)

    @Query("SELECT * FROM preset_tags ORDER BY orderIndex ASC")
    fun getAllTags(): Flow<List<PresetTag>>

    @Query("DELETE FROM preset_tags WHERE id = :id")
    suspend fun deleteTag(id: Long)

    @Query("DELETE FROM preset_tags")
    suspend fun deleteAllTags()
}
