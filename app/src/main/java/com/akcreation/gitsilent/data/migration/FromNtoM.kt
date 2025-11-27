package com.akcreation.gitsilent.data.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.akcreation.gitsilent.constants.StorageDirCons

val MIGRATION_17_18 = object : Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE repo ADD COLUMN upstreamBranch TEXT NOT NULL DEFAULT ''")
    }
}
val MIGRATION_18_19 = object : Migration(18, 19) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE repo ADD COLUMN ahead INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE repo ADD COLUMN behind INTEGER NOT NULL DEFAULT 0")
    }
}
val MIGRATION_19_20 = object : Migration(19, 20) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val TABLE_NAME = "storageDir"
        val createStorageDirTableSql = "CREATE TABLE IF NOT EXISTS `${TABLE_NAME}` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `fullPath` TEXT NOT NULL, `type` INTEGER NOT NULL, `allowDel` INTEGER NOT NULL, `parentId` TEXT NOT NULL, `baseStatus` INTEGER NOT NULL, `baseCreateTime` INTEGER NOT NULL, `baseUpdateTime` INTEGER NOT NULL, `baseIsDel` INTEGER NOT NULL, PRIMARY KEY(`id`))"
        db.execSQL(createStorageDirTableSql)
        db.execSQL("ALTER TABLE repo ADD COLUMN storageDirId TEXT NOT NULL DEFAULT '${StorageDirCons.DefaultStorageDir.puppyGitRepos.id}'")
    }
}
val MIGRATION_20_21 = object : Migration(20, 21) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE storageDir ADD COLUMN virtualPath TEXT NOT NULL DEFAULT ''")
    }
}
val MIGRATION_21_22 = object : Migration(21, 22) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE remote ADD COLUMN pushUrl TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE remote ADD COLUMN pushCredentialId TEXT NOT NULL DEFAULT ''")
    }
}
val MIGRATION_22_23 = object : Migration(22, 23) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val TABLE_NAME = "domain_credential"
        val createTableSql = "CREATE TABLE IF NOT EXISTS `${TABLE_NAME}` (`id` TEXT NOT NULL, `domain` TEXT NOT NULL, `credentialId` TEXT NOT NULL, `baseStatus` INTEGER NOT NULL, `baseCreateTime` INTEGER NOT NULL, `baseUpdateTime` INTEGER NOT NULL, `baseIsDel` INTEGER NOT NULL, PRIMARY KEY(`id`))"
        db.execSQL(createTableSql)
    }
}
val MIGRATION_23_24 = object : Migration(23, 24) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE domain_credential ADD COLUMN sshCredentialId TEXT NOT NULL DEFAULT ''")
    }
}
val MIGRATION_24_25 = object : Migration(24, 25) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE credential ADD COLUMN encryptVer INTEGER NOT NULL DEFAULT 5")
    }
}
