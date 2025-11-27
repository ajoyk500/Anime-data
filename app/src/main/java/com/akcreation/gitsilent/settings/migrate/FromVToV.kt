package com.akcreation.gitsilent.settings.migrate

import com.akcreation.gitsilent.settings.version.SettingsVersion

abstract class FromVToV(var from: String, var to: String) {
    abstract fun doMigration(settings:MutableMap<String,String>);
    fun migration(s: MutableMap<String,String>) {
        val key_version = SettingsVersion.commonKey_version
        if(""+s[key_version] != from) {
            return
        }
        doMigration(s)
        s[key_version] = to
    }
    protected fun getValueOrEmptyStr(s:MutableMap<String,String>, key:String):String {
        return s.get(key)?:""
    }
    protected fun updateField(s:MutableMap<String,String>, key:String, newValue:String):String {
        return s.put(key,newValue)?:""
    }
    protected fun removedField(s:MutableMap<String,String>, key:String):String {
        return s.remove(key)?:""
    }
    protected fun renameField(s:MutableMap<String,String>, key:String, newKey:String):String {
        val tmpV = getValueOrEmptyStr(s, key)
        updateField(s, newKey, tmpV)
        return removedField(s, key)
    }
}
