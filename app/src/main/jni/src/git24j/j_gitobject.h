#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_GITOBJECT_H__
#define __GIT24J_GITOBJECT_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT void JNICALL J_MAKE_METHOD(GitObject_jniFree)(JNIEnv *env, jclass obj, jlong objPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniType)(JNIEnv *env, jclass obj, jlong objPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(GitObject_jniId)(JNIEnv *env, jclass obj, jlong objPtr, jobject outId);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniShortId)(JNIEnv *env, jclass obj, jobject outBuf, jlong objPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniLookup)(JNIEnv *env, jclass obj, jobject outObj, jlong repoPtr, jobject oid, jint objType);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniLookupPrefix)(JNIEnv *env, jclass obj, jobject outObj, jlong repoPtr, jstring oidStr, jint objType);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(GitObject_jniOwner)(JNIEnv *env, jclass obj, jlong objPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniPeel)(JNIEnv *env, jclass obj, jobject outObj, jlong objPtr, jint objType);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(GitObject_jniDup)(JNIEnv *env, jclass obj, jobject outObj, jlong objPtr);
#ifdef __cplusplus
}
#endif
#endif
