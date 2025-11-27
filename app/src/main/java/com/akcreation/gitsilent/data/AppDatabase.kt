
package com.akcreation.gitsilent.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.akcreation.gitsilent.data.dao.CredentialDao
import com.akcreation.gitsilent.data.dao.DomainCredentialDao
import com.akcreation.gitsilent.data.dao.ErrorDao
import com.akcreation.gitsilent.data.dao.PassEncryptDao
import com.akcreation.gitsilent.data.dao.RemoteDao
import com.akcreation.gitsilent.data.dao.RepoDao
import com.akcreation.gitsilent.data.dao.SettingsDao
import com.akcreation.gitsilent.data.dao.StorageDirDao
import com.akcreation.gitsilent.data.entity.CredentialEntity
import com.akcreation.gitsilent.data.entity.DomainCredentialEntity
import com.akcreation.gitsilent.data.entity.ErrorEntity
import com.akcreation.gitsilent.data.entity.PassEncryptEntity
import com.akcreation.gitsilent.data.entity.RemoteEntity
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.data.entity.SettingsEntity
import com.akcreation.gitsilent.data.entity.StorageDirEntity
import com.akcreation.gitsilent.data.migration.MIGRATION_16_17
import com.akcreation.gitsilent.data.migration.MIGRATION_17_18
import com.akcreation.gitsilent.data.migration.MIGRATION_18_19
import com.akcreation.gitsilent.data.migration.MIGRATION_19_20
import com.akcreation.gitsilent.data.migration.MIGRATION_20_21
import com.akcreation.gitsilent.data.migration.MIGRATION_21_22
import com.akcreation.gitsilent.data.migration.MIGRATION_22_23
import com.akcreation.gitsilent.data.migration.MIGRATION_23_24
import com.akcreation.gitsilent.data.migration.MIGRATION_24_25

@Database(entities = [
                        RepoEntity::class,
                        ErrorEntity::class,
                        CredentialEntity::class,
                        RemoteEntity::class,
                        SettingsEntity::class,
                        PassEncryptEntity::class,
                        StorageDirEntity::class,
                        DomainCredentialEntity::class
                     ],
    version = 25,
    exportSchema = true,
    autoMigrations = [
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun errorDao(): ErrorDao
    abstract fun credentialDao(): CredentialDao
    abstract fun remoteDao(): RemoteDao
    abstract fun settingsDao(): SettingsDao
    abstract fun passEncryptDao(): PassEncryptDao
    abstract fun storageDirDao(): StorageDirDao
    abstract fun domainCredentialDao(): DomainCredentialDao
    companion object {
        @Volatile
        private var Instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "puppygitdb")
                    .addMigrations(
                        MIGRATION_16_17,
                        MIGRATION_17_18,
                        MIGRATION_18_19,
                        MIGRATION_19_20,
                        MIGRATION_20_21,
                        MIGRATION_21_22,
                        MIGRATION_22_23,
                        MIGRATION_23_24,
                        MIGRATION_24_25
                        )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
