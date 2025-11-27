
#ifndef INCLUDE_git_graph_h__
#define INCLUDE_git_graph_h__
#include "common.h"
#include "types.h"
#include "oid.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_graph_ahead_behind(size_t *ahead, size_t *behind, git_repository *repo, const git_oid *local, const git_oid *upstream);
GIT_EXTERN(int) git_graph_descendant_of(
	git_repository *repo,
	const git_oid *commit,
	const git_oid *ancestor);
GIT_EXTERN(int) git_graph_reachable_from_any(
	git_repository *repo,
	const git_oid *commit,
	const git_oid descendant_array[],
	size_t length);
GIT_END_DECL
#endif
