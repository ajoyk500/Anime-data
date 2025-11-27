
#ifndef INCLUDE_git_note_h__
#define INCLUDE_git_note_h__
#include "oid.h"
GIT_BEGIN_DECL
typedef int GIT_CALLBACK(git_note_foreach_cb)(
	const git_oid *blob_id,
	const git_oid *annotated_object_id,
	void *payload);
typedef struct git_iterator git_note_iterator;
GIT_EXTERN(int) git_note_iterator_new(
	git_note_iterator **out,
	git_repository *repo,
	const char *notes_ref);
GIT_EXTERN(int) git_note_commit_iterator_new(
	git_note_iterator **out,
	git_commit *notes_commit);
GIT_EXTERN(void) git_note_iterator_free(git_note_iterator *it);
GIT_EXTERN(int) git_note_next(
	git_oid *note_id,
	git_oid *annotated_id,
	git_note_iterator *it);
GIT_EXTERN(int) git_note_read(
	git_note **out,
	git_repository *repo,
	const char *notes_ref,
	const git_oid *oid);
GIT_EXTERN(int) git_note_commit_read(
	git_note **out,
	git_repository *repo,
	git_commit *notes_commit,
	const git_oid *oid);
GIT_EXTERN(const git_signature *) git_note_author(const git_note *note);
GIT_EXTERN(const git_signature *) git_note_committer(const git_note *note);
GIT_EXTERN(const char *) git_note_message(const git_note *note);
GIT_EXTERN(const git_oid *) git_note_id(const git_note *note);
GIT_EXTERN(int) git_note_create(
	git_oid *out,
	git_repository *repo,
	const char *notes_ref,
	const git_signature *author,
	const git_signature *committer,
	const git_oid *oid,
	const char *note,
	int force);
GIT_EXTERN(int) git_note_commit_create(
	git_oid *notes_commit_out,
	git_oid *notes_blob_out,
	git_repository *repo,
	git_commit *parent,
	const git_signature *author,
	const git_signature *committer,
	const git_oid *oid,
	const char *note,
	int allow_note_overwrite);
GIT_EXTERN(int) git_note_remove(
	git_repository *repo,
	const char *notes_ref,
	const git_signature *author,
	const git_signature *committer,
	const git_oid *oid);
GIT_EXTERN(int) git_note_commit_remove(
		git_oid *notes_commit_out,
		git_repository *repo,
		git_commit *notes_commit,
		const git_signature *author,
		const git_signature *committer,
		const git_oid *oid);
GIT_EXTERN(void) git_note_free(git_note *note);
GIT_EXTERN(int) git_note_default_ref(git_buf *out, git_repository *repo);
GIT_EXTERN(int) git_note_foreach(
	git_repository *repo,
	const char *notes_ref,
	git_note_foreach_cb note_cb,
	void *payload);
GIT_END_DECL
#endif
