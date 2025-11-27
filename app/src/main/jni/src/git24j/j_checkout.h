#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_CHECKOUT_H__
#define __GIT24J_CHECKOUT_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniHead)(JNIEnv *env, jclass obj, jlong repoPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniIndex)(JNIEnv *env, jclass obj, jlong repoPtr, jlong indexPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniTree)(JNIEnv *env, jclass obj, jlong repoPtr, jlong treeishPtr, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniProgressCb)(JNIEnv *env, jclass obj, jstring path, jint completedSteps, jint totalSteps);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniPerfdataCb)(JNIEnv *env, jclass obj, jlong perfdataPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetNotifyCb)(JNIEnv *env, jclass obj, jlong optsPtr, jobject notifyCb);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetProgressCb)(JNIEnv *env, jclass obj, jlong optsPtr, jobject progressCb);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetPerfdataCb)(JNIEnv *env, jclass obj, jlong optsPtr, jobject perfdataCb);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetStrategy)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetStrategy)(JNIEnv *env, jclass obj, jlong optsPtr, jint strategy);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetDisableFilters)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetDisableFilters)(JNIEnv *env, jclass obj, jlong optsPtr, jint disalbe_filters);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetDirMode)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetDirMode)(JNIEnv *env, jclass obj, jlong optsPtr, jint mode);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetFileMode)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetFileMode)(JNIEnv *env, jclass obj, jlong optsPtr, jint mode);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetFileOpenFlags)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetFileOpenFlags)(JNIEnv *env, jclass obj, jlong optsPtr, jint flags);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetNotifyFlags)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetNotifyFlags)(JNIEnv *env, jclass obj, jlong optsPtr, jint flags);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetPaths)(JNIEnv *env, jclass obj, jlong optsPtr, jobjectArray paths);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetPaths)(JNIEnv *env, jclass obj, jlong optsPtr, jobject outPathsList);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetBaseline)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetBaseline)(JNIEnv *env, jclass obj, jlong optsPtr, jlong baselinePtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetBaselineIndex)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetBaselineIndex)(JNIEnv *env, jclass obj, jlong optsPtr, jlong baselineIndexPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetTargetDirectory)(JNIEnv *env, jclass obj, jlong optsPtr, jstring target_directory);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetTargetDirectory)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetAncestorLabel)(JNIEnv *env, jclass obj, jlong optsPtr, jstring ancestor_label);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetAncestorLabel)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetOurLabel)(JNIEnv *env, jclass obj, jlong optsPtr, jstring our_label);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetOurLabel)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetTheirLable)(JNIEnv *env, jclass obj, jlong optsPtr, jstring their_label);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetTheirLable)(JNIEnv *env, jclass obj, jlong optsPtr);
#ifdef __cplusplus
}
#endif
#endif