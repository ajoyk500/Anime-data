#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_TAG_H__
#define __GIT24J_TAG_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniTarget)(JNIEnv *env, jclass obj, jobject outTargetPtr, jlong tagPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Tag_jniTargetId)(JNIEnv *env, jclass obj, jlong tagPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniTargetType)(JNIEnv *env, jclass obj, jlong tagPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Tag_jniName)(JNIEnv *env, jclass obj, jlong tagPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tag_jniTagger)(JNIEnv *env, jclass obj, jlong tagPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Tag_jniMessage)(JNIEnv *env, jclass obj, jlong tagPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniCreate)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring tag_name, jlong targetPtr, jlong taggerPtr, jstring message, jint force);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniAnnotationCreate)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring tag_name, jlong targetPtr, jlong taggerPtr, jstring message);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniCreateFromBuffer)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring buffer, jint force);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniCreateLightWeight)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring tagName, jlong targetPtr, jint force);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniDelete)(JNIEnv *env, jclass obj, jlong repoPtr, jstring tagName);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniList)(JNIEnv *env, jclass obj, jobject tagNames, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniListMatch)(JNIEnv *env, jclass obj, jobject tagNames, jstring pattern, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject foreachCb);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniPeel)(JNIEnv *env, jclass obj, jobject outTarget, jlong tagPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniDup)(JNIEnv *env, jclass obj, jobject outTag, jlong sourcePtr);
#ifdef __cplusplus
}
#endif
#endif