#include "j_remote.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <string.h>
extern j_constants_t *jniConstants;
int j_git_transport_message_cb(const char *str, int len, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    JNIEnv *env = getEnv();
    jstring message = (*env)->NewStringUTF(env, str);
    int r = (*env)->CallIntMethod(env, (jobject)payload, jniConstants->remote.midTransportMessage, message);
    (*env)->DeleteLocalRef(env, message);
    return r;
}
int j_git_remote_completion_cb(git_remote_completion_t completion_type, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    JNIEnv *env = getEnv();
    int r = (*env)->CallIntMethod(env, (jobject)payload, jniConstants->remote.midCompletion, completion_type);
    return r;
}
int j_git_cred_acquire_cb(git_credential **cred, const char *url, const char *username_from_url, unsigned int allowed_types, void *payload)
{
    if (!payload) { 
        return 1;  
    }
    JNIEnv *env = getEnv();
    jstring jniUrl = (*env)->NewStringUTF(env, url);
    jstring usernameFromUrl = (*env)->NewStringUTF(env, username_from_url);
    jlong ptr = (jlong)((*env)->CallLongMethod(env, (jobject)payload,
        jniConstants->remote.midAcquireCred, (jstring)jniUrl, (jstring)usernameFromUrl, (jint)allowed_types));
    (*env)->DeleteLocalRef(env, jniUrl);
    (*env)->DeleteLocalRef(env, usernameFromUrl);
    if(!ptr) {  
        return -1;  
    }
    *cred = (git_credential *)ptr;  
    return 0;  
}
int j_git_transport_certificate_check_cb(git_cert *cert, int valid, const char *host, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    JNIEnv *env = getEnv();
    jstring jHost = (*env)->NewStringUTF(env, host);
    int r = (*env)->CallIntMethod(env, (jobject)payload, jniConstants->remote.midTransportCertificateCheck, (jlong)cert, valid, jHost);
    (*env)->DeleteLocalRef(env, jHost);
    return r;
}
int j_git_transfer_progress_cb(const git_transfer_progress *stats, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    JNIEnv *env = getEnv();
    int r = (*env)->CallIntMethod(env, (jobject)payload, jniConstants->remote.midTransferProgress, (jlong)stats);
    return r;
}
int j_git_remote_update_tips_cb(const char *refname, const git_oid *a, const git_oid *b, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    JNIEnv *env = getEnv();
    jstring jRefname = (*env)->NewStringUTF(env, refname);
    jbyteArray ja = j_git_oid_to_bytearray(env, a);
    jbyteArray jb = j_git_oid_to_bytearray(env, b);
    int r = (*env)->CallIntMethod(env, (jobject)payload, jniConstants->remote.midUpdateTips, jRefname, ja, jb);
    (*env)->DeleteLocalRef(env, ja);
    (*env)->DeleteLocalRef(env, jb);
    (*env)->DeleteLocalRef(env, jRefname);
    return r;
}
int j_git_packbuilder_progress_cb(int stage, uint32_t current, uint32_t total, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    JNIEnv *env = getEnv();
    int r = (*env)->CallIntMethod(env, (jobject)payload, jniConstants->remote.midPackProgress, stage, current, total);
    return r;
}
int j_git_push_transfer_progress_cb(unsigned int current, unsigned int total, size_t bytes, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    JNIEnv *env = getEnv();
    return (*env)->CallIntMethod(env, (jobject)payload, jniConstants->remote.midPushTransferProgress, (jlong)current, (jlong)total, (jint)bytes);
}
int j_git_push_update_reference_cb(const char *refname, const char *status, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    JNIEnv *env = getEnv();
    jstring jRefname = (*env)->NewStringUTF(env, refname);
    jstring jStatus = (*env)->NewStringUTF(env, status);
    int r = (*env)->CallIntMethod(env, (jobject)payload, jniConstants->remote.midPushUpdateReference, jRefname, jStatus);
    (*env)->DeleteLocalRef(env, jRefname);
    (*env)->DeleteLocalRef(env, jStatus);
    return r;
}
int j_git_push_negotiation_cb(const git_push_update **updates, size_t len, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    JNIEnv *env = getEnv();
    jlongArray jUpdates = j_long_array_from_pointers(env, (const void **)updates, len);
    int r = (*env)->CallIntMethod(env, (jobject)payload, jniConstants->remote.midPushNegotiation, jUpdates);
    (*env)->DeleteLocalRef(env, jUpdates);
    return r;
}
int j_git_transport_cb(git_transport **out, git_remote *owner, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    JNIEnv *env = getEnv();
    long res = (*env)->CallIntMethod(env, (jobject)payload, jniConstants->remote.midTransport, (jlong)owner);
    if (res > 0)
    {
        *out = (git_transport *)res;
        return 0;
    }
    return res;
}
int j_git_url_resolve_cb(git_buf *url_resolved, const char *url, int direction, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    JNIEnv *env = getEnv();
    jstring urlResolved = j_git_buf_to_jstring(env, url_resolved);
    jstring jUrl = (*env)->NewStringUTF(env, url);
    int r = (*env)->CallIntMethod(env, (jobject)payload, jniConstants->remote.midResolveUrl, urlResolved, jUrl, direction);
    (*env)->DeleteLocalRef(env, urlResolved);
    (*env)->DeleteLocalRef(env, jUrl);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniAddFetch)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring refspec)
{
    char *c_remote = j_copy_of_jstring(env, remote, true);
    char *c_refspec = j_copy_of_jstring(env, refspec, true);
    int r = git_remote_add_fetch((git_repository *)repoPtr, c_remote, c_refspec);
    free(c_remote);
    free(c_refspec);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniAddPush)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring refspec)
{
    char *c_remote = j_copy_of_jstring(env, remote, true);
    char *c_refspec = j_copy_of_jstring(env, refspec, true);
    int r = git_remote_add_push((git_repository *)repoPtr, c_remote, c_refspec);
    free(c_remote);
    free(c_refspec);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniAutotag)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    git_remote_autotag_option_t r = git_remote_autotag((git_remote *)remotePtr);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniConnect)(JNIEnv *env, jclass obj, jlong remotePtr, jint direction, jlong callbacksPtr, jlong proxyOptsPtr, jobjectArray customHeaders)
{
    git_strarray c_custom_headers;
    git_strarray_of_jobject_array(env, customHeaders, &c_custom_headers);
    int r = git_remote_connect((git_remote *)remotePtr, direction, (git_remote_callbacks *)callbacksPtr, (git_proxy_options *)proxyOptsPtr, &c_custom_headers);
    git_strarray_free(&c_custom_headers);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniConnected)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    int r = git_remote_connected((git_remote *)remotePtr);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreate)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name, jstring url)
{
    git_remote *c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_remote_create(&c_out, (git_repository *)repoPtr, c_name, c_url);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_name);
    free(c_url);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateAnonymous)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring url)
{
    git_remote *c_out = 0;
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_remote_create_anonymous(&c_out, (git_repository *)repoPtr, c_url);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_url);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateDetached)(JNIEnv *env, jclass obj, jobject out, jstring url)
{
    git_remote *c_out = 0;
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_remote_create_detached(&c_out, c_url);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_url);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_remote_create_init_options((git_remote_create_options *)optsPtr, version);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_remote_create_options *opts = (git_remote_create_options *)malloc(sizeof(git_remote_create_options));
    int r = git_remote_create_init_options(opts, version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    git_remote_create_options *opts = (git_remote_create_options *)optsPtr;
    free((char *)opts->name);
    free((char *)opts->fetchspec);
    free(opts);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateWithFetchspec)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name, jstring url, jstring fetch)
{
    git_remote *c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_url = j_copy_of_jstring(env, url, true);
    char *c_fetch = j_copy_of_jstring(env, fetch, true);
    int r = git_remote_create_with_fetchspec(&c_out, (git_repository *)repoPtr, c_name, c_url, c_fetch);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_name);
    free(c_url);
    free(c_fetch);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateWithOpts)(JNIEnv *env, jclass obj, jobject out, jstring url, jlong optsPtr)
{
    git_remote *c_out = 0;
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_remote_create_with_opts(&c_out, c_url, (git_remote_create_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_url);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDefaultBranch)(JNIEnv *env, jclass obj, jobject out, jlong remotePtr)
{
    git_buf c_out = {0};
    int r = git_remote_default_branch(&c_out, (git_remote *)remotePtr);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDelete)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_remote_delete((git_repository *)repoPtr, c_name);
    free(c_name);
    return r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniDisconnect)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    git_remote_disconnect((git_remote *)remotePtr);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDownload)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr)
{
    git_strarray c_refspecs;
    git_strarray_of_jobject_array(env, refspecs, &c_refspecs);
    int r = git_remote_download((git_remote *)remotePtr, &c_refspecs, (git_fetch_options *)optsPtr);
    git_strarray_free(&c_refspecs);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDup)(JNIEnv *env, jclass obj, jobject dest, jlong sourcePtr)
{
    git_remote *c_dest = 0;
    int r = git_remote_dup(&c_dest, (git_remote *)sourcePtr);
    (*env)->CallVoidMethod(env, dest, jniConstants->midAtomicLongSet, (jlong)c_dest);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetch)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr, jstring reflog_message)
{
    git_strarray c_refspecs;
    if (refspecs != NULL)
    {
        git_strarray_of_jobject_array(env, refspecs, &c_refspecs);
    }
    char *c_reflog_message = j_copy_of_jstring(env, reflog_message, true);
    const git_strarray *refspect_ptr = refspecs == NULL ? NULL : &c_refspecs;
    int r = git_remote_fetch((git_remote *)remotePtr, refspect_ptr, (git_fetch_options *)optsPtr, c_reflog_message);
    if (refspecs != NULL)
    {
        git_strarray_free(&c_refspecs);
    }
    free(c_reflog_message);
    return r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFree)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    git_remote_free((git_remote *)remotePtr);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniGetFetchRefspecs)(JNIEnv *env, jclass obj, jobject array, jlong remotePtr)
{
    git_strarray *c_array = (git_strarray *)malloc(sizeof(git_strarray));
    int r = git_remote_get_fetch_refspecs(c_array, (git_remote *)remotePtr);
    j_strarray_to_java_list(env, c_array, array);
    git_strarray_free(c_array);
    free(c_array);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniGetPushRefspecs)(JNIEnv *env, jclass obj, jobject array, jlong remotePtr)
{
    git_strarray *c_array = (git_strarray *)malloc(sizeof(git_strarray));
    int r = git_remote_get_push_refspecs(c_array, (git_remote *)remotePtr);
    j_strarray_to_java_list(env, c_array, array);
    git_strarray_free(c_array);
    free(c_array);
    return r;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniGetRefspec)(JNIEnv *env, jclass obj, jlong remotePtr, jint n)
{
    const git_refspec *r = git_remote_get_refspec((git_remote *)remotePtr, n);
    return (jlong)r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniInitCallbacks)(JNIEnv *env, jclass obj, jlong remoteCallbacksPtr, jint version)
{
    int r = git_remote_init_callbacks((git_remote_callbacks *)remoteCallbacksPtr, version);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCallbacksNew)(JNIEnv *env, jclass obj, jobject outCb, jint version)
{
    git_remote_callbacks *cb = (git_remote_callbacks *)malloc(sizeof(git_remote_callbacks));
    int r = git_remote_init_callbacks((git_remote_callbacks *)cb, version);
    (*env)->CallVoidMethod(env, outCb, jniConstants->midAtomicLongSet, (jlong)cb);
    return r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCallbacksFree)(JNIEnv *env, jclass obj, jlong cbsPtr)
{
    git_remote_callbacks *c_ptr = (git_remote_callbacks *)cbsPtr;
    if (c_ptr->payload != NULL)
    {
        (*env)->DeleteGlobalRef(env, (jobject)c_ptr->payload);
    }
    free(c_ptr);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCallbacksTest)(JNIEnv *env, jclass obj, jlong cbsPtr, jobject cbsObject)
{
    git_remote_callbacks *cb = (git_remote_callbacks *)cbsPtr;
    void *payload = (void *)cbsObject;
    cb->sideband_progress("sideband_progress.str", 1, payload);
    cb->completion(1, payload);
    cb->credentials(NULL, "credentials.url", "credentials.user_name_from_url", 1, payload);
    cb->certificate_check(NULL, 1, "certificate_check.host", payload);
    cb->transfer_progress(NULL, payload);
    cb->update_tips("update_tips.refname", NULL, NULL, payload);
    cb->pack_progress(1, 2, 3, payload);
    cb->push_transfer_progress(1, 2, 3, payload);
    cb->push_update_reference("push_update_reference.refname", "push_update_reference.status", payload);
    git_push_update pu1;
    git_push_update pu2;
    git_push_update **fake = (git_push_update **)malloc(sizeof(git_push_update *) * 2);
    fake[0] = &pu1;
    fake[1] = &pu2;
    cb->push_negotiation((const git_push_update **)fake, 2, payload);
    free(fake);
    cb->transport(NULL, NULL, payload);
    cb->resolve_url(NULL, "resolve_url.url", 1, payload);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCallbacksSetCallbackObject)(JNIEnv *env, jclass obj, jlong cbsPtr, jobject cbsObject, jint cbt)
{
    git_remote_callbacks *cb = (git_remote_callbacks *)cbsPtr;
    if (cb->payload == NULL)
    {
        cb->payload = (*env)->NewGlobalRef(env, cbsObject);
    }
    assert((cbt >= J_REMOTE_CALLBACK_CRED && cbt <= J_REMOTE_CALLBACK_URL_RESOLVE) && "unrecognized callbacks type");
    switch (cbt)
    {
    case J_REMOTE_CALLBACK_CRED:
        cb->credentials = j_git_cred_acquire_cb;
        break;
    case J_REMOTE_CALLBACK_TRANSPORT_MSG:
        cb->sideband_progress = j_git_transport_message_cb;
        break;
    case J_REMOTE_CALLBACK_COMPLETION:
        cb->completion = j_git_remote_completion_cb;
        break;
    case J_REMOTE_CALLBACK_CERTIFICATE_CHECK:
        cb->certificate_check = j_git_transport_certificate_check_cb;
        break;
    case J_REMOTE_CALLBACK_TRANSFER_PROGRESS:
        cb->transfer_progress = j_git_transfer_progress_cb;
        break;
    case J_REMOTE_CALLBACK_UPDATE_TIP:
        cb->update_tips = j_git_remote_update_tips_cb;
        break;
    case J_REMOTE_CALLBACK_PACK_PROGRESS:
        cb->pack_progress = j_git_packbuilder_progress_cb;
        break;
    case J_REMOTE_CALLBACK_PUSH_TRANSFER_PROGRESS:
        cb->push_transfer_progress = j_git_push_transfer_progress_cb;
        break;
    case J_REMOTE_CALLBACK_PUSH_UPDATE_REFERENCE:
        cb->push_update_reference = j_git_push_update_reference_cb;
        break;
    case J_REMOTE_CALLBACK_PUSH_NEGOTIATION:
        cb->push_negotiation = j_git_push_negotiation_cb;
        break;
    case J_REMOTE_CALLBACK_TRANSPORT:
        cb->transport = j_git_transport_cb;
        break;
    case J_REMOTE_CALLBACK_URL_RESOLVE:
        cb->resolve_url = j_git_url_resolve_cb;
        break;
    default:
        break;
    }
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniIsValidName)(JNIEnv *env, jclass obj, jstring remote_name)
{
    char *c_remote_name = j_copy_of_jstring(env, remote_name, true);
    int r = git_remote_is_valid_name(c_remote_name);
    free(c_remote_name);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniList)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr)
{
    git_strarray *c_out = (git_strarray *)malloc(sizeof(git_strarray));
    int r = git_remote_list(c_out, (git_repository *)repoPtr);
    j_strarray_to_java_list(env, c_out, out);
    git_strarray_free(c_out);
    free(c_out);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniLookup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name)
{
    git_remote *c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_remote_lookup(&c_out, (git_repository *)repoPtr, c_name);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_name);
    return r;
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniName)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    const char *r = git_remote_name((git_remote *)remotePtr);
    return (*env)->NewStringUTF(env, r);
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniOwner)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    git_repository *r = git_remote_owner((git_remote *)remotePtr);
    return (jlong)r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPrune)(JNIEnv *env, jclass obj, jlong remotePtr, jlong callbacksPtr)
{
    int r = git_remote_prune((git_remote *)remotePtr, (git_remote_callbacks *)callbacksPtr);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPruneRefs)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    int r = git_remote_prune_refs((git_remote *)remotePtr);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPush)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr)
{
    git_strarray c_refspecs;
    git_strarray_of_jobject_array(env, refspecs, &c_refspecs);
    int r = git_remote_push((git_remote *)remotePtr, &c_refspecs, (git_push_options *)optsPtr);
    git_strarray_free(&c_refspecs);
    return r;
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniPushurl)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    const char *r = git_remote_pushurl((git_remote *)remotePtr);
    return r ? (*env)->NewStringUTF(env, r) : NULL;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniRefspecCount)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    size_t r = git_remote_refspec_count((git_remote *)remotePtr);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniRename)(JNIEnv *env, jclass obj, jobject problems, jlong repoPtr, jstring name, jstring new_name)
{
    git_strarray *c_problems = (git_strarray *)malloc(sizeof(git_strarray));
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_new_name = j_copy_of_jstring(env, new_name, true);
    int r = git_remote_rename(c_problems, (git_repository *)repoPtr, c_name, c_new_name);
    j_strarray_to_java_list(env, c_problems, problems);
    git_strarray_free(c_problems);
    free(c_problems);
    free(c_name);
    free(c_new_name);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniSetAutotag)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jint value)
{
    char *c_remote = j_copy_of_jstring(env, remote, true);
    int r = git_remote_set_autotag((git_repository *)repoPtr, c_remote, value);
    free(c_remote);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniSetPushurl)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring url)
{
    char *c_remote = j_copy_of_jstring(env, remote, true);
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_remote_set_pushurl((git_repository *)repoPtr, c_remote, c_url);
    free(c_remote);
    free(c_url);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniSetUrl)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring url)
{
    char *c_remote = j_copy_of_jstring(env, remote, true);
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_remote_set_url((git_repository *)repoPtr, c_remote, c_url);
    free(c_remote);
    free(c_url);
    return r;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniStats)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    const git_transfer_progress *r = git_remote_stats((git_remote *)remotePtr);
    return (jlong)r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniStop)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    git_remote_stop((git_remote *)remotePtr);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniUpdateTips)(JNIEnv *env, jclass obj, jlong remotePtr, jlong callbacksPtr, jint update_fetchhead, jint downloadTags, jstring reflog_message)
{
    char *c_reflog_message = j_copy_of_jstring(env, reflog_message, true);
    int r = git_remote_update_tips((git_remote *)remotePtr, (git_remote_callbacks *)callbacksPtr, update_fetchhead, downloadTags, c_reflog_message);
    free(c_reflog_message);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniUpload)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr)
{
    git_strarray c_refspecs;
    git_strarray_of_jobject_array(env, refspecs, &c_refspecs);
    int r = git_remote_upload((git_remote *)remotePtr, &c_refspecs, (git_push_options *)optsPtr);
    git_strarray_free(&c_refspecs);
    return r;
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniUrl)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    const char *r = git_remote_url((git_remote *)remotePtr);
    return r ? (*env)->NewStringUTF(env, r) : NULL;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetVersion)(JNIEnv *env, jclass obj, jlong createOptionsPtr)
{
    return ((git_remote_create_options *)createOptionsPtr)->version;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetRepository)(JNIEnv *env, jclass obj, jlong createOptionsPtr)
{
    return (jlong)((git_remote_create_options *)createOptionsPtr)->repository;
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetName)(JNIEnv *env, jclass obj, jlong createOptionsPtr)
{
    const char *name = ((git_remote_create_options *)createOptionsPtr)->name;
    return (*env)->NewStringUTF(env, name);
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetFetchspec)(JNIEnv *env, jclass obj, jlong createOptionsPtr)
{
    return (*env)->NewStringUTF(env, ((git_remote_create_options *)createOptionsPtr)->fetchspec);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetFlags)(JNIEnv *env, jclass obj, jlong createOptionsPtr)
{
    return ((git_remote_create_options *)createOptionsPtr)->flags;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetVersion)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jint version)
{
    ((git_remote_create_options *)createOptionsPtr)->version = (unsigned int)version;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetRepository)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jlong repository)
{
    ((git_remote_create_options *)createOptionsPtr)->repository = (git_repository *)repository;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetName)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jstring name)
{
    ((git_remote_create_options *)createOptionsPtr)->name = j_copy_of_jstring(env, name, true);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetFetchspec)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jstring fetchspec)
{
    ((git_remote_create_options *)createOptionsPtr)->fetchspec = j_copy_of_jstring(env, fetchspec, true);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetFlags)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jint flags)
{
    ((git_remote_create_options *)createOptionsPtr)->flags = (unsigned int)flags;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version)
{
    git_fetch_options *opts = (git_fetch_options *)malloc(sizeof(git_fetch_options));
    int r = git_fetch_init_options(opts, version);
    (*env)->CallVoidMethod(env, outPtr, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    git_fetch_options *opts = (git_fetch_options *)optsPtr;
    if (opts->callbacks.payload != NULL)
    {
        (*env)->DeleteGlobalRef(env, (jobject)(opts->callbacks.payload));
    }
    free(opts);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetVersion)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr)
{
    return ((git_fetch_options *)fetchOptionsPtr)->version;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetCallbacks)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr)
{
    return (jlong) & (((git_fetch_options *)fetchOptionsPtr)->callbacks);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetPrune)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr)
{
    return ((git_fetch_options *)fetchOptionsPtr)->prune;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetUpdateFetchhead)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr)
{
    return ((git_fetch_options *)fetchOptionsPtr)->update_fetchhead;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetDownloadTags)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr)
{
    return ((git_fetch_options *)fetchOptionsPtr)->download_tags;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetProxyOpts)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr)
{
    return (jlong) & (((git_fetch_options *)fetchOptionsPtr)->proxy_opts);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetCustomHeaders)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jobject outHeaderList)
{
    j_strarray_to_java_list(env, &(((git_fetch_options *)fetchOptionsPtr)->custom_headers), outHeaderList);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetVersion)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint version)
{
    ((git_fetch_options *)fetchOptionsPtr)->version = (int)version;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetDepth)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint depth)
{
    ((git_fetch_options *)fetchOptionsPtr)->depth = (int)depth;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetDepth)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr)
{
    return (jint)(((git_fetch_options *)fetchOptionsPtr)->depth);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetFollowRedirects)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint redirectT) {
    ((git_fetch_options *)fetchOptionsPtr)->follow_redirects = (git_remote_redirect_t)redirectT;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetFollowRedirects)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr) {
    return (jint)(((git_fetch_options *)fetchOptionsPtr)->follow_redirects);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetPrune)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint prune)
{
    ((git_fetch_options *)fetchOptionsPtr)->prune = (git_fetch_prune_t)prune;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetUpdateFetchhead)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint updateFetchhead)
{
    ((git_fetch_options *)fetchOptionsPtr)->update_fetchhead = (int)updateFetchhead;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetDownloadTags)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint downloadTags)
{
    ((git_fetch_options *)fetchOptionsPtr)->download_tags = (git_remote_autotag_option_t)downloadTags;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetCustomHeaders)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jobjectArray customHeaders)
{
    j_strarray_from_java(env, &(((git_fetch_options *)fetchOptionsPtr)->custom_headers), customHeaders);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPushOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version)
{
    git_push_options *opts = (git_push_options *)malloc(sizeof(git_push_options));
    int r = git_push_options_init(opts, version);
    (*env)->CallVoidMethod(env, outPtr, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    git_push_options *opts = (git_push_options *)optsPtr;
    if (opts->callbacks.payload != NULL)
    {
        (*env)->DeleteGlobalRef(env, (jobject)(opts->callbacks.payload));
    }
    free(opts);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetVersion)(JNIEnv *env, jclass obj, jlong pushOptionsPtr)
{
    return ((git_push_options *)pushOptionsPtr)->version;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetPbParallelism)(JNIEnv *env, jclass obj, jlong pushOptionsPtr)
{
    return ((git_push_options *)pushOptionsPtr)->pb_parallelism;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetCallbacks)(JNIEnv *env, jclass obj, jlong pushOptionsPtr)
{
    return (jlong) & (((git_push_options *)pushOptionsPtr)->callbacks);
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetProxyOpts)(JNIEnv *env, jclass obj, jlong pushOptionsPtr)
{
    return (jlong) & (((git_push_options *)pushOptionsPtr)->proxy_opts);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetCustomHeaders)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jobject outHeadersList)
{
    git_strarray *c_array = &(((git_push_options *)pushOptionsPtr)->custom_headers);
    j_strarray_to_java_list(env, c_array, outHeadersList);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsSetVersion)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jint version)
{
    ((git_push_options *)pushOptionsPtr)->version = (unsigned int)version;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsSetPbParallelism)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jint pbParallelism)
{
    ((git_push_options *)pushOptionsPtr)->pb_parallelism = (unsigned int)pbParallelism;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsSetCustomHeaders)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jobjectArray customHeaders)
{
    git_strarray *c_array = &(((git_push_options *)pushOptionsPtr)->custom_headers);
    j_strarray_from_java(env, c_array, customHeaders);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsSetRemotePushOptions)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jobjectArray remotePushOptions)
{
    git_strarray *c_array = &(((git_push_options *)pushOptionsPtr)->remote_push_options);
    j_strarray_from_java(env, c_array, remotePushOptions);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetRemotePushOptions)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jobject outRemotePushOptions)
{
    git_strarray *c_array = &(((git_push_options *)pushOptionsPtr)->remote_push_options);
    j_strarray_to_java_list(env, c_array, outRemotePushOptions);
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniPushUpdateNew)(JNIEnv *env, jclass obj)
{
    git_push_update *ptr = (git_push_update *)malloc(sizeof(git_push_update));
    ptr->dst_refname = NULL;
    ptr->src_refname = NULL;
    return (jlong)ptr;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateFree)(JNIEnv *env, jclass obj, jlong pushUpdatePtr)
{
    git_push_update *ptr = (git_push_update *)pushUpdatePtr;
    free(ptr->dst_refname);
    free(ptr->src_refname);
    free(ptr);
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniPushUpdateGetSrcRefname)(JNIEnv *env, jclass obj, jlong pushUpdatePtr)
{
    return (*env)->NewStringUTF(env, ((git_push_update *)pushUpdatePtr)->src_refname);
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniPushUpdateGetDstRefname)(JNIEnv *env, jclass obj, jlong pushUpdatePtr)
{
    return (*env)->NewStringUTF(env, ((git_push_update *)pushUpdatePtr)->dst_refname);
}
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Remote_jniPushUpdateGetSrc)(JNIEnv *env, jclass obj, jlong pushUpdatePtr)
{
    return j_git_oid_to_bytearray(env, &(((git_push_update *)pushUpdatePtr)->src));
}
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Remote_jniPushUpdateGetDst)(JNIEnv *env, jclass obj, jlong pushUpdatePtr)
{
    return j_git_oid_to_bytearray(env, &(((git_push_update *)pushUpdatePtr)->dst));
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateSetSrcRefname)(JNIEnv *env, jclass obj, jlong pushUpdatePtr, jstring srcRefname)
{
    git_push_update *ptr = (git_push_update *)pushUpdatePtr;
    ptr->src_refname = j_copy_of_jstring(env, srcRefname, true);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateSetDstRefname)(JNIEnv *env, jclass obj, jlong pushUpdatePtr, jstring dstRefname)
{
    git_push_update *ptr = (git_push_update *)pushUpdatePtr;
    ptr->dst_refname = j_copy_of_jstring(env, dstRefname, true);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateSetSrc)(JNIEnv *env, jclass obj, jlong pushUpdatePtr, jobject src)
{
    git_push_update *ptr = (git_push_update *)pushUpdatePtr;
    j_git_oid_from_java(env, src, &ptr->src);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateSetDst)(JNIEnv *env, jclass obj, jlong pushUpdatePtr, jobject dst)
{
    git_push_update *ptr = (git_push_update *)pushUpdatePtr;
    j_git_oid_from_java(env, dst, &ptr->dst);
}
