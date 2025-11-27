#include "j_common.h"
#include <jni.h>
#ifndef __GIT24J_STATUS_H__
#define __GIT24J_STATUS_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniFile)(JNIEnv *env, jclass obj, jobject statusFlags, jlong repoPtr, jstring path);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniListNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniListEntrycount)(JNIEnv *env, jclass obj, jlong statuslistPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Status_jniByindex)(JNIEnv *env, jclass obj, jlong statuslistPtr, jint idx);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniListFree)(JNIEnv *env, jclass obj, jlong statuslistPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniShouldIgnore)(JNIEnv *env, jclass obj, jobject ignored, jlong repoPtr, jstring path);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniOptionsGetShow)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniOptionsGetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsGetPathspec)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject outListPathSpec);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Status_jniOptionsGetBaseline)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetShow)(JNIEnv *env, jclass obj, jlong optionsPtr, jint show);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint flags);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetPathspec)(JNIEnv *env, jclass obj, jlong optionsPtr, jobjectArray pathspec);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniOptionsSetBaseline)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong baseline);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniEntryGetStatus)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Status_jniEntryGetHeadToIndex)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Status_jniEntryGetIndexToWorkdir)(JNIEnv *env, jclass obj, jlong entryPtr);
#ifdef __cplusplus
}
#endif
#endif