package com.akcreation.gitsilent.data.migration.auto

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase

@DeleteColumn(tableName = "repo", columnName = "testMigra")
class From4To5DelRepoColumn : AutoMigrationSpec {
    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        super.onPostMigrate(db)
    }
}