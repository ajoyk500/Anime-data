
#ifndef INCLUDE_git_commit_h__
#define INCLUDE_git_commit_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "object.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_commit_lookup(
	git_commit **commit, git_repository *repo, const git_oid *id);
GIT_EXTERN(int) git_commit_lookup_prefix(
	git_commit **commit, git_repository *repo, const git_oid *id, size_t len);
GIT_EXTERN(void) git_commit_free(git_commit *commit);
GIT_EXTERN(const git_oid *) git_commit_id(const git_commit *commit);
GIT_EXTERN(git_repository *) git_commit_owner(const git_commit *commit);
GIT_EXTERN(const char *) git_commit_message_encoding(const git_commit *commit);
GIT_EXTERN(const char *) git_commit_message(const git_commit *commit);
GIT_EXTERN(const char *) git_commit_message_raw(const git_commit *commit);
GIT_EXTERN(const char *) git_commit_summary(git_commit *commit);
GIT_EXTERN(const char *) git_commit_body(git_commit *commit);
GIT_EXTERN(git_time_t) git_commit_time(const git_commit *commit);
GIT_EXTERN(int) git_commit_time_offset(const git_commit *commit);
GIT_EXTERN(const git_signature *) git_commit_committer(const git_commit *commit);
GIT_EXTERN(const git_signature *) git_commit_author(const git_commit *commit);
GIT_EXTERN(int) git_commit_committer_with_mailmap(
	git_signature **out, const git_commit *commit, const git_mailmap *mailmap);
GIT_EXTERN(int) git_commit_author_with_mailmap(
	git_signature **out, const git_commit *commit, const git_mailmap *mailmap);
GIT_EXTERN(const char *) git_commit_raw_header(const git_commit *commit);
GIT_EXTERN(int) git_commit_tree(git_tree **tree_out, const git_commit *commit);
GIT_EXTERN(const git_oid *) git_commit_tree_id(const git_commit *commit);
GIT_EXTERN(unsigned int) git_commit_parentcount(const git_commit *commit);
GIT_EXTERN(int) git_commit_parent(
	git_commit **out,
	const git_commit *commit,
	unsigned int n);
GIT_EXTERN(const git_oid *) git_commit_parent_id(
	const git_commit *commit,
	unsigned int n);
GIT_EXTERN(int) git_commit_nth_gen_ancestor(
	git_commit **ancestor,
	const git_commit *commit,
	unsigned int n);
GIT_EXTERN(int) git_commit_header_field(git_buf *out, const git_commit *commit, const char *field);
GIT_EXTERN(int) git_commit_extract_signature(git_buf *signature, git_buf *signed_data, git_repository *repo, git_oid *commit_id, const char *field);
GIT_EXTERN(int) git_commit_create(
	git_oid *id,
	git_repository *repo,
	const char *update_ref,
	const git_signature *author,
	const git_signature *committer,
	const char *message_encoding,
	const char *message,
	const git_tree *tree,
	size_t parent_count,
	const git_commit *parents[]);
GIT_EXTERN(int) git_commit_create_v(
	git_oid *id,
	git_repository *repo,
	const char *update_ref,
	const git_signature *author,
	const git_signature *committer,
	const char *message_encoding,
	const char *message,
	const git_tree *tree,
	size_t parent_count,
	...);
typedef struct {
	unsigned int version;
	unsigned int allow_empty_commit : 1;
	const git_signature *author;
	const git_signature *committer;
	const char *message_encoding;
} git_commit_create_options;
#define GIT_COMMIT_CREATE_OPTIONS_VERSION 1
#define GIT_COMMIT_CREATE_OPTIONS_INIT { GIT_COMMIT_CREATE_OPTIONS_VERSION }
GIT_EXTERN(int) git_commit_create_from_stage(
	git_oid *id,
	git_repository *repo,
	const char *message,
	const git_commit_create_options *opts);
GIT_EXTERN(int) git_commit_amend(
	git_oid *id,
	const git_commit *commit_to_amend,
	const char *update_ref,
	const git_signature *author,
	const git_signature *committer,
	const char *message_encoding,
	const char *message,
	const git_tree *tree);
GIT_EXTERN(int) git_commit_create_buffer(
	git_buf *out,
	git_repository *repo,
	const git_signature *author,
	const git_signature *committer,
	const char *message_encoding,
	const char *message,
	const git_tree *tree,
	size_t parent_count,
	const git_commit *parents[]);
GIT_EXTERN(int) git_commit_create_with_signature(
	git_oid *out,
	git_repository *repo,
	const char *commit_content,
	const char *signature,
	const char *signature_field);
GIT_EXTERN(int) git_commit_dup(git_commit **out, git_commit *source);
typedef int (*git_commit_create_cb)(
	git_oid *out,
	const git_signature *author,
	const git_signature *committer,
	const char *message_encoding,
	const char *message,
	const git_tree *tree,
	size_t parent_count,
	const git_commit *parents[],
	void *payload);
typedef struct git_commitarray {
	git_commit *const *commits;
	size_t count;
} git_commitarray;
GIT_EXTERN(void) git_commitarray_dispose(git_commitarray *array);
GIT_END_DECL
#endif
