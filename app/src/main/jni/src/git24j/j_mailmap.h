#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_MAILMAP_H__
#define __GIT24J_MAILMAP_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT void JNICALL J_MAKE_METHOD(Mailmap_jniFree)(JNIEnv *env, jclass obj, jlong mmPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniAddEntry)(JNIEnv *env, jclass obj, jlong mmPtr, jstring realName, jstring realEmail, jstring replaceName, jstring replaceEmail);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniFromBuffer)(JNIEnv *env, jclass obj, jobject outPtr, jstring buf);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniFromRepository)(JNIEnv *env, jclass obj, jobject outPtr, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniResolve)(JNIEnv *env, jclass obj, jobject outRealName, jobject outRealEmail, jlong mmPtr, jstring name, jstring email);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniResolveSignature)(JNIEnv *env, jclass obj, jobject out, jlong mmPtr, jlong sigPtr);
#ifdef __cplusplus
}
#endif
#endif