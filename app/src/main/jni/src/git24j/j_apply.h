#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_APPLY_H__
#define __GIT24J_APPLY_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Apply_jniToTree)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong preimagePtr, jlong diffPtr, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Apply_jniApply)(JNIEnv *env, jclass obj, jlong repoPtr, jlong diffPtr, jint location, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Apply_jniOptionsNew)(JNIEnv *env, jclass obj, jint version, jobject out, jobject deltaCb, jobject hunkCb);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Apply_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Apply_jniOptionsGetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Apply_jniOptionsSetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint flags);
#ifdef __cplusplus
}
#endif
#endif