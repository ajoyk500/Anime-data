#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_INDEX_H__
#define __GIT24J_INDEX_H__
#ifdef __cplusplus
extern "C"
{
#endif
    int standard_matched_cb(const char *path, const char *matched_pathspec, void *payload);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniOpen)(JNIEnv *env, jclass obj, jobject outIndexPtr, jstring indexPath);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniFree)(JNIEnv *env, jclass obj, jlong index);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniOwner)(JNIEnv *env, jclass obj, jlong indexPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniCaps)(JNIEnv *env, jclass obj, jlong idxPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniSetCaps)(JNIEnv *env, jclass obj, jlong index, jint caps);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniVersion)(JNIEnv *env, jclass obj, jlong idxPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniSetVersion)(JNIEnv *env, jclass obj, jlong idxPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRead)(JNIEnv *env, jclass obj, jlong indexPtr, jint force);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWrite)(JNIEnv *env, jclass obj, jlong index);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Index_jniPath)(JNIEnv *env, jclass obj, jlong idxPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Index_jniChecksum)(JNIEnv *env, jclass obj, jlong indexPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniReadTree)(JNIEnv *env, jclass obj, jlong indexPtr, jlong treePtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWriteTree)(JNIEnv *env, jclass obj, jobject outOid, jlong indexPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWriteTreeTo)(JNIEnv *env, jclass obj, jobject outOid, jlong indexPtr, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryCount)(JNIEnv *env, jclass obj, jlong indexPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniClear)(JNIEnv *env, jclass obj, jlong indexPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniGetByIndex)(JNIEnv *env, jclass obj, jlong indexPtr, jint n);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniGetByPath)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path, jint stage);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRemove)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path, jint stage);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRemoveDirectory)(JNIEnv *env, jclass obj, jlong indexPtr, jstring dir, jint stage);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAdd)(JNIEnv *env, jclass obj, jlong index, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryStage)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryIsConflict)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniIteratorNew)(JNIEnv *env, jclass obj, jobject outIterPtr, jlong indexptr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniIteratorNext)(JNIEnv *env, jclass obj, jobject outEntryPtr, jlong iterPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddByPath)(JNIEnv *env, jclass obj, jlong index, jstring path);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddFromBuffer)(JNIEnv *env, jclass obj, jlong indexPtr, jlong entryPtr, jbyteArray buffer);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRemoveByPath)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddAll)(JNIEnv *env, jclass obj, jlong index, jobjectArray pathspec, jint flags, jobject biConsumer);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniUpdateAll)(JNIEnv *env, jclass obj, jlong index, jobjectArray pathspec, jobject biConsumer);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniFind)(JNIEnv *env, jclass obj, jobject outPos, jlong indexPtr, jstring path);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniFindPrefix)(JNIEnv *env, jclass obj, jobject outPos, jlong indexPtr, jstring prefix);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictAdd)(JNIEnv *env, jclass obj, jlong indexPtr, jlong ancestorEntryPtr, jlong outEntryPtr, jlong theirEntryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictGet)(JNIEnv *env, jclass obj, jobject ancestorOut, jobject ourOut, jobject theirOut, jlong indexPtr, jstring path);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictRemove)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictCleanup)(JNIEnv *env, jclass obj, jlong indexPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniHasConflicts)(JNIEnv *env, jclass obj, jlong indexPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictIteratorNew)(JNIEnv *env, jclass obj, jobject outIterPtr, jlong indexPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictNext)(JNIEnv *env, jclass obj, jobject ancestorOut, jobject ourOut, jobject theirOut, jlong iterPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniConflictIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryNew)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntryFree)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryGetCtimeSeconds)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryGetCtimeNanoseconds)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryGetMtimeSeconds)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryGetMtimeNanoseconds)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetDev)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetIno)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetMode)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetUid)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetGid)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetFileSize)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Index_jniEntryGetId)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetFlags)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetFlagsExtended)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Index_jniEntryGetPath)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetCtimeSeconds)(JNIEnv *env, jclass obj, jlong entryPtr, jlong ctimeSeconds);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetCtimeNanoseconds)(JNIEnv *env, jclass obj, jlong entryPtr, jlong ctimeNanoseconds);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetMtimeSeconds)(JNIEnv *env, jclass obj, jlong entryPtr, jlong mtimeSeconds);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetMtimeNanoseconds)(JNIEnv *env, jclass obj, jlong entryPtr, jlong mtimeNanoseconds);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetDev)(JNIEnv *env, jclass obj, jlong entryPtr, jint dev);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetIno)(JNIEnv *env, jclass obj, jlong entryPtr, jint ino);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetMode)(JNIEnv *env, jclass obj, jlong entryPtr, jint mode);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetUid)(JNIEnv *env, jclass obj, jlong entryPtr, jint uid);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetGid)(JNIEnv *env, jclass obj, jlong entryPtr, jint gid);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetFileSize)(JNIEnv *env, jclass obj, jlong entryPtr, jint fileSize);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetId)(JNIEnv *env, jclass obj, jlong entryPtr, jobject id);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetFlags)(JNIEnv *env, jclass obj, jlong entryPtr, jint flags);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetFlagsExtended)(JNIEnv *env, jclass obj, jlong entryPtr, jint flagsExtended);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetPath)(JNIEnv *env, jclass obj, jlong entryPtr, jstring path);
#ifdef __cplusplus
}
#endif
#endif
