#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_PATHSPEC_H__
#define __GIT24J_PATHSPEC_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniNew)(JNIEnv *env, jclass obj, jobject out, jobjectArray pathspec);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Pathspec_jniFree)(JNIEnv *env, jclass obj, jlong psPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchesPath)(JNIEnv *env, jclass obj, jlong psPtr, jint flags, jstring path);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchWorkdir)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jint flags, jlong psPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchIndex)(JNIEnv *env, jclass obj, jobject out, jlong indexPtr, jint flags, jlong psPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchTree)(JNIEnv *env, jclass obj, jobject out, jlong treePtr, jint flags, jlong psPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchDiff)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jint flags, jlong psPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Pathspec_jniMatchListFree)(JNIEnv *env, jclass obj, jlong mPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchListEntrycount)(JNIEnv *env, jclass obj, jlong mPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Pathspec_jniMatchListEntry)(JNIEnv *env, jclass obj, jlong mPtr, jint pos);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Pathspec_jniMatchListDiffEntry)(JNIEnv *env, jclass obj, jlong mPtr, jint pos);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchListFailedEntrycount)(JNIEnv *env, jclass obj, jlong mPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Pathspec_jniMatchListFailedEntry)(JNIEnv *env, jclass obj, jlong mPtr, jint pos);
#ifdef __cplusplus
}
#endif
#endif