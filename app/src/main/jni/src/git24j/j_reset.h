#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_RESET_H__
#define __GIT24J_RESET_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reset_jniReset)(JNIEnv *env, jclass obj, jlong repoPtr, jlong targetPtr, jint reset_type, jlong checkoutOptsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reset_jniFromAnnotated)(JNIEnv *env, jclass obj, jlong repoPtr, jlong commitPtr, jint reset_type, jlong checkoutOptsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reset_jniDefault)(JNIEnv *env, jclass obj, jlong repoPtr, jlong targetPtr, jobjectArray pathspecs);
#ifdef __cplusplus
}
#endif
#endif