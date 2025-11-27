#include "j_common.h"
#include <jni.h>
#ifndef __GIT24J_REPOSITORY_H__
#define __GIT24J_REPOSITORY_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOpen)(JNIEnv *env, jclass obj, jobject ptrReceiver, jstring path);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOpenFromWorkTree)(JNIEnv *env, jclass obj, jobject ptrReceiver, jlong wtPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniWrapOdb)(JNIEnv *env, jclass obj, jobject ptrReceiver, jlong odbPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniDiscover)(JNIEnv *env, jclass obj, jobject buf, jstring startPath, jint acrossFs, jstring ceilingDirs);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOpenExt)(JNIEnv *env, jclass obj, jobject ptrReceiver, jstring path, jint flags, jstring ceilingDirs);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOpenBare)(JNIEnv *env, jclass obj, jobject ptrReceiver, jstring path);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Repository_jniFree)(JNIEnv *env, jclass obj, jlong repo);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniInit)(JNIEnv *env, jclass obj, jobject outRepo, jstring path, jint isBare);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniInitOptionsInit)(JNIEnv *env, jclass obj, jobject initOpts, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniInitOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniInitExt)(JNIEnv *env, jclass obj, jobject outRepo, jstring repoPath, jobject initOpts);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHead)(JNIEnv *env, jclass obj, jobject outRef, jlong repo);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHeadForWorktree)(JNIEnv *env, jclass obj, jobject outRef, jlong repo, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHeadDetached)(JNIEnv *env, jclass obj, jlong repo);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHeadUnborn)(JNIEnv *env, jclass obj, jlong repo);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIsEmpty)(JNIEnv *env, jclass obj, jlong repo);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniItemPath)(JNIEnv *env, jclass obj, jobject outBuf, jlong repo, jint item);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIndex)(JNIEnv *env, jclass obj, jobject outIndex, jlong repo);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Repository_jniPath)(JNIEnv *env, jclass obj, jlong repoPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Repository_jniWorkdir)(JNIEnv *env, jclass obj, jlong repoPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Repository_jniCommondir)(JNIEnv *env, jclass obj, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetWorkdir)(JNIEnv *env, jclass obj, jlong repoPtr, jstring workdir, jint updateGitlink);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIsBare)(JNIEnv *env, jclass obj, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIsWorktree)(JNIEnv *env, jclass obj, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniConfig)(JNIEnv *env, jclass obj, jobject outConfig, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniConfigSnapshot)(JNIEnv *env, jclass obj, jobject outConfig, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOdb)(JNIEnv *env, jclass obj, jobject outOdb, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniRefdb)(JNIEnv *env, jclass obj, jobject outRefdb, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIndex)(JNIEnv *env, jclass obj, jobject outIndex, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniMessage)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniMessageRemove)(JNIEnv *env, jclass obj, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniStateCleanup)(JNIEnv *env, jclass obj, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniFetchheadForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject consumer);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniMergeheadForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject consumer);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHashfile)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring path, jint type, jstring asPath);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetHead)(JNIEnv *env, jclass obj, jlong repoPtr, jstring refName);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetHeadDetached)(JNIEnv *env, jclass obj, jlong repoPtr, jobject oid);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetHeadDetachedFromAnnotated)(JNIEnv *env, jclass obj, jlong repoPtr, jlong commitishPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniDetachHead)(JNIEnv *env, jclass obj, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniState)(JNIEnv *env, jclass obj, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetNamespace)(JNIEnv *env, jclass obj, jlong repoPtr, jstring nmspace);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Repository_jniGetNamespace)(JNIEnv *env, jclass obj, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIsShallow)(JNIEnv *env, jclass obj, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIdent)(JNIEnv *env, jclass obj, jobject identity, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetIdent)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jstring email);
#ifdef __cplusplus
}
#endif
#endif
