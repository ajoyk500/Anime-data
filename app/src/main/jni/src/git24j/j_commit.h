#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_COMMIT_H__
#define __GIT24J_COMMIT_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessageEncoding)(JNIEnv *env, jclass obj, jlong commitPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessage)(JNIEnv *env, jclass obj, jlong commitPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessageRaw)(JNIEnv *env, jclass obj, jlong commitPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniSummary)(JNIEnv *env, jclass obj, jlong commitPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniBody)(JNIEnv *env, jclass obj, jlong commitPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Commit_jniTime)(JNIEnv *env, jclass obj, jlong commitPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniTimeOffset)(JNIEnv *env, jclass obj, jlong commitPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Commit_jniCommitter)(JNIEnv *env, jclass obj, jlong commitPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Commit_jniAuthor)(JNIEnv *env, jclass obj, jlong commitPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCommitterWithMailmap)(JNIEnv *env, jclass obj, jobject out, jlong commitPtr, jlong mailmapPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniAuthorWithMailmap)(JNIEnv *env, jclass obj, jobject out, jlong commitPtr, jlong mailmapPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniRawHeader)(JNIEnv *env, jclass obj, jlong commitPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniTree)(JNIEnv *env, jclass obj, jobject outTreePtr, jlong commitPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Commit_jniTreeId)(JNIEnv *env, jclass obj, jlong commitPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniParentCount)(JNIEnv *env, jclass obj, jlong commitPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniParent)(JNIEnv *env, jclass obj, jobject outPtr, jlong commitPtr, jint n);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Commit_jniParentId)(JNIEnv *env, jclass obj, jlong commitPtr, jint n);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniNthGenAncestor)(JNIEnv *env, jclass obj, jobject outPtr, jlong commitPtr, jint n);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniHeaderField)(JNIEnv *env, jclass obj, jobject outBuf, jlong commitPtr, jstring field);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniExtractSignature)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jobject commitId, jstring field);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCreate)(JNIEnv *env,
                                                           jclass obj,
                                                           jobject outOid,
                                                           jlong repoPtr,
                                                           jstring updateRef,
                                                           jlong author,
                                                           jlong committer,
                                                           jstring msgEncoding,
                                                           jstring message,
                                                           jlong treePtr,
                                                           jlongArray parents);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniAmend)(JNIEnv *env, jclass obj,
                                                          jobject id,
                                                          jlong commitToAmendPtr,
                                                          jstring update_ref,
                                                          jlong authorPtr,
                                                          jlong committerPtr,
                                                          jstring message_encoding,
                                                          jstring message,
                                                          jlong treePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCreateBuffer)(JNIEnv *env, jclass obj,
                                                                 jobject out,
                                                                 jlong repoPtr,
                                                                 jlong authorPtr,
                                                                 jlong committerPtr,
                                                                 jstring message_encoding,
                                                                 jstring message,
                                                                 jlong treePtr,
                                                                 jint parentCount,
                                                                 jlongArray parents);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCreateWithSignature)(JNIEnv *env, jclass obj,
                                                                        jobject outOid,
                                                                        jlong repoPtr,
                                                                        jstring commitContent,
                                                                        jstring signature,
                                                                        jstring signatureField);
#ifdef __cplusplus
}
#endif
#endif
