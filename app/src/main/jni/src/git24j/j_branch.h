#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_BRANCH_H__
#define __GIT24J_BRANCH_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniCreate)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jlong targetPtr, jint force);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniCreateFromAnnotated)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jlong annoCommitPtr, jint force);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniDelete)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniIteratorNew)(JNIEnv *env, jclass obj, jobject outBranchIter, jlong repoPtr, jint listFlags);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniNext)(JNIEnv *env, jclass obj, jobject outRef, jobject outType, jlong branchIterPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Branch_jniIteratorFree)(JNIEnv *env, jclass obj, jlong branchIterPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniMove)(JNIEnv *env, jclass obj, jobject outRef, jlong branchPtr, jstring branchName, jint force);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniLookup)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jint branchType);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniName)(JNIEnv *env, jclass obj, jobject outStr, jlong refPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniUpstream)(JNIEnv *env, jclass obj, jobject outRef, jlong branchPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniSetUpstream)(JNIEnv *env, jclass obj, jlong refPtr, jstring upstreamName);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniUpstreamName)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jstring refName);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniIsHead)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniIsCheckedOut)(JNIEnv *env, jclass obj, jlong rePftr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniRemoteName)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jstring canonicalBranchName);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniUpstreamRemote)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jstring refName);
#ifdef __cplusplus
}
#endif
#endif