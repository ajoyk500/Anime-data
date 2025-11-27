package com.akcreation.gitsilent.data.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `repo` ADD COLUMN `parentRepoId` TEXT NOT NULL DEFAULT ''")
    }
}
