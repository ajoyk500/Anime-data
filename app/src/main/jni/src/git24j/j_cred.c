#include "j_cred.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>
extern j_constants_t *jniConstants;
JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniHasUsername)(JNIEnv *env, jclass obj, jlong credPtr)
{
    int r = git_cred_has_username((git_cred *)credPtr);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniUserpassPlaintextNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring password)
{
    git_cred *c_out = 0;
    char *c_username = j_copy_of_jstring(env, username, true);
    char *c_password = j_copy_of_jstring(env, password, true);
    int r = git_cred_userpass_plaintext_new(&c_out, c_username, c_password);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_username);
    free(c_password);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniSshKeyNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring publickey, jstring privatekey, jstring passphrase)
{
    git_cred *c_out = 0;
    char *c_username = j_copy_of_jstring(env, username, true);
    char *c_publickey = j_copy_of_jstring(env, publickey, true);
    char *c_privatekey = j_copy_of_jstring(env, privatekey, true);
    char *c_passphrase = j_copy_of_jstring(env, passphrase, true);
    int r = git_cred_ssh_key_new(&c_out, c_username, c_publickey, c_privatekey, c_passphrase);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_username);
    free(c_publickey);
    free(c_privatekey);
    free(c_passphrase);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniSshKeyFromAgent)(JNIEnv *env, jclass obj, jobject out, jstring username)
{
    git_cred *c_out = 0;
    char *c_username = j_copy_of_jstring(env, username, true);
    int r = git_cred_ssh_key_from_agent(&c_out, c_username);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_username);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniDefaultNew)(JNIEnv *env, jclass obj, jobject out)
{
    git_cred *c_out = 0;
    int r = git_cred_default_new(&c_out);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniUsernameNew)(JNIEnv *env, jclass obj, jobject cred, jstring username)
{
    git_cred *c_cred = 0;
    char *c_username = j_copy_of_jstring(env, username, true);
    int r = git_cred_username_new(&c_cred, c_username);
    (*env)->CallVoidMethod(env, cred, jniConstants->midAtomicLongSet, (jlong)c_cred);
    free(c_username);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniSshKeyMemoryNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring publickey, jstring privatekey, jstring passphrase)
{
    git_cred *c_out = 0;
    char *c_username = j_copy_of_jstring(env, username, true);
    char *c_publickey = j_copy_of_jstring(env, publickey, true);
    char *c_privatekey = j_copy_of_jstring(env, privatekey, true);
    char *c_passphrase = j_copy_of_jstring(env, passphrase, true);
    int r = git_cred_ssh_key_memory_new(&c_out, c_username, c_publickey, c_privatekey, c_passphrase);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_username);
    free(c_publickey);
    free(c_privatekey);
    free(c_passphrase);
    return r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Cred_jniFree)(JNIEnv *env, jclass obj, jlong credPtr)
{
    git_cred_free((git_cred *)credPtr);
}
