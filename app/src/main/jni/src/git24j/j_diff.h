#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_DIFF_H__
#define __GIT24J_DIFF_H__
#ifdef __cplusplus
extern "C"
{
#endif
    typedef struct
    {
        JNIEnv *env;
        jobject fileCb;
        jobject binaryCb;
        jobject hunkCb;
        jobject lineCb;
    } j_diff_callback_payload;
    int j_git_diff_line_cb(const git_diff_delta *delta, const git_diff_hunk *hunk, const git_diff_line *line, void *payload);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniInitOptions)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniFreeOptions)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFindInitOptions)(JNIEnv *env, jclass obj, jobject outFindOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniFreeFindOptions)(JNIEnv *env, jclass obj, jlong findOptsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniFree)(JNIEnv *env, jclass obj, jlong diffPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniTreeToTree)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldTreePtr, jlong newTreePtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniTreeToIndex)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldTreePtr, jlong indexPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniIndexToWorkdir)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong indexPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniTreeToWorkdir)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldTreePtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniTreeToWorkdirWithIndex)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldTreePtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniIndexToIndex)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldIndexPtr, jlong newIndexPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniMerge)(JNIEnv *env, jclass obj, jlong ontoPtr, jlong fromPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFindSimilar)(JNIEnv *env, jclass obj, jlong diffPtr, jlong optionsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniNumDeltas)(JNIEnv *env, jclass obj, jlong diffPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniNumDeltasOfType)(JNIEnv *env, jclass obj, jlong diffPtr, jint type);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Diff_jniGetDelta)(JNIEnv *env, jclass obj, jlong diffPtr, jint idx);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniIsSortedIcase)(JNIEnv *env, jclass obj, jlong diffPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniForeach)(JNIEnv *env, jclass obj, jlong diffPtr, jobject fileCb, jobject binaryCb, jobject hunkCb, jobject lineCb);
    JNIEXPORT jchar JNICALL J_MAKE_METHOD(Diff_jniStatusChar)(JNIEnv *env, jclass obj, jint status);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniPrint)(JNIEnv *env, jclass obj, jlong diffPtr, jint format, jobject printCb);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniToBuf)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jint format);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBlobs)(JNIEnv *env, jclass obj, jlong oldBlobPtr, jstring old_as_path, jlong newBlobPtr, jstring new_as_path, jlong optionsPtr, jobject fileCb, jobject binaryCb, jobject hunkCb, jobject lineCb);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBlobToBuffer)(JNIEnv *env, jclass obj, jlong oldBlobPtr, jstring old_as_path, jstring buffer, jint bufferLen, jstring buffer_as_path, jlong optionsPtr, jobject fileCb, jobject binaryCb, jobject hunkCb, jobject lineCb);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBuffers)(JNIEnv *env, jclass obj, jbyteArray oldBuffer, jint oldLen, jstring old_as_path, jbyteArray newBuffer, jint newLen, jstring new_as_path, jlong optionsPtr, jobject fileCb, jobject binaryCb, jobject hunkCb, jobject lineCb);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFromBuffer)(JNIEnv *env, jclass obj, jobject out, jstring content, jint contentLen);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniGetStats)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniStatsFilesChanged)(JNIEnv *env, jclass obj, jlong statsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniStatsInsertions)(JNIEnv *env, jclass obj, jlong statsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniStatsDeletions)(JNIEnv *env, jclass obj, jlong statsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniStatsToBuf)(JNIEnv *env, jclass obj, jobject out, jlong statsPtr, jint format, jint width);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniStatsFree)(JNIEnv *env, jclass obj, jlong statsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFormatEmail)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniCommitAsEmail)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong commitPtr, jint patchNo, jint totalPatches, jint flags, jlong diffOptsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFormatEmailInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFormatEmailNewOptions)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniFormatEmailOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniPatchidInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniPatchidOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniPatchidOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniPatchid)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jlong optsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniDeltaGetStatus)(JNIEnv *env, jclass obj, jlong deltaPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniDeltaGetFlags)(JNIEnv *env, jclass obj, jlong deltaPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniDeltaGetSimilarity)(JNIEnv *env, jclass obj, jlong deltaPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniDeltaGetNfiles)(JNIEnv *env, jclass obj, jlong deltaPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Diff_jniDeltaGetOldFile)(JNIEnv *env, jclass obj, jlong deltaPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Diff_jniDeltaGetNewFile)(JNIEnv *env, jclass obj, jlong deltaPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Diff_jniFileGetId)(JNIEnv *env, jclass obj, jlong filePtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Diff_jniFileGetPath)(JNIEnv *env, jclass obj, jlong filePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFileGetSize)(JNIEnv *env, jclass obj, jlong filePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFileGetFlags)(JNIEnv *env, jclass obj, jlong filePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFileGetMode)(JNIEnv *env, jclass obj, jlong filePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFileGetIdAbbrev)(JNIEnv *env, jclass obj, jlong filePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniHunkGetOldStart)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniHunkGetOldLines)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniHunkGetNewStart)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniHunkGetNewLines)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniHunkGetHeaderLen)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Diff_jniHunkGetHeader)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Diff_jniHunkGetHeaderBytes)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jchar JNICALL J_MAKE_METHOD(Diff_jniLineGetOrigin)(JNIEnv *env, jclass obj, jlong linePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniLineGetOldLineno)(JNIEnv *env, jclass obj, jlong linePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniLineGetNewLineno)(JNIEnv *env, jclass obj, jlong linePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniLineGetNumLines)(JNIEnv *env, jclass obj, jlong linePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniLineGetContentLen)(JNIEnv *env, jclass obj, jlong linePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniLineGetContentOffset)(JNIEnv *env, jclass obj, jlong linePtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Diff_jniLineGetContent)(JNIEnv *env, jclass obj, jlong linePtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Diff_jniLineGetContentBytes)(JNIEnv *env, jclass obj, jlong linePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBinaryGetContainsData)(JNIEnv *env, jclass obj, jlong binaryPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Diff_jniBinaryGetOldFile)(JNIEnv *env, jclass obj, jlong binaryPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Diff_jniBinaryGetNewFile)(JNIEnv *env, jclass obj, jlong binaryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBinaryFileGetType)(JNIEnv *env, jclass obj, jlong binaryFilePtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Diff_jniBinaryFileGetData)(JNIEnv *env, jclass obj, jlong binaryFilePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBinaryFileGetDatalen)(JNIEnv *env, jclass obj, jlong binaryFilePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBinaryFileGetInflatedlen)(JNIEnv *env, jclass obj, jlong binaryFilePtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniDiffOptionsSetPathSpec)(JNIEnv *env, jclass obj, jlong diffOptionsPtr, jobjectArray pathSpecJArr);
    JNIEXPORT jobjectArray JNICALL J_MAKE_METHOD(Diff_jniDiffOptionsGetPathSpec)(JNIEnv *env, jclass obj, jlong diffOptionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniDiffOptionsSetFlags)(JNIEnv *env, jclass obj, jlong diffOptionsPtr, jint flags);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniDiffOptionsGetFlags)(JNIEnv *env, jclass obj, jlong diffOptionsPtr);
#ifdef __cplusplus
}
#endif
#endif
