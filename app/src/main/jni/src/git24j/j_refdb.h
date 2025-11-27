#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_REFDB_H__
#define __GIT24J_REFDB_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refdb_jniNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refdb_jniOpen)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refdb_jniCompress)(JNIEnv *env, jclass obj, jlong refdbPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Refdb_jniFree)(JNIEnv *env, jclass obj, jlong refdbPtr);
#ifdef __cplusplus
}
#endif
#endif