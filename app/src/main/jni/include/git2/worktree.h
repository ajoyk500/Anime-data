
#ifndef INCLUDE_git_worktree_h__
#define INCLUDE_git_worktree_h__
#include "common.h"
#include "buffer.h"
#include "types.h"
#include "strarray.h"
#include "checkout.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_worktree_list(git_strarray *out, git_repository *repo);
GIT_EXTERN(int) git_worktree_lookup(git_worktree **out, git_repository *repo, const char *name);
GIT_EXTERN(int) git_worktree_open_from_repository(git_worktree **out, git_repository *repo);
GIT_EXTERN(void) git_worktree_free(git_worktree *wt);
GIT_EXTERN(int) git_worktree_validate(const git_worktree *wt);
typedef struct git_worktree_add_options {
	unsigned int version;
	int lock;		
	int checkout_existing;	
	git_reference *ref;	
	git_checkout_options checkout_options;
} git_worktree_add_options;
#define GIT_WORKTREE_ADD_OPTIONS_VERSION 1
#define GIT_WORKTREE_ADD_OPTIONS_INIT { GIT_WORKTREE_ADD_OPTIONS_VERSION, \
	0, 0, NULL, GIT_CHECKOUT_OPTIONS_INIT }
GIT_EXTERN(int) git_worktree_add_options_init(git_worktree_add_options *opts,
	unsigned int version);
GIT_EXTERN(int) git_worktree_add(git_worktree **out, git_repository *repo,
	const char *name, const char *path,
	const git_worktree_add_options *opts);
GIT_EXTERN(int) git_worktree_lock(git_worktree *wt, const char *reason);
GIT_EXTERN(int) git_worktree_unlock(git_worktree *wt);
GIT_EXTERN(int) git_worktree_is_locked(git_buf *reason, const git_worktree *wt);
GIT_EXTERN(const char *) git_worktree_name(const git_worktree *wt);
GIT_EXTERN(const char *) git_worktree_path(const git_worktree *wt);
typedef enum {
	GIT_WORKTREE_PRUNE_VALID = 1u << 0,
	GIT_WORKTREE_PRUNE_LOCKED = 1u << 1,
	GIT_WORKTREE_PRUNE_WORKING_TREE = 1u << 2
} git_worktree_prune_t;
typedef struct git_worktree_prune_options {
	unsigned int version;
	uint32_t flags;
} git_worktree_prune_options;
#define GIT_WORKTREE_PRUNE_OPTIONS_VERSION 1
#define GIT_WORKTREE_PRUNE_OPTIONS_INIT {GIT_WORKTREE_PRUNE_OPTIONS_VERSION,0}
GIT_EXTERN(int) git_worktree_prune_options_init(
	git_worktree_prune_options *opts,
	unsigned int version);
GIT_EXTERN(int) git_worktree_is_prunable(git_worktree *wt,
	git_worktree_prune_options *opts);
GIT_EXTERN(int) git_worktree_prune(git_worktree *wt,
	git_worktree_prune_options *opts);
GIT_END_DECL
#endif
