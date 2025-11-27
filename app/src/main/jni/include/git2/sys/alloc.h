
#ifndef INCLUDE_sys_git_alloc_h__
#define INCLUDE_sys_git_alloc_h__
#include "git2/common.h"
GIT_BEGIN_DECL
typedef struct {
	void * GIT_CALLBACK(gmalloc)(size_t n, const char *file, int line);
	void * GIT_CALLBACK(grealloc)(void *ptr, size_t size, const char *file, int line);
	void GIT_CALLBACK(gfree)(void *ptr);
} git_allocator;
int git_stdalloc_init_allocator(git_allocator *allocator);
int git_win32_crtdbg_init_allocator(git_allocator *allocator);
GIT_END_DECL
#endif
