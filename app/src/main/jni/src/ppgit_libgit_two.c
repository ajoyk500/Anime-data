#include "git2.h"
#include "ppgit_common.h"
#include <errno.h>
#include <android/log.h>
#include <jni.h>
#include <assert.h>
#include <string.h>
#include <stdbool.h>
#define LOG_TAG "JNI_libgit_two"
JNIEnv * globalEnv=NULL;
JNIEXPORT jstring JNICALL J_MAKE_METHOD(LibgitTwo_hello)(JNIEnv *env, jclass type, jint a, jint b) {
    return (*env)->NewStringUTF(env, "Hello from NDK,sum is:");
}
static jclass findClass(JNIEnv *env, const char *name) {
    jclass localClass = (*env)->FindClass(env, name);
    if (!localClass) {
        ALOGE("Failed to find class '%s'", name);
        abort();
    }
    return localClass;
}
static jfieldID findField(JNIEnv *env, jclass clazz, const char *name, const char *signature) {
    jfieldID field = (*env)->GetFieldID(env, clazz, name, signature);
    if (!field) {
        ALOGE("Failed to find field '%s' '%s'", name, signature);
        abort();
    }
    return field;
}
static jmethodID findMethod(JNIEnv *env, jclass clazz, const char *name, const char *signature) {
    jmethodID method = (*env)->GetMethodID(env, clazz, name, signature);
    if (!method) {
        ALOGE("Failed to find method '%s' '%s'", name, signature);
        abort();
    }
    return method;
}
static void throwException(JNIEnv *env, jclass exceptionClass, jmethodID constructor3,
                           jmethodID constructor2, const char *functionName, int error) {
    jthrowable cause = NULL;
    if ((*env)->ExceptionCheck(env)) {
        cause = (*env)->ExceptionOccurred(env);
        (*env)->ExceptionClear(env);
    }
    jstring detailMessage = (*env)->NewStringUTF(env, functionName);
    if (!detailMessage) {
        (*env)->ExceptionClear(env);
    }
    jobject exception;
    if (cause) {
        exception = (*env)->NewObject(env, exceptionClass, constructor3, detailMessage, error,
                                      cause);
    } else {
        exception = (*env)->NewObject(env, exceptionClass, constructor2, detailMessage, error);
    }
    (*env)->Throw(env, exception);
    if (detailMessage) {
        (*env)->DeleteLocalRef(env, detailMessage);
    }
}
static jclass getLibgitTwoExceptionClass(JNIEnv *env) {
    static jclass exceptionClass = NULL;
    if (!exceptionClass) {
        exceptionClass = findClass(env, J_CLZ_PREFIX "LibGitTwoException");
    }
    return exceptionClass;
}
__attribute__((unused))  static void throwLibgitTwoException(JNIEnv* env, const char* functionName) {
    int error = errno;  
    static jmethodID constructor3 = NULL;
    if (!constructor3) {
        constructor3 = findMethod(env, getLibgitTwoExceptionClass(env), "<init>",
                                  "(Ljava/lang/String;ILjava/lang/Throwable;)V");
    }
    static jmethodID constructor2 = NULL;
    if (!constructor2) {
        constructor2 = findMethod(env, getLibgitTwoExceptionClass(env), "<init>",
                                  "(Ljava/lang/String;I)V");
    }
    throwException(env, getLibgitTwoExceptionClass(env), constructor3, constructor2, functionName,
                   error);
}
static char *j_strcopy(const char* src) {
    char *copy=NULL;
    size_t len = strlen(src)+1;
    if(src) {
        copy = malloc(len);
        if(copy) {
            strncpy(copy, src, len);
        }
    }
    return copy;
}
static char *j_copy_of_jstring(JNIEnv *env, jstring jstr, bool nullable) {
    if(!nullable) {
        assert(jstr && "Cannot cast null to c string");
    }
    if(!jstr) {
        return NULL;
    }
    const char *c_str = (*env)->GetStringUTFChars(env,jstr,NULL);
    char *copy = j_strcopy(c_str);
    (*env)->ReleaseStringUTFChars(env, jstr, c_str);
    return copy;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(LibgitTwo_jniLibgitTwoInit)(JNIEnv *env, jclass callerJavaClass) {
    git_libgit2_init();
}
int passCertCheck(git_cert *cert, int valid, const char *host, void *payload) {
    return 0;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(LibgitTwo_jniSetCertCheck)(JNIEnv *env, jclass callerJavaClass, jlong remoteCallbacksPtr) {
    ((git_remote_callbacks *)remoteCallbacksPtr) ->certificate_check = passCertCheck;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(LibgitTwo_jniSetCertFileAndOrPath)(JNIEnv *env, jclass callerJavaClass, jstring certFile, jstring certPath) {
    if(!certFile) {
        ALOGI("certFile is NULL");
    }
    if(!certPath) {
        ALOGI("certPath is NULL");
    }
    char *c_certFile = j_copy_of_jstring(env, certFile, true);
    char *c_certPath = j_copy_of_jstring(env, certPath, true);
    int error=git_libgit2_opts(GIT_OPT_SET_SSL_CERT_LOCATIONS,c_certFile,c_certPath);
    free(c_certFile);
    free(c_certPath);
    return error;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(LibgitTwo_jniClone)(JNIEnv *env, jclass callerJavaClass, jstring url, jstring local_path, jlong jniOptionsPtr, jboolean allowInsecure) {
    git_repository *cloned_repo=NULL;
    char *c_url=j_copy_of_jstring(env, url, true);
    char *c_local_path=j_copy_of_jstring(env, local_path, true);
    git_clone_options cloneOptions = GIT_CLONE_OPTIONS_INIT;  
    git_checkout_options checkout_opts = GIT_CHECKOUT_OPTIONS_INIT;
    checkout_opts.checkout_strategy = GIT_CHECKOUT_SAFE;
    cloneOptions.checkout_opts = checkout_opts;
    char *certfile="/storage/emulated/0/Android/data/com.catpuppyapp.puppygit/files/cafolder/399e7759.0";
    char *capath="/storage/emulated/0/Android/data/com.catpuppyapp.puppygit/files/cafolder";
    char *syscapath="/system/etc/security/cacerts/";
    char *syscapath2="/system/etc/security/cacerts_google";
    char *gitlabca="/storage/emulated/0/Android/data/com.catpuppyapp.puppygit/files/cafolder/gitlab.crt";
    ALOGE("before set cert path in libgit");
    int error = 0;
     error=git_libgit2_opts(GIT_OPT_SET_SSL_CERT_LOCATIONS,NULL,syscapath);
    if(error!=0) {
        ALOGE("jniClonecertpath::ERROR '%d'", error);
    }
    if(allowInsecure){
        cloneOptions.fetch_opts.callbacks.certificate_check = passCertCheck;
    }
    int ret = git_clone(&cloned_repo, c_url, c_local_path, &cloneOptions);
    free(c_url);
    free(c_local_path);
    if(ret!=0) {
        const git_error *err = git_error_last();
        ALOGE("jniClone::ERROR '%s'", err->message);
        return JNI_ERR;
    }
    return (jlong)cloned_repo;
}
int cred_acquire_cb(git_credential **out, const char *url, const char *username_from_url, unsigned int allowed_types, void *payload) {
    return git_credential_userpass_plaintext_new(out, "testusername", "testpassword");
}
JNIEXPORT void JNICALL J_MAKE_METHOD(LibgitTwo_jniSetCredentialCbTest)(JNIEnv *env, jclass callerJavaClass, jlong remoteCallbacks) {
    ALOGD("LibgitTwo_jniSetCredentialCbTest::");
    globalEnv = env;
    git_remote_callbacks *ptr = (git_remote_callbacks *)remoteCallbacks;
    ptr->credentials = cred_acquire_cb;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(LibgitTwo_jniCreateCloneOptions)(JNIEnv *env, jclass callerJavaClass, jint version) {
    git_clone_options *clone_options = (git_clone_options *)malloc(sizeof(git_clone_options));
    int ret = git_clone_init_options(clone_options,(unsigned int)version);
    if(ret!=0) {
        return JNI_ERR;
    }
    return (jlong)clone_options;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(LibgitTwo_jniTestClone)(JNIEnv *env, jclass callerJavaClass, jstring url, jstring local_path, jlong jniOptionsPtr) {
    git_clone_options *clone_options = (git_clone_options *)malloc(sizeof(git_clone_options));
    int ret = git_clone_init_options(clone_options,1);
    if(ret!=0) {
        return JNI_ERR;
    }
    git_repository *repo=NULL;
    char *c_url=j_copy_of_jstring(env, url, true);
    char *c_local_path=j_copy_of_jstring(env, local_path, true);
    int ret2 = git_clone(&repo, c_url, c_local_path, clone_options);
    free(c_url);
    free(c_local_path);
    if(ret || ret2) {
        return JNI_ERR;
    }
    return (jlong)repo;
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(LibgitTwo_jniLineGetContent)(JNIEnv *env, jclass callerJavaClass, jlong linePtr)
{
    ALOGD("ccode: jniLineGetContent() start\n");
    jstring s = (*env)->NewStringUTF(env, ((git_diff_line *)linePtr)->content);
    ALOGD("ccode: jniLineGetContent() end\n");
    return s;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(LibgitTwo_jniTestAccessExternalStorage)(JNIEnv *env, jclass callerJavaClass)
{
    ALOGD("ccode: LibgitTwo_jniTestAccessExternalStorage() start\n");
    FILE* file = fopen("/sdcard/puppygit-repos/hello.txt","w+");
    if (file != NULL)
    {
        fputs("HELLO WORLD!\n", file);
        fflush(file);
        fclose(file);
    }
    ALOGD("ccode: LibgitTwo_jniTestAccessExternalStorage() end\n");
}
JNIEXPORT jobject JNICALL J_MAKE_METHOD(LibgitTwo_jniEntryByName)(JNIEnv *env, jclass callerJavaClass, jlong treePtr, jstring filename)
{
    char *c_filename = j_copy_of_jstring(env, filename, true);
    const git_tree_entry *r = git_tree_entry_byname((git_tree *)treePtr, c_filename);
    free(c_filename);
    return (jobject)r;
}
void bytesToHexString(const unsigned char *bytes, size_t length, char *hexString) {
    for (size_t i = 0; i < length; i++) {
        sprintf(hexString + (i * 2), "%02x", bytes[i]);
    }
    hexString[length * 2] = '\0'; 
}
jobject createSshCert(git_cert_hostkey *certHostKey, jstring hostname, JNIEnv *env) {
    jclass sshCertClass = findClass(env, J_CLZ_PREFIX "SshCert");
    jmethodID constructor = findMethod(env, sshCertClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    char *md5Str[16*2+1];
    char *sha1Str[20*2+1];
    char *sha256Str[32*2+1];
    char *hostKeyStr[certHostKey->hostkey_len + 1];
    if(certHostKey->type&GIT_CERT_SSH_MD5) {
        bytesToHexString(certHostKey->hash_md5, 16, md5Str);
    }
    if(certHostKey->type&GIT_CERT_SSH_SHA1) {
        bytesToHexString(certHostKey->hash_sha1, 20, sha1Str);
    }
    if(certHostKey->type&GIT_CERT_SSH_SHA256) {
        bytesToHexString(certHostKey->hash_sha256, 32, sha256Str);
    }
    if(certHostKey->type&GIT_CERT_SSH_RAW) {
        strncpy(hostKeyStr, certHostKey->hostkey, certHostKey->hostkey_len);
        hostKeyStr[certHostKey->hostkey_len] = '\0';
    }
    jobject sshCertObject = (*env)->NewObject(
            env,
            sshCertClass,
            constructor,
            hostname,
            (*env)->NewStringUTF(env, (const char *) md5Str),
            (*env)->NewStringUTF(env, (const char *) sha1Str),
            (*env)->NewStringUTF(env, (const char *) sha256Str),
            (*env)->NewStringUTF(env, (const char *) hostKeyStr)
    );
    (*env)->DeleteLocalRef(env, sshCertClass);
    return sshCertObject;
}
JNIEXPORT jobject JNICALL J_MAKE_METHOD(LibgitTwo_jniGetDataOfSshCert)(JNIEnv *env, jclass callerJavaClass, jlong cretprt, jstring hostname)
{
    git_cert_t type = ((git_cert*)cretprt)->cert_type;
    if(type == GIT_CERT_HOSTKEY_LIBSSH2) {
        return createSshCert((git_cert_hostkey *)cretprt, hostname, env);
    }else {
        ALOGW("is not a ssh cert, type=%d", type);
        return NULL;
    }
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(LibgitTwo_jniSaveBlobToPath)(JNIEnv *env, jclass callerJavaClass, jlong blobPtr, jstring savePath)
{
    git_blob* blob = (git_blob *)blobPtr;
    const char* blob_data = git_blob_rawcontent(blob);
    size_t blob_size = git_blob_rawsize(blob);
    char* c_savePath = j_copy_of_jstring(env, savePath, true);
    if(c_savePath == NULL) {
        free(c_savePath);
        return -1;
    }
    FILE *output_file = fopen(c_savePath, "wb");
    fwrite(blob_data, 1, blob_size, output_file);
    fclose(output_file);
    free(c_savePath);
    return 0;
}
JNIEXPORT jlongArray JNICALL J_MAKE_METHOD(LibgitTwo_jniGetStatusEntryRawPointers)(JNIEnv *env, jclass obj, jlong statusListPtr) {
    git_status_list* listPtr = (git_status_list *)statusListPtr;
    size_t length = git_status_list_entrycount(listPtr);
    jlongArray resultArray = (*env)->NewLongArray(env, length);
    if (resultArray == NULL) {
        return NULL; 
    }
    jsize jlongSize = sizeof(jlong);
    for(int i=0; i < length; i++) {
        (*env)->SetLongArrayRegion(env, resultArray, i, jlongSize, git_status_byindex(listPtr, i));
    }
    return resultArray;
}
jobject createStatusEntryDto(
        JNIEnv *env,
        jclass statusEntryDtoClass,
        jmethodID constructor,
        jstring indexToWorkDirOldFilePath,
        jstring indexToWorkDirNewFilePath,
        jstring headToIndexOldFilePath,
        jstring headToIndexNewFilePath,
        jlong indexToWorkDirOldFileSize,
        jlong indexToWorkDirNewFileSize,
        jlong headToIndexOldFileSize,
        jlong headToIndexNewFileSize,
        jint statusFlag
) {
    return (*env)->NewObject(
            env,
            statusEntryDtoClass,
            constructor,
            indexToWorkDirOldFilePath,
            indexToWorkDirNewFilePath,
            headToIndexOldFilePath,
            headToIndexNewFilePath,
            indexToWorkDirOldFileSize,
            indexToWorkDirNewFileSize,
            headToIndexOldFileSize,
            headToIndexNewFileSize,
            statusFlag
    );
}
jclass statusEntryDtoClassCache = NULL;
JNIEXPORT jobjectArray JNICALL J_MAKE_METHOD(LibgitTwo_jniGetStatusEntries)(JNIEnv *env, jclass obj, jlong statusListPtr) {
    git_status_list* listPtr = (git_status_list *)statusListPtr;
    size_t length = git_status_list_entrycount(listPtr);
    jclass statusEntryDtoClass = findClass(env, J_CLZ_PREFIX "StatusEntryDto");
    jmethodID constructor = findMethod(env, statusEntryDtoClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JJJJI)V");
    jobjectArray statusEntryDtoArray = (*env)->NewObjectArray(env, length, statusEntryDtoClass, NULL);
    if (statusEntryDtoArray == NULL) {
        return NULL; 
    }
    for(int i=0; i < length; i++) {
        git_status_entry* entry = git_status_byindex(listPtr, i);
        git_diff_delta * head2IndexDelta = entry->head_to_index;
        git_diff_delta * index2WorkDirDelta = entry->index_to_workdir;
        jstring index2WorkDirDeltaOldFilePath = NULL;
        jstring index2WorkDirDeltaNewFilePath = NULL;
        jlong index2WorkDirDeltaOldFileSize = 0;
        jlong index2WorkDirDeltaNewFileSize = 0;
        if(index2WorkDirDelta != NULL) {
            index2WorkDirDeltaOldFilePath = (*env)->NewStringUTF(env, index2WorkDirDelta->old_file.path);
            index2WorkDirDeltaNewFilePath = (*env)->NewStringUTF(env, index2WorkDirDelta->new_file.path);
            index2WorkDirDeltaOldFileSize = index2WorkDirDelta->old_file.size;
            index2WorkDirDeltaNewFileSize = index2WorkDirDelta->new_file.size;
        }
        jstring head2IndexDeltaOldFilePath = NULL;
        jstring head2IndexDeltaNewFilePath = NULL;
        jlong head2IndexDeltaOldFileSize = 0;
        jlong head2IndexDeltaNewFileSize = 0;
        if(head2IndexDelta != NULL) {
            head2IndexDeltaOldFilePath = (*env)->NewStringUTF(env, head2IndexDelta->old_file.path);
            head2IndexDeltaNewFilePath = (*env)->NewStringUTF(env, head2IndexDelta->new_file.path);
            head2IndexDeltaOldFileSize = head2IndexDelta->old_file.size;
            head2IndexDeltaNewFileSize = head2IndexDelta->new_file.size;
        }
        (*env)->SetObjectArrayElement(
                env,
                statusEntryDtoArray,
                i,
                createStatusEntryDto(
                    env,
                    statusEntryDtoClass,
                    constructor,
                    index2WorkDirDeltaOldFilePath,
                    index2WorkDirDeltaNewFilePath,
                    head2IndexDeltaOldFilePath,
                    head2IndexDeltaNewFilePath,
                    index2WorkDirDeltaOldFileSize,
                    index2WorkDirDeltaNewFileSize,
                    head2IndexDeltaOldFileSize,
                    head2IndexDeltaNewFileSize,
                    entry->status
                )
        );
    }
    (*env)->DeleteLocalRef(env, statusEntryDtoClass);
    return statusEntryDtoArray;
}
