#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_CERT_H__
#define __GIT24J_CERT_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetParent)(JNIEnv *env, jclass obj, jlong hostkeyPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetType)(JNIEnv *env, jclass obj, jlong hostkeyPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetHashMd5)(JNIEnv *env, jclass obj, jlong hostkeyPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetHashSha1)(JNIEnv *env, jclass obj, jlong hostkeyPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetHashSha256)(JNIEnv *env, jclass obj, jlong hostkeyPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cert_jniHostkeyCreateEmptyForTesting)(JNIEnv *env, jclass obj);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cert_jniX509GetParent)(JNIEnv *env, jclass obj, jlong x509Ptr);
#ifdef __cplusplus
}
#endif
#endif