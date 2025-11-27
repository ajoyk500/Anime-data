#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_REFLOG_H__
#define __GIT24J_REFLOG_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniRead)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniWrite)(JNIEnv *env, jclass obj, jlong reflogPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniAppend)(JNIEnv *env, jclass obj, jlong reflogPtr, jobject id, jlong committerPtr, jstring msg);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniRename)(JNIEnv *env, jclass obj, jlong repoPtr, jstring old_name, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniDelete)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniEntrycount)(JNIEnv *env, jclass obj, jlong reflogPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Reflog_jniEntryByindex)(JNIEnv *env, jclass obj, jlong reflogPtr, jint idx);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniDrop)(JNIEnv *env, jclass obj, jlong reflogPtr, jint idx, jint rewrite_previous_entry);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Reflog_jniEntryIdOld)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Reflog_jniEntryIdNew)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Reflog_jniEntryCommitter)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Reflog_jniEntryMessage)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Reflog_jniFree)(JNIEnv *env, jclass obj, jlong reflogPtr);
#ifdef __cplusplus
}
#endif
#endif