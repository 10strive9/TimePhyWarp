package com.phywarp.time.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TimerRecord::class, PresetTag::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timerDao(): TimerDao
    abstract fun presetTagDao(): PresetTagDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 数据库升级：新增 tag 字段到 timer_records，新增 preset_tags 表
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. 添加 tag 列
                db.execSQL("ALTER TABLE timer_records ADD COLUMN tag TEXT")
                // 2. 创建 preset_tags 表
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS preset_tags (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        orderIndex INTEGER NOT NULL DEFAULT 0,
                        color INTEGER
                    )
                """)
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "timer_database"
                )
                    .addMigrations(MIGRATION_1_2) // 添加升级
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
