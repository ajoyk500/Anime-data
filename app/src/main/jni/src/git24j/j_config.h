#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_CONFIG_H__
#define __GIT24J_CONFIG_H__
#ifdef __cplusplus
extern "C"
{
#endif
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Config_jniEntryGetName)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Config_jniEntryGetValue)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Config_jniEntryGetBackendType)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Config_jniEntryGetOriginPath)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniEntryGetIncludeDepth)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniEntryGetLevel)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Config_jniEntryFree)(JNIEnv *env, jclass obj, jlong entryPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindGlobal)(JNIEnv *env, jclass obj, jobject out);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindXdg)(JNIEnv *env, jclass obj, jobject out);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindSystem)(JNIEnv *env, jclass obj, jobject out);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindProgramdata)(JNIEnv *env, jclass obj, jobject out);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniOpenDefault)(JNIEnv *env, jclass obj, jobject out);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniNew)(JNIEnv *env, jclass obj, jobject out);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniAddFileOndisk)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring path, jint level, jlong repoPtr, jint force);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniOpenOndisk)(JNIEnv *env, jclass obj, jobject out, jstring path);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniOpenLevel)(JNIEnv *env, jclass obj, jobject out, jlong parentPtr, jint level);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniOpenGlobal)(JNIEnv *env, jclass obj, jobject out, jlong configPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSnapshot)(JNIEnv *env, jclass obj, jobject out, jlong configPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Config_jniFree)(JNIEnv *env, jclass obj, jlong cfgPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetEntry)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetInt32)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetInt64)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetBool)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetPath)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetString)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetStringBuf)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetMultivarForeach)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jstring regexp, jobject callback);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniMultivarIteratorNew)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name, jstring regexp);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniNext)(JNIEnv *env, jclass obj, jobject entry, jlong iterPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Config_jniIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetInt32)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jint value);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetInt64)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jlong value);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetBool)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jint value);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetString)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jstring value);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetMultivar)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jstring regexp, jstring value);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniDeleteEntry)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniDeleteMultivar)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jstring regexp);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniForeach)(JNIEnv *env, jclass obj, jlong cfgPtr, jobject callback);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniIteratorNew)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniIteratorGlobNew)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring regexp);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniForeachMatch)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring regexp, jobject callback);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniParseBool)(JNIEnv *env, jclass obj, jobject out, jstring value);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniParseInt32)(JNIEnv *env, jclass obj, jobject out, jstring value);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniParseInt64)(JNIEnv *env, jclass obj, jobject out, jstring value);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniParsePath)(JNIEnv *env, jclass obj, jobject out, jstring value);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniBackendForeachMatch)(JNIEnv *env, jclass obj, jlong backendPtr, jstring regexp, jobject callback);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniLock)(JNIEnv *env, jclass obj, jobject tx, jlong cfgPtr);
#ifdef __cplusplus
}
#endif
#endif
