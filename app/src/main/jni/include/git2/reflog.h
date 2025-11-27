
#ifndef INCLUDE_git_reflog_h__
#define INCLUDE_git_reflog_h__
#include "common.h"
#include "types.h"
#include "oid.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_reflog_read(git_reflog **out, git_repository *repo,  const char *name);
GIT_EXTERN(int) git_reflog_write(git_reflog *reflog);
GIT_EXTERN(int) git_reflog_append(git_reflog *reflog, const git_oid *id, const git_signature *committer, const char *msg);
GIT_EXTERN(int) git_reflog_rename(git_repository *repo, const char *old_name, const char *name);
GIT_EXTERN(int) git_reflog_delete(git_repository *repo, const char *name);
GIT_EXTERN(size_t) git_reflog_entrycount(git_reflog *reflog);
GIT_EXTERN(const git_reflog_entry *) git_reflog_entry_byindex(const git_reflog *reflog, size_t idx);
GIT_EXTERN(int) git_reflog_drop(
	git_reflog *reflog,
	size_t idx,
	int rewrite_previous_entry);
GIT_EXTERN(const git_oid *) git_reflog_entry_id_old(const git_reflog_entry *entry);
GIT_EXTERN(const git_oid *) git_reflog_entry_id_new(const git_reflog_entry *entry);
GIT_EXTERN(const git_signature *) git_reflog_entry_committer(const git_reflog_entry *entry);
GIT_EXTERN(const char *) git_reflog_entry_message(const git_reflog_entry *entry);
GIT_EXTERN(void) git_reflog_free(git_reflog *reflog);
GIT_END_DECL
#endif
