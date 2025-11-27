#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_ANNOTATED_COMMIT_H__
#define __GIT24J_ANNOTATED_COMMIT_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(AnnotatedCommit_jniFromRef)(JNIEnv *env, jclass obj, jobject outAc, jlong repoPtr, jlong refPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(AnnotatedCommit_jniFromFetchHead)(JNIEnv *env, jclass obj, jobject outAc, jlong repoPtr, jstring branchName, jstring remoteUrl, jobject oid);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(AnnotatedCommit_jniLookup)(JNIEnv *env, jclass obj, jobject outAc, jlong repoPtr, jobject oid);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(AnnotatedCommit_jniFromRevspec)(JNIEnv *env, jclass obj, jobject outAc, jlong repoPtr, jstring revspec);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(AnnotatedCommit_jniId)(JNIEnv *env, jclass obj, jlong acPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(AnnotatedCommit_jniRef)(JNIEnv *env, jclass obj, jlong acPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(AnnotatedCommit_jniFree)(JNIEnv *env, jclass obj, jlong acPtr);
#ifdef __cplusplus
}
#endif
#endif
