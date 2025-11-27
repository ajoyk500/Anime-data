#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_REVERT_H__
#define __GIT24J_REVERT_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revert_jniCommit)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong revertCommitPtr, jlong ourCommitPtr, jint mainline, jlong mergeOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revert_jniRevert)(JNIEnv *env, jclass obj, jlong repoPtr, jlong commitPtr, jlong givenOptsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revert_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revert_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revert_jniOptionsGetMainline)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Revert_jniOptionsGetMergeOpts)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Revert_jniOptionsGetCheckoutOpts)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revert_jniOptionsSetMainline)(JNIEnv *env, jclass obj, jlong optionsPtr, jint mainline);
#ifdef __cplusplus
}
#endif
#endif