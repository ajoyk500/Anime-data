
#ifndef INCLUDE_git_buf_h__
#define INCLUDE_git_buf_h__
#include "common.h"
GIT_BEGIN_DECL
typedef struct {
	char *ptr;
	size_t reserved;
	size_t size;
} git_buf;
#define GIT_BUF_INIT { NULL, 0, 0 }
GIT_EXTERN(void) git_buf_dispose(git_buf *buffer);
GIT_END_DECL
#endif
