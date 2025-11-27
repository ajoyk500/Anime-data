
#ifndef INCLUDE_git_checkout_h__
#define INCLUDE_git_checkout_h__
#include "common.h"
#include "types.h"
#include "diff.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_CHECKOUT_SAFE = 0,
	GIT_CHECKOUT_FORCE = (1u << 1),
	GIT_CHECKOUT_RECREATE_MISSING = (1u << 2),
	GIT_CHECKOUT_ALLOW_CONFLICTS = (1u << 4),
	GIT_CHECKOUT_REMOVE_UNTRACKED = (1u << 5),
	GIT_CHECKOUT_REMOVE_IGNORED = (1u << 6),
	GIT_CHECKOUT_UPDATE_ONLY = (1u << 7),
	GIT_CHECKOUT_DONT_UPDATE_INDEX = (1u << 8),
	GIT_CHECKOUT_NO_REFRESH = (1u << 9),
	GIT_CHECKOUT_SKIP_UNMERGED = (1u << 10),
	GIT_CHECKOUT_USE_OURS = (1u << 11),
	GIT_CHECKOUT_USE_THEIRS = (1u << 12),
	GIT_CHECKOUT_DISABLE_PATHSPEC_MATCH = (1u << 13),
	GIT_CHECKOUT_SKIP_LOCKED_DIRECTORIES = (1u << 18),
	GIT_CHECKOUT_DONT_OVERWRITE_IGNORED = (1u << 19),
	GIT_CHECKOUT_CONFLICT_STYLE_MERGE = (1u << 20),
	GIT_CHECKOUT_CONFLICT_STYLE_DIFF3 = (1u << 21),
	GIT_CHECKOUT_DONT_REMOVE_EXISTING = (1u << 22),
	GIT_CHECKOUT_DONT_WRITE_INDEX = (1u << 23),
	GIT_CHECKOUT_DRY_RUN = (1u << 24),
	GIT_CHECKOUT_CONFLICT_STYLE_ZDIFF3 = (1u << 25),
	GIT_CHECKOUT_NONE = (1u << 30),
	GIT_CHECKOUT_UPDATE_SUBMODULES = (1u << 16),
	GIT_CHECKOUT_UPDATE_SUBMODULES_IF_CHANGED = (1u << 17)
} git_checkout_strategy_t;
typedef enum {
	GIT_CHECKOUT_NOTIFY_NONE      = 0,
	GIT_CHECKOUT_NOTIFY_CONFLICT  = (1u << 0),
	GIT_CHECKOUT_NOTIFY_DIRTY     = (1u << 1),
	GIT_CHECKOUT_NOTIFY_UPDATED   = (1u << 2),
	GIT_CHECKOUT_NOTIFY_UNTRACKED = (1u << 3),
	GIT_CHECKOUT_NOTIFY_IGNORED   = (1u << 4),
	GIT_CHECKOUT_NOTIFY_ALL       = 0x0FFFFu
} git_checkout_notify_t;
typedef struct {
	size_t mkdir_calls;
	size_t stat_calls;
	size_t chmod_calls;
} git_checkout_perfdata;
typedef int GIT_CALLBACK(git_checkout_notify_cb)(
	git_checkout_notify_t why,
	const char *path,
	const git_diff_file *baseline,
	const git_diff_file *target,
	const git_diff_file *workdir,
	void *payload);
typedef void GIT_CALLBACK(git_checkout_progress_cb)(
	const char *path,
	size_t completed_steps,
	size_t total_steps,
	void *payload);
typedef void GIT_CALLBACK(git_checkout_perfdata_cb)(
	const git_checkout_perfdata *perfdata,
	void *payload);
typedef struct git_checkout_options {
	unsigned int version; 
	unsigned int checkout_strategy; 
	int disable_filters;    
	unsigned int dir_mode;  
	unsigned int file_mode; 
	int file_open_flags;    
	unsigned int notify_flags;
	git_checkout_notify_cb notify_cb;
	void *notify_payload;
	git_checkout_progress_cb progress_cb;
	void *progress_payload;
	git_strarray paths;
	git_tree *baseline;
	git_index *baseline_index;
	const char *target_directory; 
	const char *ancestor_label; 
	const char *our_label; 
	const char *their_label; 
	git_checkout_perfdata_cb perfdata_cb;
	void *perfdata_payload;
} git_checkout_options;
#define GIT_CHECKOUT_OPTIONS_VERSION 1
#define GIT_CHECKOUT_OPTIONS_INIT { GIT_CHECKOUT_OPTIONS_VERSION }
GIT_EXTERN(int) git_checkout_options_init(
	git_checkout_options *opts,
	unsigned int version);
GIT_EXTERN(int) git_checkout_head(
	git_repository *repo,
	const git_checkout_options *opts);
GIT_EXTERN(int) git_checkout_index(
	git_repository *repo,
	git_index *index,
	const git_checkout_options *opts);
GIT_EXTERN(int) git_checkout_tree(
	git_repository *repo,
	const git_object *treeish,
	const git_checkout_options *opts);
GIT_END_DECL
#endif
