#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_MESSAGE_H__
#define __GIT24J_MESSAGE_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Message_jniPrettify)(JNIEnv *env, jclass obj, jobject out, jstring message, jint strip_comments, jchar comment_char);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Message_jniTrailers)(JNIEnv *env, jclass obj, jobject outArrPtr, jstring message);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Message_jniTrailerArrayFree)(JNIEnv *env, jclass obj, jlong arrPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Message_jniTrailerGetKey)(JNIEnv *env, jclass obj, jlong trailerPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Message_jniTrailerGetValue)(JNIEnv *env, jclass obj, jlong trailerPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Message_jniTrailerArrayGetTrailer)(JNIEnv *env, jclass obj, jlong trailerArrayPtr, jint idx);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Message_jniTrailerArrayGetCount)(JNIEnv *env, jclass obj, jlong trailerArrayPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Message_jniTrailerArrayGetTrailerBlock)(JNIEnv *env, jclass obj, jlong trailerArrayPtr);
#ifdef __cplusplus
}
#endif
#endif