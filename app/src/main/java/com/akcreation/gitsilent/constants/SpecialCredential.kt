package com.akcreation.gitsilent.constants

import com.akcreation.gitsilent.data.entity.CredentialEntity

object SpecialCredential {
    object MatchByDomain {
        const val credentialId = "match_by_domain"
        const val name = "Match By Domain"
        const val type = Cons.dbCredentialTypeHttp
        private val entity = CredentialEntity(id= credentialId, name = name, type = type)
        fun getEntityCopy():CredentialEntity {
            return entity.copy()
        }
        fun equals_to(other:CredentialEntity):Boolean {
            return SpecialCredential.equals_to(entity, other)
        }
        fun id_equals_to(otherId:String):Boolean {
            return otherId == credentialId
        }
    }
    object NONE {
        const val credentialId = ""  
        const val name = "NONE"  
        const val type = Cons.dbCredentialTypeHttp
        private val entity = CredentialEntity(id= credentialId, name = name, type = type)
        fun getEntityCopy():CredentialEntity {
            return entity.copy()
        }
        fun equals_to(other:CredentialEntity):Boolean {
            return SpecialCredential.equals_to(entity, other)
        }
        fun id_equals_to(otherId:String):Boolean {
            return otherId == credentialId
        }
    }
    fun isAllowedCredentialName(name:String):Boolean {
        return name.isNotBlank() && name != NONE.name && name != MatchByDomain.name
    }
    private fun equals_to(_this:CredentialEntity, other:CredentialEntity):Boolean {
        return _this.name == other.name && _this.id == other.id && _this.type == other.type
    }
}
