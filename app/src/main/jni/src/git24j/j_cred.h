#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_CRED_H__
#define __GIT24J_CRED_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniHasUsername)(JNIEnv *env, jclass obj, jlong credPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniUserpassPlaintextNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring password);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniSshKeyNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring publickey, jstring privatekey, jstring passphrase);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniSshKeyFromAgent)(JNIEnv *env, jclass obj, jobject out, jstring username);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniDefaultNew)(JNIEnv *env, jclass obj, jobject out);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniUsernameNew)(JNIEnv *env, jclass obj, jobject cred, jstring username);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniSshKeyMemoryNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring publickey, jstring privatekey, jstring passphrase);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Cred_jniFree)(JNIEnv *env, jclass obj, jlong credPtr);
#ifdef __cplusplus
}
#endif
#endif