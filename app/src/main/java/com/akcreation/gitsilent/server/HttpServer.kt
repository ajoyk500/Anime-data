package com.akcreation.gitsilent.server

import com.akcreation.gitsilent.constants.Cons
import com.akcreation.gitsilent.data.entity.RepoEntity
import com.akcreation.gitsilent.etc.Ret
import com.akcreation.gitsilent.notification.HttpServiceExecuteNotify
import com.akcreation.gitsilent.notification.base.ServiceNotify
import com.akcreation.gitsilent.notification.util.NotifyUtil
import com.akcreation.gitsilent.server.bean.NotificationSender
import com.akcreation.gitsilent.settings.AppSettings
import com.akcreation.gitsilent.settings.SettingsUtil
import com.akcreation.gitsilent.utils.AppModel
import com.akcreation.gitsilent.utils.MyLog
import com.akcreation.gitsilent.utils.NetUtil
import com.akcreation.gitsilent.utils.RepoActUtil
import com.akcreation.gitsilent.utils.cache.NotifySenderMap
import com.akcreation.gitsilent.utils.createAndInsertError
import com.akcreation.gitsilent.utils.doJobThenOffLoading
import com.akcreation.gitsilent.utils.forEachBetter
import com.akcreation.gitsilent.utils.genHttpHostPortStr
import com.akcreation.gitsilent.utils.generateRandomString
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.host
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

private const val TAG = "HttpServer"
private const val matchAllIpSign = "*"
private fun isGoodTokenAndIp(token:String?, requestIp:String, settings: AppSettings): Ret<Unit?> {
    val errMsg = "invalid token or ip blocked"
    val errRet:Ret<Unit?> = Ret.createError(null, errMsg)
    val tokenList = settings.httpService.tokenList
    if(token == null || tokenList.isEmpty() || token.isBlank() || tokenList.contains(token).not()) {
        return errRet
    }
    val whiteList = settings.httpService.ipWhiteList
    if(whiteList.isEmpty() || requestIp.isBlank() || whiteList.find { it == matchAllIpSign || it == requestIp } == null) {
        return errRet
    }
    return Ret.createSuccess(null)
}
internal class HttpServer(
    val host:String,
    val port:Int
) {
    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine. Configuration>? = null
    private fun createNotify(notifyId:Int) : ServiceNotify {
        return ServiceNotify(HttpServiceExecuteNotify.create(notifyId))
    }
    private fun sendSuccessNotificationIfEnable(serviceNotify: ServiceNotify, settings: AppSettings) = { title:String?, msg:String?, startPage:Int?, startRepoId:String? ->
        if(settings.httpService.showNotifyWhenProgress) {
            serviceNotify.sendSuccessNotification(title, msg, startPage, startRepoId)
        }
    }
    private fun sendErrNotificationIfEnable(serviceNotify: ServiceNotify, settings:AppSettings)={ title:String, msg:String, startPage:Int, startRepoId:String ->
        if(settings.httpService.showNotifyWhenProgress) {
            serviceNotify.sendErrNotification(title, msg, startPage, startRepoId)
        }
    }
    private fun sendProgressNotificationIfEnable(serviceNotify: ServiceNotify, settings: AppSettings) = { repoNameOrId:String, progress:String ->
        if(settings.httpService.showNotifyWhenProgress) {
            serviceNotify.sendProgressNotification(repoNameOrId, progress)
        }
    }
    suspend fun startServer():Exception? {
        if(isServerRunning()) return null
        try {
            server = embeddedServer(Netty, host = host, port = port) {
                install(ContentNegotiation) {
                    json(Json{ ignoreUnknownKeys = true; encodeDefaults=true; prettyPrint = false})
                }
                routing {
                    get("/status") {
                        call.respond(createSuccessResult("online"))
                    }
                    get("/pull") {
                        val sessionId = generateRandomString()
                        val repoNameOrIdForLog = mutableListOf<String>()
                        var repoForLog:RepoEntity? = null
                        val routeName = "'/pull'"
                        val settings = SettingsUtil.getSettingsSnapshot()
                        val serviceNotify = createNotify(NotifyUtil.genId())
                        val sendErrNotification = sendErrNotificationIfEnable(serviceNotify, settings)
                        try {
                            tokenAndIpPassedOrThrowException(call, routeName, settings)
                            val forceUseIdMatchRepo = call.request.queryParameters.get("forceUseIdMatchRepo") == "1"
                            val gitUsernameFromUrl = call.request.queryParameters.get("gitUsername") ?:""
                            val gitEmailFromUrl = call.request.queryParameters.get("gitEmail") ?:""
                            val pullWithRebase = call.request.queryParameters.get("pullWithRebase")?.let { it == "1" } ?: SettingsUtil.pullWithRebase()
                            val validRepoListFromDb = getRepoListFromDb(
                                call.request.queryParameters.getAll("repoNameOrId"),
                                repoNameOrIdForLog,
                                forceUseIdMatchRepo
                            )
                            if(validRepoListFromDb.size == 1) {
                                repoForLog = validRepoListFromDb.first()
                            }
                            doJobThenOffLoading {
                                MyLog.d(TAG, "generate notifyers for ${validRepoListFromDb.size} repos")
                                validRepoListFromDb.forEachBetter {
                                    val serviceNotify = createNotify(NotifyUtil.genId())
                                    NotifySenderMap.set(
                                        NotifySenderMap.genKey(it.id, sessionId),
                                        NotificationSender(
                                            sendErrNotificationIfEnable(serviceNotify, settings),
                                            sendSuccessNotificationIfEnable(serviceNotify, settings),
                                            sendProgressNotificationIfEnable(serviceNotify, settings),
                                        )
                                    )
                                }
                                MyLog.d(TAG, "will do pull for ${validRepoListFromDb.size} repos: $validRepoListFromDb")
                                pullRepoList(
                                    sessionId = sessionId,
                                    repoList = validRepoListFromDb,
                                    routeName = routeName,
                                    gitUsernameFromUrl = gitUsernameFromUrl,
                                    gitEmailFromUrl = gitEmailFromUrl,
                                    pullWithRebase = pullWithRebase
                                )
                            }
                            call.respond(createSuccessResult())
                        }catch (e:Exception) {
                            val errMsg = e.localizedMessage ?: "unknown err"
                            call.respond(createErrResult(errMsg))
                            if(repoForLog!=null) {
                                createAndInsertError(repoForLog.id, "pull by api $routeName err: $errMsg")
                            }
                            sendErrNotification("$routeName err", errMsg, Cons.selectedItem_ChangeList, repoForLog?.id ?: "")
                            MyLog.e(TAG, "method:GET, route:$routeName, sessionId=$sessionId, repoNameOrId.size=${repoNameOrIdForLog.size}, repoNameOrId=$repoNameOrIdForLog, err=${e.stackTraceToString()}")
                        }
                    }
                    get("/push") {
                        val sessionId = generateRandomString()
                        val repoNameOrIdForLog = mutableListOf<String>()
                        var repoForLog:RepoEntity? = null
                        val routeName = "'/push'"
                        val settings = SettingsUtil.getSettingsSnapshot()
                        val serviceNotify = createNotify(NotifyUtil.genId())
                        val sendErrNotification = sendErrNotificationIfEnable(serviceNotify, settings)
                        try {
                            tokenAndIpPassedOrThrowException(call, routeName, settings)
                            val forceUseIdMatchRepo = call.request.queryParameters.get("forceUseIdMatchRepo") == "1"
                            val autoCommit = call.request.queryParameters.get("autoCommit") != "0"
                            val force = call.request.queryParameters.get("force") == "1"
                            val gitUsernameFromUrl = call.request.queryParameters.get("gitUsername") ?:""
                            val gitEmailFromUrl = call.request.queryParameters.get("gitEmail") ?:""
                            val validRepoListFromDb = getRepoListFromDb(
                                call.request.queryParameters.getAll("repoNameOrId"),
                                repoNameOrIdForLog,
                                forceUseIdMatchRepo
                            )
                            if(validRepoListFromDb.size == 1) {
                                repoForLog = validRepoListFromDb.first()
                            }
                            doJobThenOffLoading {
                                MyLog.d(TAG, "generate notifyers for ${validRepoListFromDb.size} repos")
                                validRepoListFromDb.forEachBetter {
                                    val serviceNotify = createNotify(NotifyUtil.genId())
                                    NotifySenderMap.set(
                                        NotifySenderMap.genKey(it.id, sessionId),
                                        NotificationSender(
                                            sendErrNotificationIfEnable(serviceNotify, settings),
                                            sendSuccessNotificationIfEnable(serviceNotify, settings),
                                            sendProgressNotificationIfEnable(serviceNotify, settings),
                                        )
                                    )
                                }
                                MyLog.d(TAG, "will do push for ${validRepoListFromDb.size} repos: $validRepoListFromDb")
                                pushRepoList(
                                    sessionId = sessionId,
                                    repoList = validRepoListFromDb,
                                    routeName = routeName,
                                    gitUsernameFromUrl = gitUsernameFromUrl,
                                    gitEmailFromUrl = gitEmailFromUrl,
                                    autoCommit = autoCommit,
                                    force = force,
                                )
                            }
                            call.respond(createSuccessResult())
                        }catch (e:Exception) {
                            val errMsg = e.localizedMessage ?: "unknown err"
                            call.respond(createErrResult(errMsg))
                            if(repoForLog!=null) {
                                createAndInsertError(repoForLog!!.id, "push by api $routeName err: $errMsg")
                            }
                            sendErrNotification("$routeName err", errMsg, Cons.selectedItem_ChangeList, repoForLog?.id ?: "")
                            MyLog.e(TAG, "method:GET, route:$routeName, sessionId=$sessionId, repoNameOrId.size=${repoNameOrIdForLog.size}, repoNameOrId=$repoNameOrIdForLog, err=${e.stackTraceToString()}")
                        }
                    }
                    get("/sync") {
                        val sessionId = generateRandomString()
                        val repoNameOrIdForLog = mutableListOf<String>()
                        var repoForLog:RepoEntity? = null
                        val routeName = "'/sync'"
                        val settings = SettingsUtil.getSettingsSnapshot()
                        val serviceNotify = createNotify(NotifyUtil.genId())
                        val sendErrNotification = sendErrNotificationIfEnable(serviceNotify, settings)
                        try {
                            tokenAndIpPassedOrThrowException(call, routeName, settings)
                            val forceUseIdMatchRepo = call.request.queryParameters.get("forceUseIdMatchRepo") == "1"
                            val autoCommit = call.request.queryParameters.get("autoCommit") != "0"
                            val force = call.request.queryParameters.get("force") == "1"
                            val gitUsernameFromUrl = call.request.queryParameters.get("gitUsername") ?:""
                            val gitEmailFromUrl = call.request.queryParameters.get("gitEmail") ?:""
                            val pullWithRebase = call.request.queryParameters.get("pullWithRebase")?.let { it == "1" } ?: SettingsUtil.pullWithRebase()
                            val validRepoListFromDb = getRepoListFromDb(
                                call.request.queryParameters.getAll("repoNameOrId"),
                                repoNameOrIdForLog,
                                forceUseIdMatchRepo
                            )
                            if(validRepoListFromDb.size == 1) {
                                repoForLog = validRepoListFromDb.first()
                            }
                            doJobThenOffLoading {
                                MyLog.d(TAG, "generate notifyers for ${validRepoListFromDb.size} repos")
                                validRepoListFromDb.forEachBetter {
                                    val serviceNotify = createNotify(NotifyUtil.genId())
                                    NotifySenderMap.set(
                                        NotifySenderMap.genKey(it.id, sessionId),
                                        NotificationSender(
                                            sendErrNotificationIfEnable(serviceNotify, settings),
                                            sendSuccessNotificationIfEnable(serviceNotify, settings),
                                            sendProgressNotificationIfEnable(serviceNotify, settings),
                                        )
                                    )
                                }
                                MyLog.d(TAG, "will do sync for ${validRepoListFromDb.size} repos: $validRepoListFromDb")
                                syncRepoList(
                                    sessionId = sessionId,
                                    repoList = validRepoListFromDb,
                                    routeName = routeName,
                                    gitUsernameFromUrl = gitUsernameFromUrl,
                                    gitEmailFromUrl = gitEmailFromUrl,
                                    autoCommit = autoCommit,
                                    force = force,
                                    pullWithRebase = pullWithRebase,
                                )
                            }
                            call.respond(createSuccessResult())
                        }catch (e:Exception) {
                            val errMsg = e.localizedMessage ?: "unknown err"
                            call.respond(createErrResult(errMsg))
                            if(repoForLog!=null) {
                                createAndInsertError(repoForLog!!.id, "sync by api $routeName err: $errMsg")
                            }
                            sendErrNotification("$routeName err", errMsg, Cons.selectedItem_ChangeList, repoForLog?.id ?: "")
                            MyLog.e(TAG, "method:GET, route:$routeName, sessionId=$sessionId, repoNameOrId.size=${repoNameOrIdForLog.size}, repoNameOrId=$repoNameOrIdForLog, err=${e.stackTraceToString()}")
                        }
                    }
                    get("/pullAll") {
                        val sessionId = generateRandomString()
                        val routeName = "'/pullAll'"
                        val settings = SettingsUtil.getSettingsSnapshot()
                        val serviceNotify = createNotify(NotifyUtil.genId())
                        val sendErrNotification = sendErrNotificationIfEnable(serviceNotify, settings)
                        try {
                            tokenAndIpPassedOrThrowException(call, routeName, settings)
                            val gitUsernameFromUrl = call.request.queryParameters.get("gitUsername") ?:""
                            val gitEmailFromUrl = call.request.queryParameters.get("gitEmail") ?:""
                            val pullWithRebase = call.request.queryParameters.get("pullWithRebase")?.let { it == "1" } ?: SettingsUtil.pullWithRebase()
                            doJobThenOffLoading {
                                val allRepos = AppModel.dbContainer.repoRepository.getAll()
                                MyLog.d(TAG, "generate notifyers for ${allRepos.size} repos")
                                allRepos.forEachBetter {
                                    val serviceNotify = createNotify(NotifyUtil.genId())
                                    NotifySenderMap.set(
                                        NotifySenderMap.genKey(it.id, sessionId),
                                        NotificationSender(
                                            sendErrNotificationIfEnable(serviceNotify, settings),
                                            sendSuccessNotificationIfEnable(serviceNotify, settings),
                                            sendProgressNotificationIfEnable(serviceNotify, settings),
                                        )
                                    )
                                }
                                pullRepoList(
                                    sessionId = sessionId,
                                    repoList = allRepos,
                                    routeName = routeName,
                                    gitUsernameFromUrl = gitUsernameFromUrl,
                                    gitEmailFromUrl = gitEmailFromUrl,
                                    pullWithRebase = pullWithRebase,
                                )
                            }
                            call.respond(createSuccessResult())
                        }catch (e:Exception) {
                            val errMsg = e.localizedMessage ?: "unknown err"
                            call.respond(createErrResult(errMsg))
                            sendErrNotification("$routeName err", errMsg, Cons.selectedItem_Repos ,"")
                            MyLog.e(TAG, "method:GET, route:$routeName, sessionId=$sessionId, err=${e.stackTraceToString()}")
                        }
                    }
                    get("/pushAll") {
                        val sessionId = generateRandomString()
                        val routeName = "'/pushAll'"
                        val settings = SettingsUtil.getSettingsSnapshot()
                        val serviceNotify = createNotify(NotifyUtil.genId())
                        val sendErrNotification = sendErrNotificationIfEnable(serviceNotify, settings)
                        try {
                            tokenAndIpPassedOrThrowException(call, routeName, settings)
                            val autoCommit = call.request.queryParameters.get("autoCommit") != "0"
                            val force = call.request.queryParameters.get("force") == "1"
                            val gitUsernameFromUrl = call.request.queryParameters.get("gitUsername") ?:""
                            val gitEmailFromUrl = call.request.queryParameters.get("gitEmail") ?:""
                            doJobThenOffLoading {
                                val allRepos = AppModel.dbContainer.repoRepository.getAll()
                                MyLog.d(TAG, "generate notifyers for ${allRepos.size} repos")
                                allRepos.forEachBetter {
                                    val serviceNotify = createNotify(NotifyUtil.genId())
                                    NotifySenderMap.set(
                                        NotifySenderMap.genKey(it.id, sessionId),
                                        NotificationSender(
                                            sendErrNotificationIfEnable(serviceNotify, settings),
                                            sendSuccessNotificationIfEnable(serviceNotify, settings),
                                            sendProgressNotificationIfEnable(serviceNotify, settings),
                                        )
                                    )
                                }
                                pushRepoList(
                                    sessionId = sessionId,
                                    repoList = allRepos,
                                    routeName = routeName,
                                    gitUsernameFromUrl = gitUsernameFromUrl,
                                    gitEmailFromUrl = gitEmailFromUrl,
                                    autoCommit = autoCommit,
                                    force = force,
                                )
                            }
                            call.respond(createSuccessResult())
                        }catch (e:Exception) {
                            val errMsg = e.localizedMessage ?: "unknown err"
                            call.respond(createErrResult(errMsg))
                            sendErrNotification("$routeName err", errMsg, Cons.selectedItem_Repos, "")
                            MyLog.e(TAG, "method:GET, route:$routeName, sessionId=$sessionId, err=${e.stackTraceToString()}")
                        }
                    }
                    get("/syncAll") {
                        val sessionId = generateRandomString()
                        val routeName = "'/syncAll'"
                        val settings = SettingsUtil.getSettingsSnapshot()
                        val serviceNotify = createNotify(NotifyUtil.genId())
                        val sendErrNotification = sendErrNotificationIfEnable(serviceNotify, settings)
                        try {
                            tokenAndIpPassedOrThrowException(call, routeName, settings)
                            val autoCommit = call.request.queryParameters.get("autoCommit") != "0"
                            val force = call.request.queryParameters.get("force") == "1"
                            val gitUsernameFromUrl = call.request.queryParameters.get("gitUsername") ?:""
                            val gitEmailFromUrl = call.request.queryParameters.get("gitEmail") ?:""
                            val pullWithRebase = call.request.queryParameters.get("pullWithRebase")?.let { it == "1" } ?: SettingsUtil.pullWithRebase()
                            doJobThenOffLoading {
                                val allRepos = AppModel.dbContainer.repoRepository.getAll()
                                MyLog.d(TAG, "generate notifyers for ${allRepos.size} repos")
                                allRepos.forEachBetter {
                                    val serviceNotify = createNotify(NotifyUtil.genId())
                                    NotifySenderMap.set(
                                        NotifySenderMap.genKey(it.id, sessionId),
                                        NotificationSender(
                                            sendErrNotificationIfEnable(serviceNotify, settings),
                                            sendSuccessNotificationIfEnable(serviceNotify, settings),
                                            sendProgressNotificationIfEnable(serviceNotify, settings),
                                        )
                                    )
                                }
                                syncRepoList(
                                    sessionId=sessionId,
                                    repoList = allRepos,
                                    routeName = routeName,
                                    gitUsernameFromUrl = gitUsernameFromUrl,
                                    gitEmailFromUrl = gitEmailFromUrl,
                                    autoCommit = autoCommit,
                                    force = force,
                                    pullWithRebase = pullWithRebase,
                                )
                            }
                            call.respond(createSuccessResult())
                        }catch (e:Exception) {
                            val errMsg = e.localizedMessage ?: "unknown err"
                            call.respond(createErrResult(errMsg))
                            sendErrNotification("$routeName err", errMsg, Cons.selectedItem_Repos, "")
                            MyLog.e(TAG, "method:GET, route:$routeName, sessionId=$sessionId, err=${e.stackTraceToString()}")
                        }
                    }
                }
            }.start(wait = false) 
            MyLog.w(TAG, "Http Server started on '${genHttpHostPortStr(host, port.toString())}'")
            return null
        }catch (e:Exception) {
            MyLog.e(TAG, "Http Server start failed, err=${e.stackTraceToString()}")
            return e
        }
    }
    private suspend fun getRepoListFromDb(
        repoNameOrIdList:List<String>?,
        repoNameOrIdForLog: MutableList<String>,
        forceUseIdMatchRepo: Boolean
    ): MutableList<RepoEntity> {
        if (repoNameOrIdList == null || repoNameOrIdList.isEmpty()) {
            throw RuntimeException("require repo name or id")
        }
        MyLog.d(TAG, "raw repoNameOrId list size is: ${repoNameOrIdList.size}, values are: $repoNameOrIdList")
        repoNameOrIdForLog.addAll(repoNameOrIdList)
        val db = AppModel.dbContainer
        val validRepoListFromDb = mutableListOf<RepoEntity>()
        repoNameOrIdList.forEachBetter { repoNameOrId ->
            val repoRet = db.repoRepository.getByNameOrId(repoNameOrId, forceUseIdMatchRepo)
            if (repoRet.hasError() || repoRet.data == null) {
                MyLog.d(TAG, "query repo '$repoNameOrId' from db err: " + repoRet.msg)
            }else {
                validRepoListFromDb.add(repoRet.data!!)
            }
        }
        if(validRepoListFromDb.isEmpty()) {
            throw RuntimeException("no valid Repo matched")
        }
        return validRepoListFromDb
    }
    private fun tokenAndIpPassedOrThrowException(call: RoutingCall, routeName: String, settings: AppSettings) {
        val token = call.request.queryParameters.get("token")
        val requestIp = call.request.host()
        val tokenCheckRet = isGoodTokenAndIp(token, requestIp, settings)
        if (tokenCheckRet.hasError()) {
            MyLog.e(TAG, "request rejected: routeName=$routeName, requestIp=$requestIp, token=$token, reason=${tokenCheckRet.msg}")
            throw RuntimeException(tokenCheckRet.msg)
        }
    }
    suspend fun stopServer():Exception? {
        if(server == null) {
            MyLog.w(TAG, "server is null, stop canceled")
            return null
        }
        try {
            server?.stop(0, 0)  
            server = null
            MyLog.w(TAG, "Http Server stopped")
            return null
        }catch (e:Exception) {
            MyLog.e(TAG, "Http Server stop failed: ${e.stackTraceToString()}")
            return e
        }
    }
    suspend fun restartServer():Exception? {
        stopServer()
        return startServer()
    }
    fun isServerRunning():Boolean {
        return server?.application?.isActive == true
    }
    private suspend fun pullRepoList(
        sessionId: String,
        repoList:List<RepoEntity>,
        routeName: String,
        gitUsernameFromUrl:String,
        gitEmailFromUrl:String,
        pullWithRebase: Boolean, 
    ) {
        RepoActUtil.pullRepoList(
            sessionId = sessionId,
            repoList = repoList,
            routeName = routeName,
            gitUsernameFromUrl = gitUsernameFromUrl,
            gitEmailFromUrl = gitEmailFromUrl,
            pullWithRebase = pullWithRebase,
        )
    }
    private suspend fun pushRepoList(
        sessionId: String,
        repoList:List<RepoEntity>,
        routeName: String,
        gitUsernameFromUrl:String,
        gitEmailFromUrl:String,
        autoCommit:Boolean,
        force:Boolean,
    ) {
        RepoActUtil.pushRepoList(
            sessionId = sessionId,
            repoList = repoList,
            routeName = routeName,
            gitUsernameFromUrl = gitUsernameFromUrl,
            gitEmailFromUrl = gitEmailFromUrl,
            autoCommit = autoCommit,
            force = force,
        )
    }
    private suspend fun syncRepoList(
        sessionId:String,
        repoList:List<RepoEntity>,
        routeName: String,
        gitUsernameFromUrl:String,
        gitEmailFromUrl:String,
        autoCommit:Boolean,
        force:Boolean,
        pullWithRebase: Boolean, 
    ) {
        RepoActUtil.syncRepoList(
            sessionId = sessionId,
            repoList = repoList,
            routeName = routeName,
            gitUsernameFromUrl = gitUsernameFromUrl,
            gitEmailFromUrl = gitEmailFromUrl,
            autoCommit = autoCommit,
            force = force,
            pullWithRebase = pullWithRebase,
        )
    }
}
fun isHttpServerOnline(host: String, port:String): Ret<Unit?> {
    val targetUrl = "${genHttpHostPortStr(host, port)}/status"
    val success = NetUtil.checkApiRunning(targetUrl)
    MyLog.d(TAG, "#isHttpServerOnline: test url '$targetUrl', success=$success")
    return success
}
