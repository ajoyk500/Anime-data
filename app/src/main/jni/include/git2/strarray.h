
#ifndef INCLUDE_git_strarray_h__
#define INCLUDE_git_strarray_h__
#include "common.h"
GIT_BEGIN_DECL
typedef struct git_strarray {
	char **strings;
	size_t count;
} git_strarray;
GIT_EXTERN(void) git_strarray_dispose(git_strarray *array);
GIT_END_DECL
#endif
