#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_BLOB_H__
#define __GIT24J_BLOB_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniLookup)(JNIEnv *env, jclass obj, jobject outBlob, jlong repoPtr, jobject oid);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniLookupPrefix)(JNIEnv *env, jclass obj, jobject outBlob, jlong repoPtr, jstring shortId);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Blob_jniFree)(JNIEnv *env, jclass obj, jlong blobPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Blob_jniId)(JNIEnv *env, jclass obj, jlong blobPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blob_jniOwner)(JNIEnv *env, jclass obj, jlong blobPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blob_jniRawContent)(JNIEnv *env, jclass obj, jlong blobPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blob_jniRawSize)(JNIEnv *env, jclass obj, jlong blobPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromWorkdir)(JNIEnv *env, jclass obj, jobject outId, jlong repoPtr, jstring relativePath);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromDisk)(JNIEnv *env, jclass obj, jobject outId, jlong repoPtr, jstring path);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromStream)(JNIEnv *env, jclass obj, jobject outStream, jlong repoPtr, jstring hintPath);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromStreamCommit)(JNIEnv *env, jclass obj, jobject outId, jlong streamPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromBuffer)(JNIEnv *env, jclass obj, jobject outId, jlong repoPtr, jbyteArray buf);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniIsBinary)(JNIEnv *env, jclass obj, jlong blobPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniDup)(JNIEnv *env, jclass obj, jobject outDest, jlong srcPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniFilteredContent)(JNIEnv *env, jclass obj, jobject out, jlong blobPtr, jstring as_path, jint check_for_binary_data);
#ifdef __cplusplus
}
#endif
#endif
