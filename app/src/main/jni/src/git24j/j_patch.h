#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_PATCH_H__
#define __GIT24J_PATCH_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromDiff)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jint idx);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromBlobs)(JNIEnv *env, jclass obj, jobject out, jlong oldBlobPtr, jstring old_as_path, jlong newBlobPtr, jstring new_as_path, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromBlobAndBuffer)(JNIEnv *env, jclass obj, jobject out, jlong oldBlobPtr, jstring old_as_path, jbyteArray buffer, jint bufferLen, jstring buffer_as_path, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromBuffers)(JNIEnv *env, jclass obj, jobject out, jbyteArray oldBuffer, jint oldLen, jstring old_as_path, jbyteArray newBuffer, jint newLen, jstring new_as_path, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Patch_jniFree)(JNIEnv *env, jclass obj, jlong patchPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Patch_jniGetDelta)(JNIEnv *env, jclass obj, jlong patchPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniNumHunks)(JNIEnv *env, jclass obj, jlong patchPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniLineStats)(JNIEnv *env, jclass obj, jobject totalContext, jobject totalAdditions, jobject totalDeletions, jlong patchPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniGetHunk)(JNIEnv *env, jclass obj, jobject out, jobject linesInHunk, jlong patchPtr, jint hunkIdx);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniNumLinesInHunk)(JNIEnv *env, jclass obj, jlong patchPtr, jint hunkIdx);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniGetLineInHunk)(JNIEnv *env, jclass obj, jobject out, jlong patchPtr, jint hunkIdx, jint lineOfHunk);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniSize)(JNIEnv *env, jclass obj, jlong patchPtr, jint include_context, jint include_hunk_headers, jint include_file_headers);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniPrint)(JNIEnv *env, jclass obj, jlong patchPtr, jobject printCb);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniToBuf)(JNIEnv *env, jclass obj, jobject out, jlong patchPtr);
#ifdef __cplusplus
}
#endif
#endif