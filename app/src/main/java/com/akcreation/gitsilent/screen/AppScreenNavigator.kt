package com.akcreation.gitsilent.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.constants.LineNum
import com.akcreation.gitsilent.screen.shared.CommitListFrom
import com.akcreation.gitsilent.screen.shared.DiffFromScreen
import com.akcreation.gitsilent.screen.shared.FileChooserType
import com.akcreation.gitsilent.screen.shared.IntentHandler
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.cache.NaviCache

@Composable
fun AppScreenNavigator() {
    AppModel.init_3()
    val navController =AppModel.navController
    val currentHomeScreen = rememberSaveable{ mutableIntStateOf(SettingsUtil.obtainLastQuitHomeScreen()) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val editorPageLastFilePath = rememberSaveable { mutableStateOf("")}
    val repoPageListState = rememberLazyListState()
    val navStartScreen = Cons.nav_HomeScreen;
    val gotNewIntent = rememberSaveable { IntentHandler.gotNewIntent }
    NavHost(navController = navController, startDestination = navStartScreen) {
        composable(Cons.nav_HomeScreen) {
            HomeScreen(drawerState, currentHomeScreen, repoPageListState, editorPageLastFilePath)
        }
        composable(Cons.nav_CredentialManagerScreen+"/{remoteId}") {
            val remoteId = it.arguments?.getString("remoteId") ?: ""
            CredentialManagerScreen(
                remoteId= if(remoteId==Cons.dbInvalidNonEmptyId) "" else remoteId,
                naviUp = { navController.navigateUp() }
            )
        }
        composable(Cons.nav_DomainCredentialListScreen) {
            DomainCredentialListScreen(
                naviUp = { navController.navigateUp() }
            )
        }
        composable(Cons.nav_CommitListScreen + "/{repoId}/{isHEAD}/{from}/{fullOidCacheKey}/{shortBranchNameCacheKey}") {
            val fullOidCacheKey = it.arguments?.getString("fullOidCacheKey") ?: ""
            val shortBranchNameCacheKey = it.arguments?.getString("shortBranchNameCacheKey") ?: ""
            CommitListScreen(
                repoId = it.arguments?.getString("repoId")?:"",
                isHEAD = it.arguments?.getString("isHEAD") != "0",
                fullOidCacheKey = fullOidCacheKey,
                shortBranchNameCacheKey = shortBranchNameCacheKey,
                from = CommitListFrom.fromCode(it.arguments?.getString("from")!!)!!,
                naviUp = {
                    navController.navigateUp()
                    NaviCache.del(fullOidCacheKey)
                    NaviCache.del(shortBranchNameCacheKey)
                },
            )
        }
        composable(Cons.nav_ErrorListScreen + "/{repoId}") {
            ErrorListScreen(
                it.arguments?.getString("repoId")?:"",
                naviUp = { navController.navigateUp() },
            )
        }
        composable(Cons.nav_CloneScreen + "/{repoId}") {
            CloneScreen(
                it.arguments?.getString("repoId") ?: "",
                naviUp = { navController.navigateUp() },
            )
        }
        composable(Cons.nav_DiffScreen + "/{repoId}/{fromTo}/{treeOid1Str}/{treeOid2Str}/{isDiffToLocal}/{curItemIndexAtDiffableList}/{localAtDiffRight}/{fromScreen}/{diffableListCacheKey}/{isMultiMode}") {
            val diffableListCacheKey = it.arguments?.getString("diffableListCacheKey") ?: ""
            DiffScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                fromTo = it.arguments?.getString("fromTo") ?: "",
                treeOid1Str = it.arguments?.getString("treeOid1Str") ?: "",
                treeOid2Str = it.arguments?.getString("treeOid2Str") ?: "",
                localAtDiffRight = (it.arguments?.getString("localAtDiffRight")?.toInt() ?: 0) != 0,
                isDiffToLocal = (it.arguments?.getString("isDiffToLocal")?.toInt() ?: 0) != 0,
                fromScreen = DiffFromScreen.fromCode(it.arguments?.getString("fromScreen")!!)!!,
                diffableListCacheKey = diffableListCacheKey,
                isMultiMode = it.arguments?.getString("isMultiMode") == "1",
                curItemIndexAtDiffableItemList = try {
                    (it.arguments?.getString("curItemIndexAtDiffableList") ?: "").toInt()
                }catch (_:Exception) {
                    -1
                },
                naviUp = {
                    navController.navigateUp()
                    NaviCache.del(diffableListCacheKey)
                },
            )
        }
        composable(Cons.nav_IndexScreen) {
            IndexScreen(
                naviUp = { navController.navigateUp() },
            )
        }
        composable(Cons.nav_CredentialNewOrEditScreen+"/{credentialId}") {
            CredentialNewOrEdit(
                credentialId = it.arguments?.getString("credentialId") ?: "",
                naviUp = {
                         navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_CredentialRemoteListScreen+"/{credentialId}/{isShowLink}") {
            CredentialRemoteListScreen(
                credentialId = it.arguments?.getString("credentialId") ?: "",
                isShowLink = if(it.arguments?.getString("isShowLink")=="0") false else true,
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_BranchListScreen+"/{repoId}") {
            BranchListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_TreeToTreeChangeListScreen+"/{repoId}/{commit1OidStrCacheKey}/{commit2OidStrCacheKey}/{commitForQueryParentsCacheKey}/{titleCacheKey}") {
            val commit1OidStrCacheKey = it.arguments?.getString("commit1OidStrCacheKey") ?: ""
            val commit2OidStrCacheKey = it.arguments?.getString("commit2OidStrCacheKey") ?: ""
            val commitForQueryParentsCacheKey = it.arguments?.getString("commitForQueryParentsCacheKey") ?: ""
            val titleCacheKey = it.arguments?.getString("titleCacheKey") ?: ""
            TreeToTreeChangeListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                commit1OidStrCacheKey = commit1OidStrCacheKey,
                commit2OidStrCacheKey = commit2OidStrCacheKey,
                commitForQueryParentsCacheKey = commitForQueryParentsCacheKey,
                titleCacheKey = titleCacheKey,
                naviUp = {
                    navController.navigateUp()
                    NaviCache.del(commit1OidStrCacheKey)
                    NaviCache.del(commit2OidStrCacheKey)
                    NaviCache.del(commitForQueryParentsCacheKey)
                    NaviCache.del(titleCacheKey)
                },
            )
        }
        composable(Cons.nav_RemoteListScreen+"/{repoId}") {
            RemoteListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_SubPageEditor+"/{goToLine}/{initMergeMode}/{initReadOnly}/{filePathKey}") {
            val filePathKey = it.arguments?.getString("filePathKey") ?: ""
            SubPageEditor(
                goToLine = try {
                    val l = it.arguments?.getString("goToLine") ?: ""
                    l.toInt()
                }catch (e:Exception) {
                    LineNum.lastPosition
                },
                initMergeMode = it.arguments?.getString("initMergeMode") == "1",  
                initReadOnly = it.arguments?.getString("initReadOnly") == "1",  
                editorPageLastFilePath = editorPageLastFilePath,
                filePathKey = filePathKey,
                naviUp = {
                    navController.navigateUp()
                    NaviCache.del(filePathKey)
                },
            )
        }
        composable(Cons.nav_TagListScreen+"/{repoId}") {
            TagListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_ReflogListScreen+"/{repoId}") {
            ReflogListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_StashListScreen+"/{repoId}") {
            StashListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_SubmoduleListScreen+"/{repoId}") {
            SubmoduleListScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(Cons.nav_FileHistoryScreen+"/{repoId}/{fileRelativePathKey}") {
            val fileRelativePathKey = it.arguments?.getString("fileRelativePathKey") ?: ""
            FileHistoryScreen(
                repoId = it.arguments?.getString("repoId") ?: "",
                fileRelativePathKey = fileRelativePathKey,
                naviUp = {
                    navController.navigateUp()
                    NaviCache.del(fileRelativePathKey)
                },
            )
        }
        composable(Cons.nav_FileChooserScreen+"/{type}") {
            FileChooserScreen (
                type = FileChooserType.fromCode(it.arguments!!.getString("type")!!)!!,
                naviUp = {
                    navController.navigateUp()
                },
            )
        }
    }
    LaunchedEffect(gotNewIntent.value) {
        IntentHandler.requireHandleNewIntent()
    }
}
