#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_ATTR_H__
#define __GIT24J_ATTR_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniValue)(JNIEnv *env, jclass obj, jstring attr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniGet)(JNIEnv *env, jclass obj, jobject valueOut, jlong repoPtr, jint flags, jstring path, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniGetMany)(JNIEnv *env, jclass obj, jobject valuesOut, jlong repoPtr, jint flags, jstring path, jobjectArray names);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jint flags, jstring path, jobject callback);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniCacheFlush)(JNIEnv *env, jclass obj, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniAddMacro)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jstring values);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniForeachCb)(JNIEnv *env, jclass obj, jstring name, jstring value);
#ifdef __cplusplus
}
#endif
#endif