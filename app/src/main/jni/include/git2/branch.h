
#ifndef INCLUDE_git_branch_h__
#define INCLUDE_git_branch_h__
#include "common.h"
#include "oid.h"
#include "types.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_branch_create(
	git_reference **out,
	git_repository *repo,
	const char *branch_name,
	const git_commit *target,
	int force);
GIT_EXTERN(int) git_branch_create_from_annotated(
	git_reference **ref_out,
	git_repository *repo,
	const char *branch_name,
	const git_annotated_commit *target,
	int force);
GIT_EXTERN(int) git_branch_delete(git_reference *branch);
typedef struct git_branch_iterator git_branch_iterator;
GIT_EXTERN(int) git_branch_iterator_new(
	git_branch_iterator **out,
	git_repository *repo,
	git_branch_t list_flags);
GIT_EXTERN(int) git_branch_next(git_reference **out, git_branch_t *out_type, git_branch_iterator *iter);
GIT_EXTERN(void) git_branch_iterator_free(git_branch_iterator *iter);
GIT_EXTERN(int) git_branch_move(
	git_reference **out,
	git_reference *branch,
	const char *new_branch_name,
	int force);
GIT_EXTERN(int) git_branch_lookup(
	git_reference **out,
	git_repository *repo,
	const char *branch_name,
	git_branch_t branch_type);
GIT_EXTERN(int) git_branch_name(
		const char **out,
		const git_reference *ref);
GIT_EXTERN(int) git_branch_upstream(
	git_reference **out,
	const git_reference *branch);
GIT_EXTERN(int) git_branch_set_upstream(
	git_reference *branch,
	const char *branch_name);
GIT_EXTERN(int) git_branch_upstream_name(
	git_buf *out,
	git_repository *repo,
	const char *refname);
GIT_EXTERN(int) git_branch_is_head(
	const git_reference *branch);
GIT_EXTERN(int) git_branch_is_checked_out(
	const git_reference *branch);
GIT_EXTERN(int) git_branch_remote_name(
	git_buf *out,
	git_repository *repo,
	const char *refname);
 GIT_EXTERN(int) git_branch_upstream_remote(git_buf *buf, git_repository *repo, const char *refname);
 GIT_EXTERN(int) git_branch_upstream_merge(git_buf *buf, git_repository *repo, const char *refname);
GIT_EXTERN(int) git_branch_name_is_valid(int *valid, const char *name);
GIT_END_DECL
#endif
