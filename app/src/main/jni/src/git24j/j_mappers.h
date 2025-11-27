#include <git2.h>
#include <jni.h>
#include <stdbool.h>
#ifndef __GIT24J_MAPPERS_H__
#define __GIT24J_MAPPERS_H__
#ifdef __cplusplus
extern "C"
{
#endif
    typedef struct
    {
        jobject callback;
        jmethodID mid;
    } j_cb_payload;
    typedef struct
    {
        jclass clzCallbacks;
        jmethodID midTransportMessage;
        jmethodID midCompletion;
        jmethodID midAcquireCred;
        jmethodID midTransportCertificateCheck;
        jmethodID midTransferProgress;
        jmethodID midUpdateTips;
        jmethodID midPackProgress;
        jmethodID midPushTransferProgress;
        jmethodID midPushUpdateReference;
        jmethodID midPushNegotiation;
        jmethodID midTransport;
        jmethodID midResolveUrl;
    } j_remote_constants;
    typedef struct
    {
        jclass clzList;
        jmethodID midGet;
        jmethodID midSize;
        jmethodID midAdd;
    } j_list;
    typedef struct
    {
        jclass clzOid;
        jmethodID midSetId;
        jmethodID midGetId;
    } j_oid_constants;
    typedef struct
    {
        jclass clzBuf;
        jmethodID emptyConstructor;
    } j_git_buf_constants_t;
    typedef struct
    {
        jclass clzGitCacheMemorySaver;
        jmethodID emptyConstructor;
        jmethodID midGetCurrentStorageValue;
        jmethodID midSetCurrentStorageValue;
        jmethodID midGetMaxStorage;
        jmethodID midSetMaxStorage;
    } j_git_cache_memory_saver_constants_t;
    typedef struct
    {
        jclass clzAtomicInt;
        jclass clzAtomicLong;
        jclass clzAtomicReference;
        jclass clzList;
        jmethodID midAtomicIntSet;
        jmethodID midAtomicLongSet;
        jmethodID midAtomicLongGet;
        jmethodID midAtomicLongInit;
        jmethodID midAtomicReferenceSet;
        jmethodID midListGetI;
        j_remote_constants remote;
        j_oid_constants oid;
        j_git_buf_constants_t buf;
        j_git_cache_memory_saver_constants_t gitCacheMemorySaver;
    } j_constants_t;
    extern j_constants_t *jniConstants;
    JNIEnv *getEnv(void);
    void j_cb_payload_init(JNIEnv *env, j_cb_payload *payload, jobject callback, const char *methodSig);
    void j_cb_payload_release(JNIEnv *env, j_cb_payload *payload);
    char *j_strdup(const char *src);
    char *j_copy_of_jstring(JNIEnv *env, jstring jstr, bool nullable);
    void j_git_buf_of_jstring(JNIEnv *env, git_buf *out_buf, jstring jstr);
    void j_git_buf_to_java(JNIEnv *env, git_buf *c_buf, jobject buf);
    jstring j_git_buf_to_jstring(JNIEnv *env, const git_buf *c_buf);
    void index_entry_from_java(JNIEnv *env, git_index_entry *c_entry, jobject entry);
    jbyteArray j_byte_array_from_c(JNIEnv *env, const unsigned char *buf, int len);
    jlongArray j_long_array_from_pointers(JNIEnv *env, const void **ptrs, size_t n);
    void **j_long_array_to_pointers(JNIEnv *env, jlongArray pointers, size_t *out_len, int freeJavaArr);
    unsigned char *j_unsigned_chars_from_java(JNIEnv *env, jbyteArray array, int *out_len);
    void j_git_oid_to_java(JNIEnv *env, const git_oid *c_oid, jobject oid);
    void j_git_short_id_to_java(JNIEnv *env, const git_oid *c_oid, jobject oid, int effectiveSize);
    int j_git_short_id_from_java(JNIEnv *env, jstring oidStr, git_oid *c_oid, int *out_len);
    void j_git_oid_from_java(JNIEnv *env, jobject oid, git_oid *c_oid);
    void j_git_oidarray_to_java(JNIEnv *env, jobject outOidArr, const git_oidarray *c_arr);
    jbyteArray j_git_oid_to_bytearray(JNIEnv *env, const git_oid *c_oid);
    char *j_call_getter_string(JNIEnv *env, jclass clz, jobject obj, const char *methodName);
    int j_call_getter_int(JNIEnv *env, jclass clz, jobject obj, const char *methodName);
    long j_call_getter_long(JNIEnv *env, jclass clz, jobject obj, const char *methodName);
    void j_call_setter_long(JNIEnv *env, jclass clz, jobject obj, const char *method, jlong val);
    void j_call_setter_int(JNIEnv *env, jclass clz, jobject obj, const char *method, jint val);
    void j_call_setter_object(JNIEnv *env, jclass clz, jobject receiver, const char *method, jobject jval);
    void j_call_setter_string(JNIEnv *env, jclass clz, jobject obj, const char *method, jstring val);
    void j_call_setter_string_c(JNIEnv *env, jclass clz, jobject obj, const char *method, const char *val);
    void j_call_setter_byte_array(JNIEnv *env, jclass clz, jobject obj, const char *method, jbyteArray val);
    jbyteArray j_call_getter_byte_array(JNIEnv *env, jclass clz, jobject obj, const char *method);
    void j_set_object_field(JNIEnv *env, jclass clz, jobject obj, jobject val, const char *field, const char *sig);
    void j_set_string_field_c(JNIEnv *env, jclass clz, jobject obj, const char *val, const char *field);
    void j_strarray_to_java_list(JNIEnv *env, git_strarray *src, jobject strList);
    void j_strarray_from_java(JNIEnv *env, git_strarray *out, jobjectArray strArr);
    void j_strarray_to_java_array(JNIEnv *env, jobjectArray out, git_strarray *src);
    void deprecated_signature_to_java(JNIEnv *env, const git_signature *c_sig, jobject sig);
    int deprecated_signature_from_java(JNIEnv *env, jobject sig, git_signature **out_sig);
    void j_atomic_long_set(JNIEnv *env, long val, jobject outAL);
    void __debug_inspect(JNIEnv *env, jobject obj);
    void __debug_inspect2(JNIEnv *env, jobject obj, const char *message, const char *fname);
    void j_clear_git_strarray(git_strarray* sarr);
#ifdef __cplusplus
}
#endif
#endif
