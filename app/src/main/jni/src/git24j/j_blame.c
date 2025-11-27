#include "j_blame.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>
extern j_constants_t *jniConstants;
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version)
{
    git_blame_options *opts = (git_blame_options *)malloc(sizeof(git_blame_options));
    int initErr = git_blame_init_options(opts, (unsigned int)version);
    (*env)->CallVoidMethod(env, outPtr, jniConstants->midAtomicLongSet, (jlong)opts);
    return initErr;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniGetHunkCount)(JNIEnv *env, jclass obj, jlong blamePtr)
{
    size_t r = git_blame_get_hunk_count((git_blame *)blamePtr);
    return r;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniGetHunkByindex)(JNIEnv *env, jclass obj, jlong blamePtr, jint index)
{
    const git_blame_hunk *r = git_blame_get_hunk_byindex((git_blame *)blamePtr, index);
    return (jlong)r;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniGetHunkByline)(JNIEnv *env, jclass obj, jlong blamePtr, jint lineno)
{
    const git_blame_hunk *r = git_blame_get_hunk_byline((git_blame *)blamePtr, lineno);
    return (jlong)r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniFile)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring path, jlong optionsPtr)
{
    git_blame *c_out = NULL;
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_blame_file(&c_out, (git_repository *)repoPtr, c_path, (git_blame_options *)optionsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_path);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniBuffer)(JNIEnv *env, jclass obj, jobject out, jlong referencePtr, jstring buffer, jint bufferLen)
{
    git_blame *c_out = NULL;
    char *c_buffer = j_copy_of_jstring(env, buffer, true);
    int r = git_blame_buffer(&c_out, (git_blame *)referencePtr, c_buffer, bufferLen);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_buffer);
    return r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Blame_jniFree)(JNIEnv *env, jclass obj, jlong blamePtr)
{
    git_blame_free((git_blame *)blamePtr);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniHunkGetLinesInHunk)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return ((git_blame_hunk *)hunkPtr)->lines_in_hunk;
}
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Blame_jniHunkGetFinalCommitId)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return j_git_oid_to_bytearray(env, &((git_blame_hunk *)hunkPtr)->final_commit_id);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniHunkGetFinalStartLineNumber)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return ((git_blame_hunk *)hunkPtr)->final_start_line_number;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniHunkGetFinalSignature)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return (jlong)((git_blame_hunk *)hunkPtr)->final_signature;
}
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigCommitId)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return j_git_oid_to_bytearray(env, &((git_blame_hunk *)hunkPtr)->orig_commit_id);
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigPath)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return (*env)->NewStringUTF(env, ((git_blame_hunk *)hunkPtr)->orig_path);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigStartLineNumber)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return ((git_blame_hunk *)hunkPtr)->orig_start_line_number;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigSignature)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return (jlong)((git_blame_hunk *)hunkPtr)->orig_signature;
}
JNIEXPORT jchar JNICALL J_MAKE_METHOD(Blame_jniHunkGetBoundary)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return ((git_blame_hunk *)hunkPtr)->boundary;
}
