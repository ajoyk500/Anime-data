#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_CHERRYPICK_H__
#define __GIT24J_CHERRYPICK_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsGetMainline)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsSetMainline)(JNIEnv *env, jclass obj, jlong optionsPtr, jint mainline);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsGetMergeOpts)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsGetCheckoutOpts)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniCommit)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong cherrypickCommitPtr, jlong ourCommitPtr, jint mainline, jlong mergeOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniCherrypick)(JNIEnv *env, jclass obj, jlong repoPtr, jlong commitPtr, jlong cherrypickOptionsPtr);
#ifdef __cplusplus
}
#endif
#endif
