#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_REMOTE_H__
#define __GIT24J_REMOTE_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniInitCallbackConstant)(JNIEnv *env, jclass obj, jobject callback);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniAddFetch)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring refspec);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniAddPush)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring refspec);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniAutotag)(JNIEnv *env, jclass obj, jlong remotePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniConnect)(JNIEnv *env, jclass obj, jlong remotePtr, jint direction, jlong callbacksPtr, jlong proxyOptsPtr, jobjectArray customHeaders);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniConnected)(JNIEnv *env, jclass obj, jlong remotePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreate)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name, jstring url);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateAnonymous)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring url);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateDetached)(JNIEnv *env, jclass obj, jobject out, jstring url);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateWithFetchspec)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name, jstring url, jstring fetch);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateWithOpts)(JNIEnv *env, jclass obj, jobject out, jstring url, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDefaultBranch)(JNIEnv *env, jclass obj, jobject out, jlong remotePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDelete)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniDisconnect)(JNIEnv *env, jclass obj, jlong remotePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDownload)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDup)(JNIEnv *env, jclass obj, jobject dest, jlong sourcePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetch)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr, jstring reflog_message);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFree)(JNIEnv *env, jclass obj, jlong remotePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniGetFetchRefspecs)(JNIEnv *env, jclass obj, jobject array, jlong remotePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniGetPushRefspecs)(JNIEnv *env, jclass obj, jobject array, jlong remotePtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniGetRefspec)(JNIEnv *env, jclass obj, jlong remotePtr, jint n);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniInitCallbacks)(JNIEnv *env, jclass obj, jlong remoteCallbacksPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCallbacksNew)(JNIEnv *env, jclass obj, jobject outCb, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCallbacksFree)(JNIEnv *env, jclass obj, jlong cbsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCallbacksTest)(JNIEnv *env, jclass obj, jlong cbsPtr, jobject cbsObject);
    typedef enum
    {
        J_REMOTE_CALLBACK_CRED = 0,
        J_REMOTE_CALLBACK_TRANSPORT_MSG,
        J_REMOTE_CALLBACK_COMPLETION,
        J_REMOTE_CALLBACK_CERTIFICATE_CHECK,
        J_REMOTE_CALLBACK_TRANSFER_PROGRESS,
        J_REMOTE_CALLBACK_UPDATE_TIP,
        J_REMOTE_CALLBACK_PACK_PROGRESS,
        J_REMOTE_CALLBACK_PUSH_TRANSFER_PROGRESS,
        J_REMOTE_CALLBACK_PUSH_UPDATE_REFERENCE,
        J_REMOTE_CALLBACK_PUSH_NEGOTIATION,
        J_REMOTE_CALLBACK_TRANSPORT,
        J_REMOTE_CALLBACK_URL_RESOLVE,
    } j_callback_type_t;
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCallbacksSetCallbackObject)(JNIEnv *env, jclass obj, jlong cbsPtr, jobject cbsObject, jint cbt);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniIsValidName)(JNIEnv *env, jclass obj, jstring remote_name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniList)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniLookup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniName)(JNIEnv *env, jclass obj, jlong remotePtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniOwner)(JNIEnv *env, jclass obj, jlong remotePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPrune)(JNIEnv *env, jclass obj, jlong remotePtr, jlong callbacksPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPruneRefs)(JNIEnv *env, jclass obj, jlong remotePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPush)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniPushurl)(JNIEnv *env, jclass obj, jlong remotePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniRefspecCount)(JNIEnv *env, jclass obj, jlong remotePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniRename)(JNIEnv *env, jclass obj, jobject problems, jlong repoPtr, jstring name, jstring new_name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniSetAutotag)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jint value);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniSetPushurl)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring url);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniSetUrl)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring url);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniStats)(JNIEnv *env, jclass obj, jlong remotePtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniStop)(JNIEnv *env, jclass obj, jlong remotePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniUpdateTips)(JNIEnv *env, jclass obj, jlong remotePtr, jlong callbacksPtr, jint update_fetchhead, jint downloadTags, jstring reflog_message);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniUpload)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniUrl)(JNIEnv *env, jclass obj, jlong remotePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetVersion)(JNIEnv *env, jclass obj, jlong createOptionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetRepository)(JNIEnv *env, jclass obj, jlong createOptionsPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetName)(JNIEnv *env, jclass obj, jlong createOptionsPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetFetchspec)(JNIEnv *env, jclass obj, jlong createOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetFlags)(JNIEnv *env, jclass obj, jlong createOptionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetVersion)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetRepository)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jlong repository);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetName)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jstring name);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetFetchspec)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jstring fetchspec);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetFlags)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jint flags);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetVersion)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetCallbacks)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetPrune)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetUpdateFetchhead)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetDownloadTags)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetProxyOpts)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetCustomHeaders)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jobject outHeadersList);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetVersion)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetDepth)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint depth);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetDepth)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetFollowRedirects)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint redirectT);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetFollowRedirects)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetPrune)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint prune);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetUpdateFetchhead)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint updateFetchhead);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetDownloadTags)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint downloadTags);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetProxyOpts)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jlong proxyOptsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetCustomHeaders)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jobjectArray customHeaders);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPushOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetVersion)(JNIEnv *env, jclass obj, jlong pushOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetPbParallelism)(JNIEnv *env, jclass obj, jlong pushOptionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetCallbacks)(JNIEnv *env, jclass obj, jlong pushOptionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetProxyOpts)(JNIEnv *env, jclass obj, jlong pushOptionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetCustomHeaders)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jobject outHeadersList);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsSetVersion)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsSetPbParallelism)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jint pbParallelism);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsSetCustomHeaders)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jobjectArray customHeaders);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsSetRemotePushOptions)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jobjectArray remotePushOptions);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetRemotePushOptions)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jobject outRemotePushOptions);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniPushUpdateNew)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateFree)(JNIEnv *env, jclass obj, jlong pushUpdatePtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniPushUpdateGetSrcRefname)(JNIEnv *env, jclass obj, jlong pushUpdatePtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniPushUpdateGetDstRefname)(JNIEnv *env, jclass obj, jlong pushUpdatePtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Remote_jniPushUpdateGetSrc)(JNIEnv *env, jclass obj, jlong pushUpdatePtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Remote_jniPushUpdateGetDst)(JNIEnv *env, jclass obj, jlong pushUpdatePtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateSetSrcRefname)(JNIEnv *env, jclass obj, jlong pushUpdatePtr, jstring srcRefname);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateSetDstRefname)(JNIEnv *env, jclass obj, jlong pushUpdatePtr, jstring dstRefname);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateSetSrc)(JNIEnv *env, jclass obj, jlong pushUpdatePtr, jobject src);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateSetDst)(JNIEnv *env, jclass obj, jlong pushUpdatePtr, jobject dst);
#ifdef __cplusplus
}
#endif
#endif
