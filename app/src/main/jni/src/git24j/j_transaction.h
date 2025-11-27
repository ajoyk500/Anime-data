#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_TRANSACTION_H__
#define __GIT24J_TRANSACTION_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniLockRef)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniSetTarget)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname, jobject target, jlong sigPtr, jstring msg);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniSetSymbolicTarget)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname, jstring target, jlong sigPtr, jstring msg);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniSetReflog)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname, jlong reflogPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniRemove)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniCommit)(JNIEnv *env, jclass obj, jlong txPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Transaction_jniFree)(JNIEnv *env, jclass obj, jlong txPtr);
#ifdef __cplusplus
}
#endif
#endif