#include "j_revert.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>
extern j_constants_t *jniConstants;
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revert_jniCommit)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong revertCommitPtr, jlong ourCommitPtr, jint mainline, jlong mergeOptionsPtr)
{
    git_index *c_out;
    int r = git_revert_commit(&c_out, (git_repository *)repoPtr, (git_commit *)revertCommitPtr, (git_commit *)ourCommitPtr, mainline, (git_merge_options *)mergeOptionsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revert_jniRevert)(JNIEnv *env, jclass obj, jlong repoPtr, jlong commitPtr, jlong givenOptsPtr)
{
    int r = git_revert((git_repository *)repoPtr, (git_commit *)commitPtr, (git_revert_options *)givenOptsPtr);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revert_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version)
{
    git_revert_options *opts = (git_revert_options *)malloc(sizeof(git_revert_options));
    int r = git_revert_options_init(opts, version);
    (*env)->CallVoidMethod(env, outPtr, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Revert_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    git_revert_options *opts = (git_revert_options *)optionsPtr;
    free(opts);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revert_jniOptionsGetMainline)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_revert_options *)optionsPtr)->mainline;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Revert_jniOptionsGetMergeOpts)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return (jlong) & (((git_revert_options *)optionsPtr)->merge_opts);
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Revert_jniOptionsGetCheckoutOpts)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    git_checkout_options *r = &((git_revert_options *)optionsPtr)->checkout_opts;
    return (jlong)r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Revert_jniOptionsSetMainline)(JNIEnv *env, jclass obj, jlong optionsPtr, jint mainline)
{
    ((git_revert_options *)optionsPtr)->mainline = (unsigned int)mainline;
}
