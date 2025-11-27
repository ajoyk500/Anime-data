#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_PACKBUILDER_H__
#define __GIT24J_PACKBUILDER_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniSetThreads)(JNIEnv *env, jclass obj, jlong pbPtr, jint n);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsert)(JNIEnv *env, jclass obj, jlong pbPtr, jobject id, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsertTree)(JNIEnv *env, jclass obj, jlong pbPtr, jobject id);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsertCommit)(JNIEnv *env, jclass obj, jlong pbPtr, jobject id);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsertWalk)(JNIEnv *env, jclass obj, jlong pbPtr, jlong walkPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsertRecur)(JNIEnv *env, jclass obj, jlong pbPtr, jobject id, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniWriteBuf)(JNIEnv *env, jclass obj, jobject buf, jlong pbPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniWrite)(JNIEnv *env, jclass obj, jlong pbPtr, jstring path, jint mode, jobject progressCb);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(PackBuilder_jniHash)(JNIEnv *env, jclass obj, jlong pbPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniForeach)(JNIEnv *env, jclass obj, jlong pbPtr, jobject foreachCb);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniObjectCount)(JNIEnv *env, jclass obj, jlong pbPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniWritten)(JNIEnv *env, jclass obj, jlong pbPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniSetCallbacks)(JNIEnv *env, jclass obj, jlong pbPtr, jobject progressCb);
    JNIEXPORT void JNICALL J_MAKE_METHOD(PackBuilder_jniFree)(JNIEnv *env, jclass obj, jlong pbPtr);
#ifdef __cplusplus
}
#endif
#endif