
#ifndef INCLUDE_git_status_h__
#define INCLUDE_git_status_h__
#include "common.h"
#include "types.h"
#include "strarray.h"
#include "diff.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_STATUS_CURRENT = 0,
	GIT_STATUS_INDEX_NEW        = (1u << 0),
	GIT_STATUS_INDEX_MODIFIED   = (1u << 1),
	GIT_STATUS_INDEX_DELETED    = (1u << 2),
	GIT_STATUS_INDEX_RENAMED    = (1u << 3),
	GIT_STATUS_INDEX_TYPECHANGE = (1u << 4),
	GIT_STATUS_WT_NEW           = (1u << 7),
	GIT_STATUS_WT_MODIFIED      = (1u << 8),
	GIT_STATUS_WT_DELETED       = (1u << 9),
	GIT_STATUS_WT_TYPECHANGE    = (1u << 10),
	GIT_STATUS_WT_RENAMED       = (1u << 11),
	GIT_STATUS_WT_UNREADABLE    = (1u << 12),
	GIT_STATUS_IGNORED          = (1u << 14),
	GIT_STATUS_CONFLICTED       = (1u << 15)
} git_status_t;
typedef int GIT_CALLBACK(git_status_cb)(
	const char *path, unsigned int status_flags, void *payload);
typedef enum {
	GIT_STATUS_SHOW_INDEX_AND_WORKDIR = 0,
	GIT_STATUS_SHOW_INDEX_ONLY = 1,
	GIT_STATUS_SHOW_WORKDIR_ONLY = 2
} git_status_show_t;
typedef enum {
	GIT_STATUS_OPT_INCLUDE_UNTRACKED                = (1u << 0),
	GIT_STATUS_OPT_INCLUDE_IGNORED                  = (1u << 1),
	GIT_STATUS_OPT_INCLUDE_UNMODIFIED               = (1u << 2),
	GIT_STATUS_OPT_EXCLUDE_SUBMODULES               = (1u << 3),
	GIT_STATUS_OPT_RECURSE_UNTRACKED_DIRS           = (1u << 4),
	GIT_STATUS_OPT_DISABLE_PATHSPEC_MATCH           = (1u << 5),
	GIT_STATUS_OPT_RECURSE_IGNORED_DIRS             = (1u << 6),
	GIT_STATUS_OPT_RENAMES_HEAD_TO_INDEX            = (1u << 7),
	GIT_STATUS_OPT_RENAMES_INDEX_TO_WORKDIR         = (1u << 8),
	GIT_STATUS_OPT_SORT_CASE_SENSITIVELY            = (1u << 9),
	GIT_STATUS_OPT_SORT_CASE_INSENSITIVELY          = (1u << 10),
	GIT_STATUS_OPT_RENAMES_FROM_REWRITES            = (1u << 11),
	GIT_STATUS_OPT_NO_REFRESH                       = (1u << 12),
	GIT_STATUS_OPT_UPDATE_INDEX                     = (1u << 13),
	GIT_STATUS_OPT_INCLUDE_UNREADABLE               = (1u << 14),
	GIT_STATUS_OPT_INCLUDE_UNREADABLE_AS_UNTRACKED  = (1u << 15)
} git_status_opt_t;
#define GIT_STATUS_OPT_DEFAULTS \
	(GIT_STATUS_OPT_INCLUDE_IGNORED | \
	GIT_STATUS_OPT_INCLUDE_UNTRACKED | \
	GIT_STATUS_OPT_RECURSE_UNTRACKED_DIRS)
typedef struct {
	unsigned int version;
	git_status_show_t show;
	unsigned int      flags;
	git_strarray      pathspec;
	git_tree          *baseline;
	uint16_t          rename_threshold;
} git_status_options;
#define GIT_STATUS_OPTIONS_VERSION 1
#define GIT_STATUS_OPTIONS_INIT {GIT_STATUS_OPTIONS_VERSION}
GIT_EXTERN(int) git_status_options_init(
	git_status_options *opts,
	unsigned int version);
typedef struct {
	git_status_t status;
	git_diff_delta *head_to_index;
	git_diff_delta *index_to_workdir;
} git_status_entry;
GIT_EXTERN(int) git_status_foreach(
	git_repository *repo,
	git_status_cb callback,
	void *payload);
GIT_EXTERN(int) git_status_foreach_ext(
	git_repository *repo,
	const git_status_options *opts,
	git_status_cb callback,
	void *payload);
GIT_EXTERN(int) git_status_file(
	unsigned int *status_flags,
	git_repository *repo,
	const char *path);
GIT_EXTERN(int) git_status_list_new(
	git_status_list **out,
	git_repository *repo,
	const git_status_options *opts);
GIT_EXTERN(size_t) git_status_list_entrycount(
	git_status_list *statuslist);
GIT_EXTERN(const git_status_entry *) git_status_byindex(
	git_status_list *statuslist,
	size_t idx);
GIT_EXTERN(void) git_status_list_free(
	git_status_list *statuslist);
GIT_EXTERN(int) git_status_should_ignore(
	int *ignored,
	git_repository *repo,
	const char *path);
GIT_END_DECL
#endif
