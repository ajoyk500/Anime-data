#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_REFERENCE_H__
#define __GIT24J_REFERENCE_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniLookup)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNameToId)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniDwim)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring shorthand);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniSymbolicCreateMatching)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name, jstring target, jint force, jstring currentValue, jstring logMessage);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniSymbolicCreate)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name, jstring target, jint force, jstring logMessage);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniCreate)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name, jobject oid, jint force, jstring logMessage);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniCreateMatching)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name, jobject oid, jint force, jobject currentId, jstring logMessage);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Reference_jniTarget)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Reference_jniTargetPeel)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Reference_jniSymbolicTarget)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniType)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Reference_jniName)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniResolve)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Reference_jniOwner)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniSymbolicSetTarget)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr, jstring target, jstring logMessage);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniSetTarget)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr, jobject oid, jstring logMessage);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniRename)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr, jstring newName, jint force, jstring logMessage);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniDelete)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniRemove)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniList)(JNIEnv *env, jclass obj, jobject strList, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject callback);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniForeachName)(JNIEnv *env, jclass obj, jlong repoPtr, jobject consumer);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniDup)(JNIEnv *env, jclass obj, jobject outDest, jlong sourcePtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Reference_jniFree)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniCmp)(JNIEnv *env, jclass obj, jlong ref1Ptr, jlong ref2Ptr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIteratorNew)(JNIEnv *env, jclass obj, jobject outIter, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIteratorGlobNew)(JNIEnv *env, jclass obj, jobject outIter, jlong repoPtr, jstring glob);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNext)(JNIEnv *env, jclass obj, jobject outRef, jlong iterPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNextName)(JNIEnv *env, jclass obj, jobject outName, jlong iterPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Reference_jniIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniForeachGlob)(JNIEnv *env, jclass obj, jlong repoPtr, jstring glob, jobject callback);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniHasLog)(JNIEnv *env, jclass obj, jlong repoPtr, jstring refname);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniEnsureLog)(JNIEnv *env, jclass obj, jlong repoPtr, jstring refname);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsBranch)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsRemote)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsTag)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsNote)(JNIEnv *env, jclass obj, jlong refPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNormalizeName)(JNIEnv *env, jclass obj, jobject bufferOut, jstring name, jint flags);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniPeel)(JNIEnv *env, jclass obj, jobject outObj, jlong refPtr, jint objType);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsValidName)(JNIEnv *env, jclass obj, jstring refname);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Reference_jniShorthand)(JNIEnv *env, jclass obj, jlong refPtr);
#ifdef __cplusplus
}
#endif
#endif
