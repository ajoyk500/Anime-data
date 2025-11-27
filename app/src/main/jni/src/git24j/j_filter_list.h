#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_FILTER_LIST_H__
#define __GIT24J_FILTER_LIST_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniLoad)(JNIEnv *env, jclass obj, jobject filters, jlong repoPtr, jlong blobPtr, jstring path, jint mode, jint flags);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniContains)(JNIEnv *env, jclass obj, jlong filtersPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniApplyToData)(JNIEnv *env, jclass obj, jobject out, jlong filtersPtr, jstring in);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniApplyToFile)(JNIEnv *env, jclass obj, jobject out, jlong filtersPtr, jlong repoPtr, jstring path);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniApplyToBlob)(JNIEnv *env, jclass obj, jobject out, jlong filtersPtr, jlong blobPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniStreamData)(JNIEnv *env, jclass obj, jlong filtersPtr, jstring data, jlong targetPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniStreamFile)(JNIEnv *env, jclass obj, jlong filtersPtr, jlong repoPtr, jstring path, jlong targetPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniStreamBlob)(JNIEnv *env, jclass obj, jlong filtersPtr, jlong blobPtr, jlong targetPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(FilterList_jniFree)(JNIEnv *env, jclass obj, jlong filtersPtr);
#ifdef __cplusplus
}
#endif
#endif