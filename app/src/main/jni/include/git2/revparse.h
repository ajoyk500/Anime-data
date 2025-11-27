
#ifndef INCLUDE_git_revparse_h__
#define INCLUDE_git_revparse_h__
#include "common.h"
#include "types.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_revparse_single(
	git_object **out, git_repository *repo, const char *spec);
GIT_EXTERN(int) git_revparse_ext(
	git_object **object_out,
	git_reference **reference_out,
	git_repository *repo,
	const char *spec);
typedef enum {
	GIT_REVSPEC_SINGLE         = 1 << 0,
	GIT_REVSPEC_RANGE          = 1 << 1,
	GIT_REVSPEC_MERGE_BASE     = 1 << 2
} git_revspec_t;
typedef struct {
	git_object *from;
	git_object *to;
	unsigned int flags;
} git_revspec;
GIT_EXTERN(int) git_revparse(
	git_revspec *revspec,
	git_repository *repo,
	const char *spec);
GIT_END_DECL
#endif
