#include "j_common.h"
#include "j_mappers.h"
#include <jni.h>
#ifndef __GIT24J_GLOBAL_H__
#define __GIT24J_GLOBAL_H__
#ifdef __cplusplus
extern "C"
{
#endif
    jclass j_find_and_hold_clz(JNIEnv *env, const char *descriptor);
    extern JavaVM *globalJvm;
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_init)(JNIEnv *, jclass);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_shutdown)(JNIEnv *, jclass);
    JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_version)(JNIEnv *, jclass);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Libgit2_features)(JNIEnv *, jclass);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_jniShadowFree)(JNIEnv *env, jclass obj, jlong ptr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetMwindowSize)(JNIEnv *env, jclass obj, jlong mWindowSize);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetMwindowSize)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetMWindowMappedLimit)(JNIEnv *env, jclass obj, jlong mWindowMappedLimit);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetMWindowMappedLimit)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetMWindowFileLimit)(JNIEnv *env, jclass obj, jlong mWindowFileLimit);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetMWindowFileLimit)(JNIEnv *env, jclass obj);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetSearchPath)(JNIEnv *env, jclass obj, jint configLevel);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetSearchPath)(JNIEnv *env, jclass obj, jint configLevel, jstring path);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetCacheObjectLimit)(JNIEnv *env, jclass obj, jint type, jlong size);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetCacheMaxSize)(JNIEnv *env, jclass obj, jlong size);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableCaching)(JNIEnv *env, jclass obj, jboolean enable);
    JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetCachedMemory)(JNIEnv *env, jclass obj);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetTemplatePath)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetTemplatePath)(JNIEnv *env, jclass obj, jstring path);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetSslCertLocations)(JNIEnv *env, jclass obj, jstring file, jstring path);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetUserAgent)(JNIEnv *env, jclass obj, jstring useragent);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableStrictObjectCreation)(JNIEnv *env, jclass obj, jboolean enable);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableStrictSymbolicRefCreation)(JNIEnv *env, jclass obj, jboolean enable);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetSslCiphers)(JNIEnv *env, jclass obj, jstring sslCiphers);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetUserAgent)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableOfsDelta)(JNIEnv *env, jclass obj, jboolean enable);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableFsyncGitdir)(JNIEnv *env, jclass obj, jboolean enable);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetWindowsSharemode)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetWindowsSharemode)(JNIEnv *env, jclass obj, jlong createFileShareMode);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableStrictHashVerification)(JNIEnv *env, jclass obj, jboolean enable);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableUnsavedIndexSafety)(JNIEnv *env, jclass obj, jboolean enable);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetPackMaxObjects)(JNIEnv *env, jclass obj, jlong maxObjects);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetPackMaxObjects)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptDisablePackKeepFileChecks)(JNIEnv *env, jclass obj, jboolean disable);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableHttpExpectContinue)(JNIEnv *env, jclass obj, jboolean expect);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetOdbPackedPriority)(JNIEnv *env, jclass obj, jlong priority);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetOdbLoosePriority)(JNIEnv *env, jclass obj, jlong priority);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetExtensions)(JNIEnv *env, jclass obj, jobjectArray extensionsArray);
    JNIEXPORT jobjectArray JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetExtensions)(JNIEnv *env, jclass obj);
    JNIEXPORT jboolean JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetOwnerValidation)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetOwnerValidation)(JNIEnv *env, jclass obj, jboolean validateOwnership);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetHomedir)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetHomedir)(JNIEnv *env, jclass obj, jstring homedir);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetServerConnectTimeout)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetServerConnectTimeout)(JNIEnv *env, jclass obj, jlong timeout);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetServerTimeout)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetServerTimeout)(JNIEnv *env, jclass obj, jlong timeout);
#ifdef __cplusplus
}
#endif
#endif
