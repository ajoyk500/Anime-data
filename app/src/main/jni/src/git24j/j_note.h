#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_NOTE_H__
#define __GIT24J_NOTE_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jstring notesRef, jobject foreachCb);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniIteratorNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring notes_ref);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCommitIteratorNew)(JNIEnv *env, jclass obj, jobject out, jlong notesCommitPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Note_jniIteratorFree)(JNIEnv *env, jclass obj, jlong itPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniNext)(JNIEnv *env, jclass obj, jobject note_id, jobject annotated_id, jlong itPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniRead)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring notes_ref, jobject oid);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCommitRead)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong notesCommitPtr, jobject oid);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Note_jniAuthor)(JNIEnv *env, jclass obj, jlong notePtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Note_jniCommitter)(JNIEnv *env, jclass obj, jlong notePtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Note_jniMessage)(JNIEnv *env, jclass obj, jlong notePtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Note_jniId)(JNIEnv *env, jclass obj, jlong notePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCreate)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring notes_ref, jlong authorPtr, jlong committerPtr, jobject oid, jstring note, jint force);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCommitCreate)(JNIEnv *env, jclass obj, jobject notes_commit_out, jobject notes_blob_out, jlong repoPtr, jlong parentPtr, jlong authorPtr, jlong committerPtr, jobject oid, jstring note, jint allow_note_overwrite);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniRemove)(JNIEnv *env, jclass obj, jlong repoPtr, jstring notes_ref, jlong authorPtr, jlong committerPtr, jobject oid);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCommitRemove)(JNIEnv *env, jclass obj, jobject notes_commit_out, jlong repoPtr, jlong notesCommitPtr, jlong authorPtr, jlong committerPtr, jobject oid);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Note_jniFree)(JNIEnv *env, jclass obj, jlong notePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniDefaultRef)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniForeachCb)(JNIEnv *env, jclass obj, jobject blobId, jobject annotatedObjectId);
#ifdef __cplusplus
}
#endif
#endif