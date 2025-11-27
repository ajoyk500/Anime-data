
#ifndef INCLUDE_git_apply_h__
#define INCLUDE_git_apply_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "diff.h"
GIT_BEGIN_DECL
typedef int GIT_CALLBACK(git_apply_delta_cb)(
	const git_diff_delta *delta,
	void *payload);
typedef int GIT_CALLBACK(git_apply_hunk_cb)(
	const git_diff_hunk *hunk,
	void *payload);
typedef enum {
	GIT_APPLY_CHECK = (1 << 0)
} git_apply_flags_t;
typedef struct {
	unsigned int version; 
	git_apply_delta_cb delta_cb;
	git_apply_hunk_cb hunk_cb;
	void *payload;
	unsigned int flags;
} git_apply_options;
#define GIT_APPLY_OPTIONS_VERSION 1
#define GIT_APPLY_OPTIONS_INIT {GIT_APPLY_OPTIONS_VERSION}
GIT_EXTERN(int) git_apply_options_init(git_apply_options *opts, unsigned int version);
GIT_EXTERN(int) git_apply_to_tree(
	git_index **out,
	git_repository *repo,
	git_tree *preimage,
	git_diff *diff,
	const git_apply_options *options);
typedef enum {
	GIT_APPLY_LOCATION_WORKDIR = 0,
	GIT_APPLY_LOCATION_INDEX = 1,
	GIT_APPLY_LOCATION_BOTH = 2
} git_apply_location_t;
GIT_EXTERN(int) git_apply(
	git_repository *repo,
	git_diff *diff,
	git_apply_location_t location,
	const git_apply_options *options);
GIT_END_DECL
#endif
