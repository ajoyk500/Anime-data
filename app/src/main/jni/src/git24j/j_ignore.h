#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_IGNORE_H__
#define __GIT24J_IGNORE_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Ignore_jniAddRule)(JNIEnv *env, jclass obj, jlong repoPtr, jstring rules);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Ignore_jniClearInternalRules)(JNIEnv *env, jclass obj, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Ignore_jniPathIsIgnored)(JNIEnv *env, jclass obj, jobject ignored, jlong repoPtr, jstring path);
#ifdef __cplusplus
}
#endif
#endif