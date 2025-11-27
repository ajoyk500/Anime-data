
#ifndef __GIT24J_COMMON_H__
#define __GIT24J_COMMON_H__
#ifdef __cplusplus
extern "C"
{
#endif
#define J_MAKE_METHOD(CM) Java_com_catpuppyapp_puppygit_jni_##CM
#define J_CLZ_PREFIX "com/catpuppyapp/puppygit/jni/"
#define J_DEFAULT_MAX_MSG_LEN 4096
#define J_NO_CLASS_ERROR "java/lang/NoClassDefFoundError"
#define ALOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define ALOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define ALOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define ALOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#ifdef __cplusplus
}
#endif
#endif
