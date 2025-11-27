#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_STASH_H__
#define __GIT24J_STASH_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniSave)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong stasherPtr, jstring message, jint flags);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniApply)(JNIEnv *env, jclass obj, jlong repoPtr, jint index, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject callback);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniDrop)(JNIEnv *env, jclass obj, jlong repoPtr, jint index);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniPop)(JNIEnv *env, jclass obj, jlong repoPtr, jint index, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsFree)(JNIEnv *env, jclass obj, jlong applyOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsGetFlags)(JNIEnv *env, jclass obj, jlong applyOptionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsGetCheckoutOptions)(JNIEnv *env, jclass obj, jlong applyOptionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsSetFlags)(JNIEnv *env, jclass obj, jlong applyOptionsPtr, jint flags);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsSetProgressCb)(JNIEnv *env, jclass obj, jlong applyOptionsPtr, jobject progressCb);
#ifdef __cplusplus
}
#endif
#endif