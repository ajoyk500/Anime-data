
#ifndef INCLUDE_git_oidarray_h__
#define INCLUDE_git_oidarray_h__
#include "common.h"
#include "oid.h"
GIT_BEGIN_DECL
typedef struct git_oidarray {
	git_oid *ids;
	size_t count;
} git_oidarray;
GIT_EXTERN(void) git_oidarray_dispose(git_oidarray *array);
GIT_END_DECL
#endif
