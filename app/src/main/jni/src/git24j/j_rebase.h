#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_REBASE_H__
#define __GIT24J_REBASE_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInit)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong branchPtr, jlong upstreamPtr, jlong ontoPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOpen)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOperationEntrycount)(JNIEnv *env, jclass obj, jlong rebasePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOperationCurrent)(JNIEnv *env, jclass obj, jlong rebasePtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Rebase_jniOperationByindex)(JNIEnv *env, jclass obj, jlong rebasePtr, jint idx);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniNext)(JNIEnv *env, jclass obj, jobject operation, jlong rebasePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInmemoryIndex)(JNIEnv *env, jclass obj, jobject index, jlong rebasePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniCommit)(JNIEnv *env, jclass obj, jobject id, jlong rebasePtr, jlong authorPtr, jlong committerPtr, jstring message_encoding, jstring message);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniAbort)(JNIEnv *env, jclass obj, jlong rebasePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniFinish)(JNIEnv *env, jclass obj, jlong rebasePtr, jlong signaturePtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniFree)(JNIEnv *env, jclass obj, jlong rebasePtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Rebase_jniOntoId)(JNIEnv *env, jclass obj, jlong rebasePtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Rebase_jniOntoName)(JNIEnv *env, jclass obj, jlong rebasePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Rebase_jniOrigHeadId)(JNIEnv *env, jclass obj, jlong rebasePtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Rebase_jniOrigHeadName)(JNIEnv *env, jclass obj, jlong rebasePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOperationGetType)(JNIEnv *env, jclass obj, jlong operationPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Rebase_jniOperationGetId)(JNIEnv *env, jclass obj, jlong operationPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Rebase_jniOperationGetExec)(JNIEnv *env, jclass obj, jlong operationPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetQuiet)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetInmemory)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetRewriteNotesRef)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetMergeOptions)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetCheckoutOptions)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetQuiet)(JNIEnv *env, jclass obj, jlong optionsPtr, jint quiet);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetInmemory)(JNIEnv *env, jclass obj, jlong optionsPtr, jint inmemory);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetRewriteNotesRef)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring rewriteNotesRef);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetSigningCb)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject signingCb);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetPayload)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong payload);
#ifdef __cplusplus
}
#endif
#endif