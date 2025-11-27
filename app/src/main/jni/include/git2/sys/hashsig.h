
#ifndef INCLUDE_sys_hashsig_h__
#define INCLUDE_sys_hashsig_h__
#include "git2/common.h"
GIT_BEGIN_DECL
typedef struct git_hashsig git_hashsig;
typedef enum {
	GIT_HASHSIG_NORMAL = 0,
	GIT_HASHSIG_IGNORE_WHITESPACE = (1 << 0),
	GIT_HASHSIG_SMART_WHITESPACE = (1 << 1),
	GIT_HASHSIG_ALLOW_SMALL_FILES = (1 << 2)
} git_hashsig_option_t;
GIT_EXTERN(int) git_hashsig_create(
	git_hashsig **out,
	const char *buf,
	size_t buflen,
	git_hashsig_option_t opts);
GIT_EXTERN(int) git_hashsig_create_fromfile(
	git_hashsig **out,
	const char *path,
	git_hashsig_option_t opts);
GIT_EXTERN(void) git_hashsig_free(git_hashsig *sig);
GIT_EXTERN(int) git_hashsig_compare(
	const git_hashsig *a,
	const git_hashsig *b);
GIT_END_DECL
#endif
