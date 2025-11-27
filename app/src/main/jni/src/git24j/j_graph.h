#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_GRAPH_H__
#define __GIT24J_GRAPH_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Graph_jniAheadBehind)(JNIEnv *env, jclass obj, jobject ahead, jobject behind, jlong repoPtr, jobject local, jobject upstream);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Graph_jniDescendantOf)(JNIEnv *env, jclass obj, jlong repoPtr, jobject commit, jobject ancestor);
#ifdef __cplusplus
}
#endif
#endif