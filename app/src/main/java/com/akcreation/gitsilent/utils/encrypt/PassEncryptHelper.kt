package com.akcreation.gitsilent.utils.encrypt


class PassEncryptHelper {
    companion object {
        val passEncryptCurrentVer = 5
        val keyMap:Map<Int,String> = mapOf(
            Pair(1, "3LHLpwTQ9uEyP9MCqgYNqncKxmQJsww9L4A7T7wK"),
            Pair(2, "ffHuzkprZY9b5PbYxaHPgHZ5UJxsqsL5MjqvCn7rQH3q7p7shz"),
            Pair(3, "qaWxActsnqiD2D5CmYroUcMRjYr4KDAiiNYHPs2RVs7DLTcU3y"),
            Pair(4, "C8mNzgW5Pwq3bFcaHP2WrwtZXA9bWniKgz9SeKRHxDbTyJ9LnZ"),
            Pair(5, "Sy7JW_S4N3Fq5xFPzSK7tNvfXeUFRqUJaC4hmyjNm_XtfbK4dW"),
        )
        val encryptorMap:Map<Int,Encryptor> = mapOf(
            Pair(1, encryptor_ver_1),
            Pair(2, encryptor_ver_2),
            Pair(3, encryptor_ver_3),
            Pair(4, encryptor_ver_4),
            Pair(5, encryptor_ver_5),
        )
        private fun chooseMasterPasswordOrDefaultPass(encryptorVersion:Int, masterPassword: String):String {
            return if(masterPassword.isEmpty() || (encryptorVersion >= 1 && encryptorVersion <= 4)) {
                keyMap[encryptorVersion]!!
            }else {  
                masterPassword
            }
        }
        fun encryptWithSpecifyEncryptorVersion(encryptorVersion:Int, raw:String, masterPassword:String):String {
            return encryptorMap[encryptorVersion]!!.encrypt(raw, chooseMasterPasswordOrDefaultPass(encryptorVersion, masterPassword))
        }
        fun decryptWithSpecifyEncryptorVersion(encryptorVersion:Int,  encryptedStr:String, masterPassword: String):String {
            return encryptorMap[encryptorVersion]!!.decrypt(encryptedStr, chooseMasterPasswordOrDefaultPass(encryptorVersion, masterPassword))
        }
        fun encryptWithCurrentEncryptor(raw:String, masterPassword:String):String {
            return encryptWithSpecifyEncryptorVersion(passEncryptCurrentVer, raw, masterPassword)
        }
        fun decryptWithCurrentEncryptor(encryptedStr:String, masterPassword: String):String {
            return decryptWithSpecifyEncryptorVersion(passEncryptCurrentVer, encryptedStr, masterPassword)
        }
    }
}
