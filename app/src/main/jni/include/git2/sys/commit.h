
#ifndef INCLUDE_sys_git_commit_h__
#define INCLUDE_sys_git_commit_h__
#include "git2/common.h"
#include "git2/types.h"
#include "git2/oid.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_commit_create_from_ids(
	git_oid *id,
	git_repository *repo,
	const char *update_ref,
	const git_signature *author,
	const git_signature *committer,
	const char *message_encoding,
	const char *message,
	const git_oid *tree,
	size_t parent_count,
	const git_oid *parents[]);
typedef const git_oid * GIT_CALLBACK(git_commit_parent_callback)(size_t idx, void *payload);
GIT_EXTERN(int) git_commit_create_from_callback(
	git_oid *id,
	git_repository *repo,
	const char *update_ref,
	const git_signature *author,
	const git_signature *committer,
	const char *message_encoding,
	const char *message,
	const git_oid *tree,
	git_commit_parent_callback parent_cb,
	void *parent_payload);
GIT_END_DECL
#endif
