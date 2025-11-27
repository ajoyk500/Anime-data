
#ifndef INCLUDE_git_pathspec_h__
#define INCLUDE_git_pathspec_h__
#include "common.h"
#include "types.h"
#include "strarray.h"
#include "diff.h"
GIT_BEGIN_DECL
typedef struct git_pathspec git_pathspec;
typedef struct git_pathspec_match_list git_pathspec_match_list;
typedef enum {
	GIT_PATHSPEC_DEFAULT        = 0,
	GIT_PATHSPEC_IGNORE_CASE    = (1u << 0),
	GIT_PATHSPEC_USE_CASE       = (1u << 1),
	GIT_PATHSPEC_NO_GLOB        = (1u << 2),
	GIT_PATHSPEC_NO_MATCH_ERROR = (1u << 3),
	GIT_PATHSPEC_FIND_FAILURES  = (1u << 4),
	GIT_PATHSPEC_FAILURES_ONLY  = (1u << 5)
} git_pathspec_flag_t;
GIT_EXTERN(int) git_pathspec_new(
	git_pathspec **out, const git_strarray *pathspec);
GIT_EXTERN(void) git_pathspec_free(git_pathspec *ps);
GIT_EXTERN(int) git_pathspec_matches_path(
	const git_pathspec *ps, uint32_t flags, const char *path);
GIT_EXTERN(int) git_pathspec_match_workdir(
	git_pathspec_match_list **out,
	git_repository *repo,
	uint32_t flags,
	git_pathspec *ps);
GIT_EXTERN(int) git_pathspec_match_index(
	git_pathspec_match_list **out,
	git_index *index,
	uint32_t flags,
	git_pathspec *ps);
GIT_EXTERN(int) git_pathspec_match_tree(
	git_pathspec_match_list **out,
	git_tree *tree,
	uint32_t flags,
	git_pathspec *ps);
GIT_EXTERN(int) git_pathspec_match_diff(
	git_pathspec_match_list **out,
	git_diff *diff,
	uint32_t flags,
	git_pathspec *ps);
GIT_EXTERN(void) git_pathspec_match_list_free(git_pathspec_match_list *m);
GIT_EXTERN(size_t) git_pathspec_match_list_entrycount(
	const git_pathspec_match_list *m);
GIT_EXTERN(const char *) git_pathspec_match_list_entry(
	const git_pathspec_match_list *m, size_t pos);
GIT_EXTERN(const git_diff_delta *) git_pathspec_match_list_diff_entry(
	const git_pathspec_match_list *m, size_t pos);
GIT_EXTERN(size_t) git_pathspec_match_list_failed_entrycount(
	const git_pathspec_match_list *m);
GIT_EXTERN(const char *) git_pathspec_match_list_failed_entry(
	const git_pathspec_match_list *m, size_t pos);
GIT_END_DECL
#endif
