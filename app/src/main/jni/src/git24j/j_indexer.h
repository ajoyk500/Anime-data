#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_INDEXER_H__
#define __GIT24J_INDEXER_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniNew)(JNIEnv *env, jclass obj, jobject out, jstring path, jint mode, jlong odbPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniAppend)(JNIEnv *env, jclass obj, jlong idxPtr, jbyteArray data, jint size, jlong statsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniCommit)(JNIEnv *env, jclass obj, jlong idxPtr, jlong statsPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Indexer_jniHash)(JNIEnv *env, jclass obj, jlong idxPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniFree)(JNIEnv *env, jclass obj, jlong idxPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version, jobject progressCb);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jbyte JNICALL J_MAKE_METHOD(Indexer_jniOptionsGetVerify)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniOptionsSetVerify)(JNIEnv *env, jclass obj, jlong optionsPtr, jbyte verify);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Indexer_jniProgressNew)(JNIEnv *env, jclass obj);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetTotalObjects)(JNIEnv *env, jclass obj, jlong progressPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetIndexedObjects)(JNIEnv *env, jclass obj, jlong progressPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetReceivedObjects)(JNIEnv *env, jclass obj, jlong progressPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetLocalObjects)(JNIEnv *env, jclass obj, jlong progressPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetTotalDeltas)(JNIEnv *env, jclass obj, jlong progressPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetIndexedDeltas)(JNIEnv *env, jclass obj, jlong progressPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetReceivedBytes)(JNIEnv *env, jclass obj, jlong progressPtr);
#ifdef __cplusplus
}
#endif
#endif