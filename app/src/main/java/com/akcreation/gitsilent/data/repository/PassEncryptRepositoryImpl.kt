
package com.akcreation.gitsilent.data.repository

import androidx.room.withTransaction
import com.akcreation.gitsilent.data.dao.PassEncryptDao
import com.akcreation.gitsilent.data.entity.PassEncryptEntity
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.encrypt.PassEncryptHelper

private const val TAG = "PassEncryptRepositoryImpl"
class PassEncryptRepositoryImpl(private val dao: PassEncryptDao) : PassEncryptRepository {
}
