package com.akcreation.gitsilent.settings.util

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.dto.AppInfo
import com.akcreation.gitsilent.service.AutomationService
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.settings.AutomationSettings
import com.akcreation.gitsilent.settings.PackageNameAndRepo
import com.akcreation.gitsilent.settings.PackageNameAndRepoSettings
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.getInstalledAppList

object AutomationUtil {
    private const val TAG = "AutomationUtil"
    fun getPackageNames(automationSettings: AutomationSettings):Set<String> {
        return automationSettings.packageNameAndRepoIdsMap.keys
    }
    fun getRepoIds(automationSettings: AutomationSettings, packageName:String):List<String> {
        return automationSettings.packageNameAndRepoIdsMap.get(packageName) ?: listOf()
    }
    suspend fun getRepos(automationSettings: AutomationSettings, packageName:String):List<RepoEntity>? {
        val bindRepoIds = getRepoIds(automationSettings, packageName).toSet()
        if(bindRepoIds.isEmpty()) {
            return null
        }
        return try {
            val repoList = AppModel.dbContainer.repoRepository.getAll(updateRepoInfo = false).filter { bindRepoIds.contains(it.id) }
            val settings = SettingsUtil.getSettingsSnapshot()
            repoList.forEachBetter {
                Libgit2Helper.updateRepoInfo(it, settings = settings)
            }
            repoList
        } catch (e:Exception) {
            MyLog.e(TAG, "#getRepos() err: packageName=$packageName, err=${e.stackTraceToString()}")
            null
        }
    }
    fun isAccessibilityServiceEnabled(context: Context): Boolean? {
        return try {
            val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
            val componentName = ComponentName(context, AutomationService::class.java).flattenToString()
            enabledServices.contains(componentName)
        }catch (e:Exception) {
            MyLog.e(TAG, "#isAccessibilityServiceEnabled() err: ${e.stackTraceToString()}")
            null
        }
    }
    fun getSelectedAndUnSelectedAppList(context: Context, automationSettings: AutomationSettings):Pair<List<AppInfo>, List<AppInfo>> {
        val installedAppList = getInstalledAppList(context)
        val userAddedAppList = getPackageNames(automationSettings)
        val selectedList = mutableListOf<AppInfo>()
        val unselectedList = mutableListOf<AppInfo>()
        val existedApps = mutableListOf<String>()
        installedAppList.forEachBetter { installed ->
            if(userAddedAppList.contains(installed.packageName)) {
                installed.isSelected = true
                selectedList.add(installed)
                existedApps.add(installed.packageName)
            }else {
                installed.isSelected = false
                unselectedList.add(installed)
            }
        }
        SettingsUtil.update { s ->
            val newMap = mutableMapOf<String, List<String>>()
            val oldMap = s.automation.packageNameAndRepoIdsMap
            val newAppAndRepoSettingsMap = mutableMapOf<String, PackageNameAndRepoSettings>()
            val oldAppAndRepoSettingsMap = s.automation.packageNameAndRepoAndSettingsMap
            existedApps.forEachBetter { packageName ->
                newMap.put(packageName, oldMap.get(packageName) ?: listOf())
                val keyPrefix = PackageNameAndRepo(packageName).toKeyPrefix()
                for (i in oldAppAndRepoSettingsMap) {
                    if(i.key.startsWith(keyPrefix)) {
                        newAppAndRepoSettingsMap.put(i.key, i.value)
                    }
                }
            }
            s.automation.packageNameAndRepoIdsMap = newMap
            s.automation.packageNameAndRepoAndSettingsMap = newAppAndRepoSettingsMap
        }
        return Pair(selectedList, unselectedList)
    }
    fun getAppAndRepoSpecifiedSettings(
        appPackageName:String,
        repoId:String,
        settings: AppSettings = SettingsUtil.getSettingsSnapshot(),
    ) = settings.automation.packageNameAndRepoAndSettingsMap.get(PackageNameAndRepo(appPackageName, repoId).toKey()) ?: PackageNameAndRepoSettings();
    fun getAppAndRepoSpecifiedSettingsActuallyBeUsed(
        appPackageName:String,
        repoId:String,
        settings: AppSettings = SettingsUtil.getSettingsSnapshot(),
    ) = getAppAndRepoSpecifiedSettings(appPackageName, repoId, settings).let { appAndRepoSetting ->
        PackageNameAndRepoSettings(
            appAndRepoSetting.getPullIntervalActuallyValue(settings).toString(),
            appAndRepoSetting.getPushDelayActuallyValue(settings).toString(),
        )
    }
    fun groupReposByPushDelayTime(
        appPackageName:String,
        repos:List<RepoEntity>,
        settings: AppSettings
    ):Map<Long, List<RepoEntity>> {
        val pushDelayGroupedMap = mutableMapOf<Long, MutableList<RepoEntity>>()
        repos.forEachBetter {
            val pushDelayInSec = getAppAndRepoSpecifiedSettings(appPackageName, it.id, settings).getPushDelayActuallyValue(settings)
            pushDelayGroupedMap.getOrPut(pushDelayInSec) { mutableListOf() }.apply { add(it) }
        }
        return pushDelayGroupedMap
    }
}
