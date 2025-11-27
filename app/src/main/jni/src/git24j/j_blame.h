#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_BLAME_H__
#define __GIT24J_BLAME_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniGetHunkCount)(JNIEnv *env, jclass obj, jlong blamePtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniGetHunkByindex)(JNIEnv *env, jclass obj, jlong blamePtr, jint index);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniGetHunkByline)(JNIEnv *env, jclass obj, jlong blamePtr, jint lineno);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniFile)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring path, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniBuffer)(JNIEnv *env, jclass obj, jobject out, jlong referencePtr, jstring buffer, jint bufferLen);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Blame_jniFree)(JNIEnv *env, jclass obj, jlong blamePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniHunkGetLinesInHunk)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Blame_jniHunkGetFinalCommitId)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniHunkGetFinalStartLineNumber)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniHunkGetFinalSignature)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigCommitId)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigPath)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigStartLineNumber)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigSignature)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jchar JNICALL J_MAKE_METHOD(Blame_jniHunkGetBoundary)(JNIEnv *env, jclass obj, jlong hunkPtr);
#ifdef __cplusplus
}
#endif
#endif