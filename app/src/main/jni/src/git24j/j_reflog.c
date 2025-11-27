#include "j_reflog.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>
extern j_constants_t *jniConstants;
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniRead)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name)
{
    git_reflog *c_out;
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_reflog_read(&c_out, (git_repository *)repoPtr, c_name);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_name);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniWrite)(JNIEnv *env, jclass obj, jlong reflogPtr)
{
    int r = git_reflog_write((git_reflog *)reflogPtr);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniAppend)(JNIEnv *env, jclass obj, jlong reflogPtr, jobject id, jlong committerPtr, jstring msg)
{
    git_oid c_id;
    j_git_oid_from_java(env, id, &c_id);
    char *c_msg = j_copy_of_jstring(env, msg, true);
    int r = git_reflog_append((git_reflog *)reflogPtr, &c_id, (git_signature *)committerPtr, c_msg);
    free(c_msg);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniRename)(JNIEnv *env, jclass obj, jlong repoPtr, jstring old_name, jstring name)
{
    char *c_old_name = j_copy_of_jstring(env, old_name, true);
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_reflog_rename((git_repository *)repoPtr, c_old_name, c_name);
    free(c_old_name);
    free(c_name);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniDelete)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_reflog_delete((git_repository *)repoPtr, c_name);
    free(c_name);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniEntrycount)(JNIEnv *env, jclass obj, jlong reflogPtr)
{
    size_t r = git_reflog_entrycount((git_reflog *)reflogPtr);
    return r;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Reflog_jniEntryByindex)(JNIEnv *env, jclass obj, jlong reflogPtr, jint idx)
{
    const git_reflog_entry *r = git_reflog_entry_byindex((git_reflog *)reflogPtr, idx);
    return (jlong)r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniDrop)(JNIEnv *env, jclass obj, jlong reflogPtr, jint idx, jint rewrite_previous_entry)
{
    int r = git_reflog_drop((git_reflog *)reflogPtr, idx, rewrite_previous_entry);
    return r;
}
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Reflog_jniEntryIdOld)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    const git_oid *c_oid = git_reflog_entry_id_old((git_reflog_entry *)entryPtr);
    return j_git_oid_to_bytearray(env, c_oid);
}
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Reflog_jniEntryIdNew)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    const git_oid *c_oid = git_reflog_entry_id_new((git_reflog_entry *)entryPtr);
    return j_git_oid_to_bytearray(env, c_oid);
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Reflog_jniEntryCommitter)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    const git_signature *r = git_reflog_entry_committer((git_reflog_entry *)entryPtr);
    return (jlong)r;
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Reflog_jniEntryMessage)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    const char *r = git_reflog_entry_message((git_reflog_entry *)entryPtr);
    return (*env)->NewStringUTF(env, r);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Reflog_jniFree)(JNIEnv *env, jclass obj, jlong reflogPtr)
{
    git_reflog_free((git_reflog *)reflogPtr);
}
