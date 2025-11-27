
#ifndef __GIT24J_UTIL_H__
#define __GIT24J_UTIL_H__
#ifdef __cplusplus
extern "C"
{
#endif
#include <git2.h>
#include <jni.h>
#include <stdbool.h>
    void j_save_c_pointer(JNIEnv *env, void *ptr, jobject object, const char *setterName);
    void git_strarray_of_jobject_array(JNIEnv *env, jobjectArray jstrarr, git_strarray *out);
    char *new_substr(const char *str, size_t len);
#ifdef __cplusplus
}
#endif
#endif
