
#ifndef INCLUDE_sys_git_index_h__
#define INCLUDE_sys_git_index_h__
#include "git2/common.h"
#include "git2/types.h"
GIT_BEGIN_DECL
typedef struct git_index_name_entry {
	char *ancestor;
	char *ours;
	char *theirs;
} git_index_name_entry;
typedef struct git_index_reuc_entry {
	uint32_t mode[3];
	git_oid oid[3];
	char *path;
} git_index_reuc_entry;
GIT_EXTERN(size_t) git_index_name_entrycount(git_index *index);
GIT_EXTERN(const git_index_name_entry *) git_index_name_get_byindex(
	git_index *index, size_t n);
GIT_EXTERN(int) git_index_name_add(git_index *index,
	const char *ancestor, const char *ours, const char *theirs);
GIT_EXTERN(int) git_index_name_clear(git_index *index);
GIT_EXTERN(size_t) git_index_reuc_entrycount(git_index *index);
GIT_EXTERN(int) git_index_reuc_find(size_t *at_pos, git_index *index, const char *path);
GIT_EXTERN(const git_index_reuc_entry *) git_index_reuc_get_bypath(git_index *index, const char *path);
GIT_EXTERN(const git_index_reuc_entry *) git_index_reuc_get_byindex(git_index *index, size_t n);
GIT_EXTERN(int) git_index_reuc_add(git_index *index, const char *path,
	int ancestor_mode, const git_oid *ancestor_id,
	int our_mode, const git_oid *our_id,
	int their_mode, const git_oid *their_id);
GIT_EXTERN(int) git_index_reuc_remove(git_index *index, size_t n);
GIT_EXTERN(int) git_index_reuc_clear(git_index *index);
GIT_END_DECL
#endif
