
#ifndef INCLUDE_git_revwalk_h__
#define INCLUDE_git_revwalk_h__
#include "common.h"
#include "types.h"
#include "oid.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_SORT_NONE = 0,
	GIT_SORT_TOPOLOGICAL = 1 << 0,
	GIT_SORT_TIME = 1 << 1,
	GIT_SORT_REVERSE = 1 << 2
} git_sort_t;
GIT_EXTERN(int) git_revwalk_new(git_revwalk **out, git_repository *repo);
GIT_EXTERN(int) git_revwalk_reset(git_revwalk *walker);
GIT_EXTERN(int) git_revwalk_push(git_revwalk *walk, const git_oid *id);
GIT_EXTERN(int) git_revwalk_push_glob(git_revwalk *walk, const char *glob);
GIT_EXTERN(int) git_revwalk_push_head(git_revwalk *walk);
GIT_EXTERN(int) git_revwalk_hide(git_revwalk *walk, const git_oid *commit_id);
GIT_EXTERN(int) git_revwalk_hide_glob(git_revwalk *walk, const char *glob);
GIT_EXTERN(int) git_revwalk_hide_head(git_revwalk *walk);
GIT_EXTERN(int) git_revwalk_push_ref(git_revwalk *walk, const char *refname);
GIT_EXTERN(int) git_revwalk_hide_ref(git_revwalk *walk, const char *refname);
GIT_EXTERN(int) git_revwalk_next(git_oid *out, git_revwalk *walk);
GIT_EXTERN(int) git_revwalk_sorting(git_revwalk *walk, unsigned int sort_mode);
GIT_EXTERN(int) git_revwalk_push_range(git_revwalk *walk, const char *range);
GIT_EXTERN(int) git_revwalk_simplify_first_parent(git_revwalk *walk);
GIT_EXTERN(void) git_revwalk_free(git_revwalk *walk);
GIT_EXTERN(git_repository *) git_revwalk_repository(git_revwalk *walk);
typedef int GIT_CALLBACK(git_revwalk_hide_cb)(
	const git_oid *commit_id,
	void *payload);
GIT_EXTERN(int) git_revwalk_add_hide_cb(
	git_revwalk *walk,
	git_revwalk_hide_cb hide_cb,
	void *payload);
GIT_END_DECL
#endif
