
#ifndef INCLUDE_git_cherrypick_h__
#define INCLUDE_git_cherrypick_h__
#include "common.h"
#include "types.h"
#include "merge.h"
GIT_BEGIN_DECL
typedef struct {
	unsigned int version;
	unsigned int mainline;
	git_merge_options merge_opts; 
	git_checkout_options checkout_opts; 
} git_cherrypick_options;
#define GIT_CHERRYPICK_OPTIONS_VERSION 1
#define GIT_CHERRYPICK_OPTIONS_INIT { \
	GIT_CHERRYPICK_OPTIONS_VERSION, 0, \
	GIT_MERGE_OPTIONS_INIT, GIT_CHECKOUT_OPTIONS_INIT }
GIT_EXTERN(int) git_cherrypick_options_init(
	git_cherrypick_options *opts,
	unsigned int version);
GIT_EXTERN(int) git_cherrypick_commit(
	git_index **out,
	git_repository *repo,
	git_commit *cherrypick_commit,
	git_commit *our_commit,
	unsigned int mainline,
	const git_merge_options *merge_options);
GIT_EXTERN(int) git_cherrypick(
	git_repository *repo,
	git_commit *commit,
	const git_cherrypick_options *cherrypick_options);
GIT_END_DECL
#endif
