
#ifndef INCLUDE_git_ignore_h__
#define INCLUDE_git_ignore_h__
#include "common.h"
#include "types.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_ignore_add_rule(
	git_repository *repo,
	const char *rules);
GIT_EXTERN(int) git_ignore_clear_internal_rules(
	git_repository *repo);
GIT_EXTERN(int) git_ignore_path_is_ignored(
	int *ignored,
	git_repository *repo,
	const char *path);
GIT_END_DECL
#endif
