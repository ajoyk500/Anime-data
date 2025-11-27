#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_REFSPEC_H__
#define __GIT24J_REFSPEC_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniParse)(JNIEnv *env, jclass obj, jobject refspec, jstring input, jint is_fetch);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Refspec_jniFree)(JNIEnv *env, jclass obj, jlong refspecPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Refspec_jniSrc)(JNIEnv *env, jclass obj, jlong refspecPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Refspec_jniDst)(JNIEnv *env, jclass obj, jlong refspecPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Refspec_jniString)(JNIEnv *env, jclass obj, jlong refspecPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniForce)(JNIEnv *env, jclass obj, jlong refspecPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniDirection)(JNIEnv *env, jclass obj, jlong specPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniSrcMatches)(JNIEnv *env, jclass obj, jlong refspecPtr, jstring refname);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniDstMatches)(JNIEnv *env, jclass obj, jlong refspecPtr, jstring refname);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniTransform)(JNIEnv *env, jclass obj, jobject out, jlong specPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniRtransform)(JNIEnv *env, jclass obj, jobject out, jlong specPtr, jstring name);
#ifdef __cplusplus
}
#endif
#endif