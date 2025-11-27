#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_ODB_H__
#define __GIT24J_ODB_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniStreamRead)(JNIEnv *env, jclass obj, jlong streamPtr, jbyteArray buffer, jint len);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniNew)(JNIEnv *env, jclass obj, jobject out);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniOpen)(JNIEnv *env, jclass obj, jobject out, jstring objects_dir);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniAddDiskAlternate)(JNIEnv *env, jclass obj, jlong odbPtr, jstring path);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Odb_jniFree)(JNIEnv *env, jclass obj, jlong dbPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniRead)(JNIEnv *env, jclass obj, jobject out, jlong dbPtr, jobject id);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniReadPrefix)(JNIEnv *env, jclass obj, jobject out, jlong dbPtr, jstring shortId);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniReadHeader)(JNIEnv *env, jclass obj, jobject lenOut, jobject typeOut, jlong dbPtr, jobject id);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExists)(JNIEnv *env, jclass obj, jlong dbPtr, jobject id);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExistsPrefix)(JNIEnv *env, jclass obj, jobject out, jlong dbPtr, jstring shortId);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExpandIds)(JNIEnv *env, jclass obj, jlong dbPtr, jlong idsPtr, jint count);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Odb_jniExpandIdsNew)(JNIEnv *env, jclass obj, jobjectArray shortIds, jint type);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Odb_jniExpandIdsGetId)(JNIEnv *env, jclass obj, jlong expandIdsPtr, jint idx);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExpandIdsGetType)(JNIEnv *env, jclass obj, jlong expandIdsPtr, jint idx);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExpandIdsGetLength)(JNIEnv *env, jclass obj, jlong expandIdsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniRefresh)(JNIEnv *env, jclass obj, jlong dbPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniWrite)(JNIEnv *env, jclass obj, jobject out, jlong odbPtr, jbyteArray data, jint len, jint type);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniOpenWstream)(JNIEnv *env, jclass obj, jobject out, jlong dbPtr, jint size, jint type);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniStreamWrite)(JNIEnv *env, jclass obj, jlong streamPtr, jstring buffer, jint len);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniStreamFinalizeWrite)(JNIEnv *env, jclass obj, jobject out, jlong streamPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Odb_jniStreamFree)(JNIEnv *env, jclass obj, jlong streamPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniOpenRstream)(JNIEnv *env, jclass obj, jobject out, jobject len, jobject outType, jlong dbPtr, jobject oid);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniHash)(JNIEnv *env, jclass obj, jobject out, jbyteArray data, jint len, jint type);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniHashfile)(JNIEnv *env, jclass obj, jobject out, jstring path, jint type);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniObjectDup)(JNIEnv *env, jclass obj, jobject dest, jlong sourcePtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Odb_jniObjectFree)(JNIEnv *env, jclass obj, jlong objectPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Odb_jniObjectId)(JNIEnv *env, jclass obj, jlong objectPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Odb_jniObjectData)(JNIEnv *env, jclass obj, jlong objectPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniObjectSize)(JNIEnv *env, jclass obj, jlong objectPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniObjectType)(JNIEnv *env, jclass obj, jlong objectPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniAddBackend)(JNIEnv *env, jclass obj, jlong odbPtr, jlong backendPtr, jint priority);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniAddAlternate)(JNIEnv *env, jclass obj, jlong odbPtr, jlong backendPtr, jint priority);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniNumBackends)(JNIEnv *env, jclass obj, jlong odbPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniGetBackend)(JNIEnv *env, jclass obj, jobject out, jlong odbPtr, jint pos);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniBackendPack)(JNIEnv *env, jclass obj, jobject out, jstring objects_dir);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniBackendLoose)(JNIEnv *env, jclass obj, jobject out, jstring objects_dir, jint compression_level, jint do_fsync, jint dirMode, jint fileMode);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniBackendOnePack)(JNIEnv *env, jclass obj, jobject out, jstring index_file);
#ifdef __cplusplus
}
#endif
#endif