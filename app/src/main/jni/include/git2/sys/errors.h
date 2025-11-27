
#ifndef INCLUDE_sys_git_errors_h__
#define INCLUDE_sys_git_errors_h__
#include "git2/common.h"
GIT_BEGIN_DECL
GIT_EXTERN(void) git_error_clear(void);
GIT_EXTERN(void) git_error_set(int error_class, const char *fmt, ...)
                 GIT_FORMAT_PRINTF(2, 3);
GIT_EXTERN(int) git_error_set_str(int error_class, const char *string);
GIT_EXTERN(void) git_error_set_oom(void);
GIT_END_DECL
#endif
