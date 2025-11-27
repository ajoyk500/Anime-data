#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_MERGE_H__
#define __GIT24J_MERGE_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInitInput)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputGetVersion)(JNIEnv *env, jclass obj, jlong fileInputPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileInputGetPtr)(JNIEnv *env, jclass obj, jlong fileInputPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputGetSize)(JNIEnv *env, jclass obj, jlong fileInputPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileInputGetPath)(JNIEnv *env, jclass obj, jlong fileInputPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputGetMode)(JNIEnv *env, jclass obj, jlong fileInputPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetVersion)(JNIEnv *env, jclass obj, jlong fileInputPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetPtr)(JNIEnv *env, jclass obj, jlong fileInputPtr, jstring ptr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetSize)(JNIEnv *env, jclass obj, jlong fileInputPtr, jint size);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetPath)(JNIEnv *env, jclass obj, jlong fileInputPtr, jstring path);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetMode)(JNIEnv *env, jclass obj, jlong fileInputPtr, jint mode);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetVersion)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetAncestorLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetOurLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetTheirLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetFavor)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetFlags)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetMarkerSize)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetVersion)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetAncestorLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jstring ancestorLabel);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetOurLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jstring ourLabel);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetTheirLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jstring theirLabel);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetFavor)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jint favor);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetFlags)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jint flags);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetMarkerSize)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jint markerSize);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsFree)(JNIEnv *env, jclass obj, jlong opts);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetRenameThreshold)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetTargetLimit)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Merge_jniOptionsGetMetric)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetRecursionLimit)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniOptionsGetDefaultDriver)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetFileFavor)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetFileFlags)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint flags);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetRenameThreshold)(JNIEnv *env, jclass obj, jlong optionsPtr, jint renameThreshold);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetTargetLimit)(JNIEnv *env, jclass obj, jlong optionsPtr, jint targetLimit);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetMetric)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong metric);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetRecursionLimit)(JNIEnv *env, jclass obj, jlong optionsPtr, jint recursionLimit);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetDefaultDriver)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring defaultDriver);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetFileFavor)(JNIEnv *env, jclass obj, jlong optionsPtr, jint fileFavor);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetFileFlags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint fileFlags);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBase)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jobject one, jobject two);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBases)(JNIEnv *env, jclass obj, jobject outOids, jlong repoPtr, jobject one, jobject two);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniTrees)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ancestorTreePtr, jlong ourTreePtr, jlong theirTreePtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniCommits)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ourCommitPtr, jlong theirCommitPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniAnalysis)(JNIEnv *env, jclass obj, jobject analysisOut, jobject preferenceOut, jlong repoPtr, jlongArray theirHeads);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniAnalysisForRef)(JNIEnv *env, jclass obj, jobject analysisOut, jobject preferenceOut, jlong repoPtr, jlong ourRefPtr, jlongArray theirHeads);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBaseMany)(JNIEnv *env, jclass obj, jobject outOid, jlong repoPtr, jobjectArray inputArray);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBasesMany)(JNIEnv *env, jclass obj, jobject outOids, jlong repoPtr, jobjectArray inputArray);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBaseOctopus)(JNIEnv *env, jclass obj, jobject outOid, jlong repoPtr, jobjectArray inputArray);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFile)(JNIEnv *env, jclass obj, jobject out, jlong ancestorPtr, jlong oursPtr, jlong theirsPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileFromIndex)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ancestorPtr, jlong oursPtr, jlong theirsPtr, jlong optsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileResultFree)(JNIEnv *env, jclass obj, jlong resultPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Merge_jniFileResultNew)(JNIEnv *env, jclass obj);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileResultGetAutomergeable)(JNIEnv *env, jclass obj, jlong fileResultPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileResultGetPath)(JNIEnv *env, jclass obj, jlong fileResultPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileResultGetMode)(JNIEnv *env, jclass obj, jlong fileResultPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileResultGetPtr)(JNIEnv *env, jclass obj, jlong fileResultPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileResultGetLen)(JNIEnv *env, jclass obj, jlong fileResultPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniMerge)(JNIEnv *env, jclass obj, jlong repoPtr, jlongArray theirHeads, jlong mergeOptsPtr, jlong checkoutOpts);
#ifdef __cplusplus
}
#endif
#endif