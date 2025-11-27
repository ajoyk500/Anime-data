
#ifndef INCLUDE_git_annotated_commit_h__
#define INCLUDE_git_annotated_commit_h__
#include "common.h"
#include "repository.h"
#include "types.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_annotated_commit_from_ref(
	git_annotated_commit **out,
	git_repository *repo,
	const git_reference *ref);
GIT_EXTERN(int) git_annotated_commit_from_fetchhead(
	git_annotated_commit **out,
	git_repository *repo,
	const char *branch_name,
	const char *remote_url,
	const git_oid *id);
GIT_EXTERN(int) git_annotated_commit_lookup(
	git_annotated_commit **out,
	git_repository *repo,
	const git_oid *id);
GIT_EXTERN(int) git_annotated_commit_from_revspec(
	git_annotated_commit **out,
	git_repository *repo,
	const char *revspec);
GIT_EXTERN(const git_oid *) git_annotated_commit_id(
	const git_annotated_commit *commit);
GIT_EXTERN(const char *) git_annotated_commit_ref(
	const git_annotated_commit *commit);
GIT_EXTERN(void) git_annotated_commit_free(
	git_annotated_commit *commit);
GIT_END_DECL
#endif
