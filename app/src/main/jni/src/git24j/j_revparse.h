#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_REFPARSE_H__
#define __GIT24J_REFPARSE_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revparse_jniLookup)(JNIEnv *env, jclass obj, jobject revspec, jlong repoPtr, jstring spec);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revparse_jniSingle)(JNIEnv *env, jclass obj, jobject outObj, jlong repoPtr, jstring spec);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revparse_jniExt)(JNIEnv *env, jclass obj, jobject outObj, jobject outRef, jlong repoPtr, jstring spec);
#ifdef __cplusplus
}
#endif
#endif