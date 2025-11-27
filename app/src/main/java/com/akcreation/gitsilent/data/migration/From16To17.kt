package com.akcreation.gitsilent.data.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE repo ADD COLUMN isDetached INTEGER NOT NULL DEFAULT 0")
    }
}
