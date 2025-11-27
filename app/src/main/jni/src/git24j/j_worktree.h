#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_WORKTREE_H__
#define __GIT24J_WORKTREE_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniAdd)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name, jstring path, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniAddInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Worktree_jniFree)(JNIEnv *env, jclass obj, jlong wtPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniIsLocked)(JNIEnv *env, jclass obj, jobject reason, jlong wtPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniIsPrunable)(JNIEnv *env, jclass obj, jlong wtPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniList)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniLock)(JNIEnv *env, jclass obj, jlong wtPtr, jstring reason);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniLookup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Worktree_jniName)(JNIEnv *env, jclass obj, jlong wtPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniOpenFromRepository)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Worktree_jniPath)(JNIEnv *env, jclass obj, jlong wtPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniPrune)(JNIEnv *env, jclass obj, jlong wtPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniPruneInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniUnlock)(JNIEnv *env, jclass obj, jlong wtPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniValidate)(JNIEnv *env, jclass obj, jlong wtPtr);
#ifdef __cplusplus
}
#endif
#endif