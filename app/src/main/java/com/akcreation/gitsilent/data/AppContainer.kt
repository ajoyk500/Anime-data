
package com.akcreation.gitsilent.data

import android.content.Context
import com.akcreation.gitsilent.data.repository.CredentialRepository
import com.akcreation.gitsilent.data.repository.CredentialRepositoryImpl
import com.akcreation.gitsilent.data.repository.DomainCredentialRepository
import com.akcreation.gitsilent.data.repository.DomainCredentialRepositoryImpl
import com.akcreation.gitsilent.data.repository.ErrorRepository
import com.akcreation.gitsilent.data.repository.ErrorRepositoryImpl
import com.akcreation.gitsilent.data.repository.PassEncryptRepository
import com.akcreation.gitsilent.data.repository.PassEncryptRepositoryImpl
import com.akcreation.gitsilent.data.repository.RemoteRepository
import com.akcreation.gitsilent.data.repository.RemoteRepositoryImpl
import com.akcreation.gitsilent.data.repository.RepoRepository
import com.akcreation.gitsilent.data.repository.RepoRepositoryImpl
import com.akcreation.gitsilent.data.repository.SettingsRepository
import com.akcreation.gitsilent.data.repository.SettingsRepositoryImpl
import com.akcreation.gitsilent.data.repository.StorageDirRepository
import com.akcreation.gitsilent.data.repository.StorageDirRepositoryImpl

interface AppContainer {
    val db:AppDatabase
    val repoRepository: RepoRepository
    val errorRepository: ErrorRepository
    val credentialRepository: CredentialRepository
    val remoteRepository: RemoteRepository
    @Deprecated("[CHINESE]")
    val settingsRepository: SettingsRepository
    @Deprecated("20241205 [CHINESE]，[CHINESE]，[CHINESE]，[CHINESE]app")
    val passEncryptRepository: PassEncryptRepository
    @Deprecated("[CHINESE]")
    val storageDirRepository: StorageDirRepository
    val domainCredentialRepository: DomainCredentialRepository
}
class AppDataContainer(private val context: Context) : AppContainer {
    override val db: AppDatabase = AppDatabase.getDatabase(context)
    override val repoRepository: RepoRepository by lazy {
        RepoRepositoryImpl(db.repoDao())
    }
    override val errorRepository: ErrorRepository by lazy {
        ErrorRepositoryImpl(db.errorDao())
    }
    override val credentialRepository: CredentialRepository by lazy {
        CredentialRepositoryImpl(db.credentialDao())
    }
    override val remoteRepository: RemoteRepository by lazy {
        RemoteRepositoryImpl(db.remoteDao())
    }
    @Deprecated("[CHINESE]")
    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(db.settingsDao())
    }
    @Deprecated("[CHINESE]")
    override val passEncryptRepository: PassEncryptRepository by lazy {
        PassEncryptRepositoryImpl(db.passEncryptDao())
    }
    @Deprecated("[CHINESE]")
    override val storageDirRepository: StorageDirRepository by lazy {
        StorageDirRepositoryImpl(db.storageDirDao())
    }
    override val domainCredentialRepository: DomainCredentialRepository by lazy {
        DomainCredentialRepositoryImpl(db.domainCredentialDao())
    }
}
