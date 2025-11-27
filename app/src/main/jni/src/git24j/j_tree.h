#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_TREE_H__
#define __GIT24J_TREE_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniWalk)(JNIEnv *env, jclass obj, jlong treePtr, jint mode, jobject callback);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniLookup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jobject id);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniFree)(JNIEnv *env, jclass obj, jlong treePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntrycount)(JNIEnv *env, jclass obj, jlong treePtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniEntryByname)(JNIEnv *env, jclass obj, jlong treePtr, jstring filename);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniEntryByindex)(JNIEnv *env, jclass obj, jlong treePtr, jint idx);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniEntryByid)(JNIEnv *env, jclass obj, jlong treePtr, jobject id);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryBypath)(JNIEnv *env, jclass obj, jobject out, jlong rootPtr, jstring path);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryDup)(JNIEnv *env, jclass obj, jobject dest, jlong sourcePtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniEntryFree)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Tree_jniEntryName)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Tree_jniEntryId)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryType)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryFilemode)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryFilemodeRaw)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryCmp)(JNIEnv *env, jclass obj, jlong e1Ptr, jlong e2Ptr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryToObject)(JNIEnv *env, jclass obj, jobject objectOut, jlong repoPtr, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniDup)(JNIEnv *env, jclass obj, jobject out, jlong sourcePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniCreateUpdated)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong baselinePtr, jlongArray updates);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniUpdateNew)(JNIEnv *env, jclass obj, jint updateType, jobject oid, jint filemodeType, jstring path);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniUpdateFree)(JNIEnv *env, jclass obj, jlong updatePtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniBuilderFilter)(JNIEnv *env, jclass obj, jlong bldPtr, jobject callback);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong sourcePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderClear)(JNIEnv *env, jclass obj, jlong bldPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderEntrycount)(JNIEnv *env, jclass obj, jlong bldPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniBuilderFree)(JNIEnv *env, jclass obj, jlong bldPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniBuilderGet)(JNIEnv *env, jclass obj, jlong bldPtr, jstring filename);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderInsert)(JNIEnv *env, jclass obj, jobject out, jlong bldPtr, jstring filename, jobject id, jint filemode);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderRemove)(JNIEnv *env, jclass obj, jlong bldPtr, jstring filename);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderWrite)(JNIEnv *env, jclass obj, jobject id, jlong bldPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderWriteWithBuffer)(JNIEnv *env, jclass obj, jobject oid, jlong bldPtr, jobject tree);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderFilterCb)(JNIEnv *env, jclass obj, jlong entryPtr);
#ifdef __cplusplus
}
#endif
#endif