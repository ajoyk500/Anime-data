#include "j_common.h"
#include <jni.h>
#ifndef __GIT24J_REVWALK_H__
#define __GIT24J_REVWALK_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniAddHideCb)(JNIEnv *env, jclass obj, jlong walkPtr, jobject hideCb, jobject outPayload);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniFreeHideCb)(JNIEnv *env, jclass obj, jlong payloadPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniFree)(JNIEnv *env, jclass obj, jlong walkPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniHide)(JNIEnv *env, jclass obj, jlong walkPtr, jobject commitId);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniHideGlob)(JNIEnv *env, jclass obj, jlong walkPtr, jstring glob);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniHideHead)(JNIEnv *env, jclass obj, jlong walkPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniHideRef)(JNIEnv *env, jclass obj, jlong walkPtr, jstring refname);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniNext)(JNIEnv *env, jclass obj, jobject out, jlong walkPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPush)(JNIEnv *env, jclass obj, jlong walkPtr, jobject id);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPushGlob)(JNIEnv *env, jclass obj, jlong walkPtr, jstring glob);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPushHead)(JNIEnv *env, jclass obj, jlong walkPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPushRange)(JNIEnv *env, jclass obj, jlong walkPtr, jstring range);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPushRef)(JNIEnv *env, jclass obj, jlong walkPtr, jstring refname);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Revwalk_jniRepository)(JNIEnv *env, jclass obj, jlong walkPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniReset)(JNIEnv *env, jclass obj, jlong walkerPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniSimplifyFirstParent)(JNIEnv *env, jclass obj, jlong walkPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniSorting)(JNIEnv *env, jclass obj, jlong walkPtr, jint sortMode);
#ifdef __cplusplus
}
#endif
#endif