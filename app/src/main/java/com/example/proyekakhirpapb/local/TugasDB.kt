package com.example.proyekakhirpapb.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Tugas::class], version = 5, exportSchema = false)
abstract class TugasDB : RoomDatabase() {
    abstract fun tugasDao(): TugasDAO

    companion object {
        @Volatile
        private var INSTANCE: TugasDB? = null

        // Migrasi dari versi 4 ke versi 5
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Menambahkan kolom 'prioritas' dengan tipe TEXT dan nilai default 'undefined'
                database.execSQL("ALTER TABLE tugas ADD COLUMN prioritas TEXT DEFAULT 'undefined'")
            }
        }

        fun getDatabase(context: Context): TugasDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TugasDB::class.java,
                    "tugas_database"
                )
                    .addMigrations(MIGRATION_4_5) // Menambahkan migrasi dari versi 4 ke 5
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
