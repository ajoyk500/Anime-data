#include "j_common.h"
#include "j_mappers.h"
#include <assert.h>
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_SUBMODULE_H__
#define __GIT24J_SUBMODULE_H__
#ifdef __cplusplus
extern "C"
{
    #endif
    int j_git_submodule_cb(git_submodule *sm, const char *name, void *payload);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject foreachCb);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniAddFinalize)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniAddSetup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring url, jstring path, jint use_gitlink);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniAddToIndex)(JNIEnv *env, jclass obj, jlong submodulePtr, jint write_index);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Submodule_jniBranch)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniFetchRecurseSubmodules)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Submodule_jniFree)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Submodule_jniHeadId)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniIgnore)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Submodule_jniIndexId)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniInit)(JNIEnv *env, jclass obj, jlong submodulePtr, jint overwrite);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniLocation)(JNIEnv *env, jclass obj, jobject locationStatus, jlong submodulePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniLookup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Submodule_jniName)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniOpen)(JNIEnv *env, jclass obj, jobject repo, jlong submodulePtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Submodule_jniOwner)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Submodule_jniPath)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniReload)(JNIEnv *env, jclass obj, jlong submodulePtr, jint force);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniRepoInit)(JNIEnv *env, jclass obj, jobject out, jlong smPtr, jint use_gitlink);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniResolveUrl)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring url);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetBranch)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jstring branch);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetFetchRecurseSubmodules)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jint fetchRecurseSubmodules);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetIgnore)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jint ignore);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetUpdate)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jint update);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetUrl)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jstring url);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniStatus)(JNIEnv *env, jclass obj, jobject status, jlong repoPtr, jstring name, jint ignore);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSync)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdate)(JNIEnv *env, jclass obj, jlong submodulePtr, jint init, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdateInitOptions)(JNIEnv *env, jclass obj, jlong updateOptionsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsNew)(JNIEnv *env, jclass obj, jobject outOpt, jint version);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsGetCheckoutOpts)(JNIEnv *env, jclass obj, jlong updateOptionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsGetFetchOpts)(JNIEnv *env, jclass obj, jlong updateOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsGetAllowFetch)(JNIEnv *env, jclass obj, jlong updateOptionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsSetAllowFetch)(JNIEnv *env, jclass obj, jlong updateOptionsPtr, jint allowFetch);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdateStrategy)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Submodule_jniUrl)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Submodule_jniWdId)(JNIEnv *env, jclass obj, jlong submodulePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniClone)(JNIEnv *env, jclass obj, jobject out, jlong submodulePtr, jlong optsPtr);
    #ifdef __cplusplus
}
#endif
#endif