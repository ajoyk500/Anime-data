#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_CLONE_H__
#define __GIT24J_CLONE_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniClone)(JNIEnv *env, jclass obj, jobject out, jstring url, jstring local_path, jlong optionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniSetVersion)(JNIEnv *env, jclass obj, jlong clonePtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsNew)(JNIEnv *env, jclass obj, jint version, jobject outOpts);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetCheckoutOpts)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetFetchOpts)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetBare)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetLocal)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Clone_jniOptionsGetCheckoutBranch)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetCheckoutBranch)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring branch);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetCheckoutOpts)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong checkoutOpts);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetFetchOpts)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong fetchOpts);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetBare)(JNIEnv *env, jclass obj, jlong optionsPtr, jint bare);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetLocal)(JNIEnv *env, jclass obj, jlong optionsPtr, jint local);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetRepositoryCb)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject repositoryCb);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetRemoteCb)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject remoteCb);
#ifdef __cplusplus
}
#endif
#endif
