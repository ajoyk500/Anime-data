#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_CREDENTIAL_H__
#define __GIT24J_CREDENTIAL_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT void JNICALL J_MAKE_METHOD(Credential_jniFree)(JNIEnv *env, jclass obj, jlong credPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniHasUsername)(JNIEnv *env, jclass obj, jlong credPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Credential_jniGetUsername)(JNIEnv *env, jclass obj, jlong credPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniUserpassPlaintextNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring password);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniDefaultNew)(JNIEnv *env, jclass obj, jobject out);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniUsernameNew)(JNIEnv *env, jclass obj, jobject out, jstring username);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniSshKeyNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring publickey, jstring privatekey, jstring passphrase);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniSshKeyMemoryNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring publickey, jstring privatekey, jstring passphrase);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniSshKeyFromAgent)(JNIEnv *env, jclass obj, jobject out, jstring username);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniUserpass)(JNIEnv *env, jclass obj, jobject out, jstring url, jstring user_from_url, jint allowedTypes, jlong payload);
#ifdef __cplusplus
}
#endif
#endif