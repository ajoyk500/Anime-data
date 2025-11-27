#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_DESCRIBE_H__
#define __GIT24J_DESCRIBE_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT int JNICALL J_MAKE_METHOD(Describe_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetMaxCandidatesTags)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetDescribeStrategy)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Describe_jniOptionsGetPattern)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetOnlyFollowFirstParent)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniOptionsGetShowCommitOidAsFallback)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetMaxCandidatesTags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint maxCandidatesTags);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetDescribeStrategy)(JNIEnv *env, jclass obj, jlong optionsPtr, jint describeStrategy);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetPattern)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring pattern);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetOnlyFollowFirstParent)(JNIEnv *env, jclass obj, jlong optionsPtr, jint onlyFollowFirstParent);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniOptionsSetShowCommitOidAsFallback)(JNIEnv *env, jclass obj, jlong optionsPtr, jint showCommitOidAsFallback);
    JNIEXPORT int JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsGetVersion)(JNIEnv *env, jclass obj, jlong formatOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsGetAbbreviatedSize)(JNIEnv *env, jclass obj, jlong formatOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsGetAlwaysUseLongFormat)(JNIEnv *env, jclass obj, jlong formatOptionsPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsGetDirtySuffix)(JNIEnv *env, jclass obj, jlong formatOptionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsSetVersion)(JNIEnv *env, jclass obj, jlong formatOptionsPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsSetAbbreviatedSize)(JNIEnv *env, jclass obj, jlong formatOptionsPtr, jint abbreviatedSize);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsSetAlwaysUseLongFormat)(JNIEnv *env, jclass obj, jlong formatOptionsPtr, jint alwaysUseLongFormat);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniFormatOptionsSetDirtySuffix)(JNIEnv *env, jclass obj, jlong formatOptionsPtr, jstring dirtySuffix);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniCommit)(JNIEnv *env, jclass obj, jobject result, jlong committishPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniWorkdir)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Describe_jniFormat)(JNIEnv *env, jclass obj, jobject out, jlong resultPtr, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Describe_jniResultFree)(JNIEnv *env, jclass obj, jlong resultPtr);
#ifdef __cplusplus
}
#endif
#endif