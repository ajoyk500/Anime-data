#include "j_commit.h"
#include "j_common.h"
#include "j_ensure.h"
#include "j_exception.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>
extern j_constants_t *jniConstants;
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessageEncoding)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const char *encoding = git_commit_message_encoding((git_commit *)commitPtr);
    return (*env)->NewStringUTF(env, encoding);
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessage)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const char *message = git_commit_message((git_commit *)commitPtr);
    return (*env)->NewStringUTF(env, message);
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessageRaw)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const char *msg_raw = git_commit_message_raw((git_commit *)commitPtr);
    return (*env)->NewStringUTF(env, msg_raw);
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniSummary)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const char *summary = git_commit_summary((git_commit *)commitPtr);
    return (*env)->NewStringUTF(env, summary);
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniBody)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const char *body = git_commit_body((git_commit *)commitPtr);
    return (*env)->NewStringUTF(env, body);
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Commit_jniTime)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    return (jlong)git_commit_time((git_commit *)commitPtr);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniTimeOffset)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    return (jint)git_commit_time_offset((git_commit *)commitPtr);
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Commit_jniCommitter)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const git_signature *c_sig = git_commit_committer((git_commit *)commitPtr);
    return (jlong)c_sig;
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Commit_jniAuthor)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const git_signature *c_sig = git_commit_author((git_commit *)commitPtr);
    return (jlong)c_sig;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCommitterWithMailmap)(JNIEnv *env, jclass obj, jobject out, jlong commitPtr, jlong mailmapPtr)
{
    git_signature *c_out = 0;
    int r = git_commit_committer_with_mailmap(&c_out, (git_commit *)commitPtr, (git_mailmap *)mailmapPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniAuthorWithMailmap)(JNIEnv *env, jclass obj, jobject out, jlong commitPtr, jlong mailmapPtr)
{
    git_signature *c_out = 0;
    int r = git_commit_author_with_mailmap(&c_out, (git_commit *)commitPtr, (git_mailmap *)mailmapPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniRawHeader)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const char *raw_header = git_commit_raw_header((git_commit *)commitPtr);
    return (*env)->NewStringUTF(env, raw_header);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniTree)(JNIEnv *env, jclass obj, jobject outTreePtr, jlong commitPtr)
{
    git_tree *tree = 0;
    int e = git_commit_tree(&tree, (git_commit *)commitPtr);
    (*env)->CallVoidMethod(env, outTreePtr, jniConstants->midAtomicLongSet, (jlong)tree);
    return e;
}
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Commit_jniTreeId)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const git_oid *c_oid = git_commit_tree_id((git_commit *)commitPtr);
    return j_git_oid_to_bytearray(env, c_oid);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniParentCount)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    return (jint)git_commit_parentcount((git_commit *)commitPtr);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniParent)(JNIEnv *env, jclass obj, jobject outPtr, jlong commitPtr, jint n)
{
    git_commit *c_out = 0;
    int e = git_commit_parent(&c_out, (git_commit *)commitPtr, (unsigned int)n);
    (*env)->CallVoidMethod(env, outPtr, jniConstants->midAtomicLongSet, (jlong)c_out);
    return e;
}
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Commit_jniParentId)(JNIEnv *env, jclass obj, jlong commitPtr, jint n)
{
    const git_oid *c_oid = git_commit_parent_id((git_commit *)commitPtr, (unsigned int)n);
    return j_git_oid_to_bytearray(env, c_oid);
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniNthGenAncestor)(JNIEnv *env, jclass obj, jobject outPtr, jlong commitPtr, jint n)
{
    git_commit *ancestor = 0;
    int e = git_commit_nth_gen_ancestor(&ancestor, (git_commit *)commitPtr, (unsigned int)n);
    (*env)->CallVoidMethod(env, outPtr, jniConstants->midAtomicLongSet, (jlong)ancestor);
    return e;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniHeaderField)(JNIEnv *env, jclass obj, jobject outBuf, jlong commitPtr, jstring field)
{
    git_buf c_buf = {0};
    char *c_field = j_copy_of_jstring(env, field, true);
    int e = git_commit_header_field(&c_buf, (git_commit *)commitPtr, c_field);
    j_git_buf_to_java(env, &c_buf, outBuf);
    git_buf_dispose(&c_buf);
    free(c_field);
    return e;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCreate)(JNIEnv *env,
                                                       jclass obj,
                                                       jobject outOid,
                                                       jlong repoPtr,
                                                       jstring updateRef,
                                                       jlong author,
                                                       jlong committer,
                                                       jstring msgEncoding,
                                                       jstring message,
                                                       jlong treePtr,
                                                       jlongArray parents)
{
    assert(parents && "parents must not be null");
    int e = 0;
    jsize np = (*env)->GetArrayLength(env, parents);
    jlong *parentCommitsPtr = (*env)->GetLongArrayElements(env, parents, 0);
    char *update_ref = j_copy_of_jstring(env, updateRef, true);
    char *message_encoding = j_copy_of_jstring(env, msgEncoding, true);
    char *c_message = j_copy_of_jstring(env, message, true);
    git_oid c_oid;
    e = git_commit_create(&c_oid,
                          (git_repository *)repoPtr,
                          update_ref,
                          (git_signature *)author,
                          (git_signature *)committer,
                          message_encoding,
                          c_message,
                          (git_tree *)treePtr,
                          np,
                          (git_commit **)parentCommitsPtr);
    j_git_oid_to_java(env, &c_oid, outOid);
    free(c_message);
    free(message_encoding);
    free(update_ref);
    (*env)->ReleaseLongArrayElements(env, parents, parentCommitsPtr, 0);
    return e;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniAmend)(JNIEnv *env, jclass obj,
                                                      jobject id,
                                                      jlong commitToAmendPtr,
                                                      jstring update_ref,
                                                      jlong authorPtr,
                                                      jlong committerPtr,
                                                      jstring message_encoding,
                                                      jstring message,
                                                      jlong treePtr)
{
    git_oid c_id;
    char *c_update_ref = j_copy_of_jstring(env, update_ref, true);
    char *c_message_encoding = j_copy_of_jstring(env, message_encoding, true);
    char *c_message = j_copy_of_jstring(env, message, true);
    int r = git_commit_amend(&c_id,
                             (git_commit *)commitToAmendPtr,
                             c_update_ref,
                             (git_signature *)authorPtr,
                             (git_signature *)committerPtr,
                             c_message_encoding,
                             c_message,
                             (git_tree *)treePtr);
    j_git_oid_to_java(env, &c_id, id);
    free(c_update_ref);
    free(c_message_encoding);
    free(c_message);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCreateBuffer)(JNIEnv *env, jclass obj,
                                                             jobject out,
                                                             jlong repoPtr,
                                                             jlong authorPtr,
                                                             jlong committerPtr,
                                                             jstring message_encoding,
                                                             jstring message,
                                                             jlong treePtr,
                                                             jint parentCount,
                                                             jlongArray parents)
{
    git_buf c_out = {0};
    char *c_message_encoding = j_copy_of_jstring(env, message_encoding, true);
    char *c_message = j_copy_of_jstring(env, message, true);
    assert(parents && "parents must not be null");
    jsize np = (*env)->GetArrayLength(env, parents);
    jlong *elements = (*env)->GetLongArrayElements(env, parents, 0);
    int r = git_commit_create_buffer(
        &c_out,
        (git_repository *)repoPtr,
        (git_signature *)authorPtr,
        (git_signature *)committerPtr,
        c_message_encoding,
        c_message,
        (git_tree *)treePtr,
        parentCount,
        (git_commit **)elements);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    free(c_message_encoding);
    free(c_message);
    (*env)->ReleaseLongArrayElements(env, parents, elements, 0);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCreateWithSignature)(JNIEnv *env, jclass obj,
                                                                    jobject outOid,
                                                                    jlong repoPtr,
                                                                    jstring commitContent,
                                                                    jstring signature,
                                                                    jstring signatureField)
{
    git_oid c_oid;
    char *commit_content = j_copy_of_jstring(env, commitContent, true);
    char *c_signature = j_copy_of_jstring(env, signature, true);
    char *signature_field = j_copy_of_jstring(env, signatureField, true);
    int e = git_commit_create_with_signature(
        &c_oid,
        (git_repository *)repoPtr,
        commit_content,
        c_signature,
        signature_field);
    free(signature_field);
    free(c_signature);
    free(commit_content);
    j_git_oid_to_java(env, &c_oid, outOid);
    return e;
}
