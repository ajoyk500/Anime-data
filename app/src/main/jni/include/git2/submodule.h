
#ifndef INCLUDE_git_submodule_h__
#define INCLUDE_git_submodule_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "remote.h"
#include "checkout.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_SUBMODULE_STATUS_IN_HEAD           = (1u << 0),
	GIT_SUBMODULE_STATUS_IN_INDEX          = (1u << 1),
	GIT_SUBMODULE_STATUS_IN_CONFIG         = (1u << 2),
	GIT_SUBMODULE_STATUS_IN_WD             = (1u << 3),
	GIT_SUBMODULE_STATUS_INDEX_ADDED       = (1u << 4),
	GIT_SUBMODULE_STATUS_INDEX_DELETED     = (1u << 5),
	GIT_SUBMODULE_STATUS_INDEX_MODIFIED    = (1u << 6),
	GIT_SUBMODULE_STATUS_WD_UNINITIALIZED  = (1u << 7),
	GIT_SUBMODULE_STATUS_WD_ADDED          = (1u << 8),
	GIT_SUBMODULE_STATUS_WD_DELETED        = (1u << 9),
	GIT_SUBMODULE_STATUS_WD_MODIFIED       = (1u << 10),
	GIT_SUBMODULE_STATUS_WD_INDEX_MODIFIED = (1u << 11),
	GIT_SUBMODULE_STATUS_WD_WD_MODIFIED    = (1u << 12),
	GIT_SUBMODULE_STATUS_WD_UNTRACKED      = (1u << 13)
} git_submodule_status_t;
#define GIT_SUBMODULE_STATUS__IN_FLAGS		0x000Fu
#define GIT_SUBMODULE_STATUS__INDEX_FLAGS	0x0070u
#define GIT_SUBMODULE_STATUS__WD_FLAGS		0x3F80u
#define GIT_SUBMODULE_STATUS_IS_UNMODIFIED(S) \
	(((S) & ~GIT_SUBMODULE_STATUS__IN_FLAGS) == 0)
#define GIT_SUBMODULE_STATUS_IS_INDEX_UNMODIFIED(S) \
	(((S) & GIT_SUBMODULE_STATUS__INDEX_FLAGS) == 0)
#define GIT_SUBMODULE_STATUS_IS_WD_UNMODIFIED(S) \
	(((S) & (GIT_SUBMODULE_STATUS__WD_FLAGS & \
	~GIT_SUBMODULE_STATUS_WD_UNINITIALIZED)) == 0)
#define GIT_SUBMODULE_STATUS_IS_WD_DIRTY(S) \
	(((S) & (GIT_SUBMODULE_STATUS_WD_INDEX_MODIFIED | \
	GIT_SUBMODULE_STATUS_WD_WD_MODIFIED | \
	GIT_SUBMODULE_STATUS_WD_UNTRACKED)) != 0)
typedef int GIT_CALLBACK(git_submodule_cb)(
	git_submodule *sm, const char *name, void *payload);
typedef struct git_submodule_update_options {
	unsigned int version;
	git_checkout_options checkout_opts;
	git_fetch_options fetch_opts;
	int allow_fetch;
} git_submodule_update_options;
#define GIT_SUBMODULE_UPDATE_OPTIONS_VERSION 1
#define GIT_SUBMODULE_UPDATE_OPTIONS_INIT \
	{ GIT_SUBMODULE_UPDATE_OPTIONS_VERSION, \
	  GIT_CHECKOUT_OPTIONS_INIT, \
	  GIT_FETCH_OPTIONS_INIT, \
	  1 }
GIT_EXTERN(int) git_submodule_update_options_init(
	git_submodule_update_options *opts, unsigned int version);
GIT_EXTERN(int) git_submodule_update(git_submodule *submodule, int init, git_submodule_update_options *options);
GIT_EXTERN(int) git_submodule_lookup(
	git_submodule **out,
	git_repository *repo,
	const char *name);
GIT_EXTERN(int) git_submodule_dup(git_submodule **out, git_submodule *source);
GIT_EXTERN(void) git_submodule_free(git_submodule *submodule);
GIT_EXTERN(int) git_submodule_foreach(
	git_repository *repo,
	git_submodule_cb callback,
	void *payload);
GIT_EXTERN(int) git_submodule_add_setup(
	git_submodule **out,
	git_repository *repo,
	const char *url,
	const char *path,
	int use_gitlink);
GIT_EXTERN(int) git_submodule_clone(
	git_repository **out,
	git_submodule *submodule,
	const git_submodule_update_options *opts);
GIT_EXTERN(int) git_submodule_add_finalize(git_submodule *submodule);
GIT_EXTERN(int) git_submodule_add_to_index(
	git_submodule *submodule,
	int write_index);
GIT_EXTERN(git_repository *) git_submodule_owner(git_submodule *submodule);
GIT_EXTERN(const char *) git_submodule_name(git_submodule *submodule);
GIT_EXTERN(const char *) git_submodule_path(git_submodule *submodule);
GIT_EXTERN(const char *) git_submodule_url(git_submodule *submodule);
GIT_EXTERN(int) git_submodule_resolve_url(git_buf *out, git_repository *repo, const char *url);
GIT_EXTERN(const char *) git_submodule_branch(git_submodule *submodule);
GIT_EXTERN(int) git_submodule_set_branch(git_repository *repo, const char *name, const char *branch);
GIT_EXTERN(int) git_submodule_set_url(git_repository *repo, const char *name, const char *url);
GIT_EXTERN(const git_oid *) git_submodule_index_id(git_submodule *submodule);
GIT_EXTERN(const git_oid *) git_submodule_head_id(git_submodule *submodule);
GIT_EXTERN(const git_oid *) git_submodule_wd_id(git_submodule *submodule);
GIT_EXTERN(git_submodule_ignore_t) git_submodule_ignore(
	git_submodule *submodule);
GIT_EXTERN(int) git_submodule_set_ignore(
	git_repository *repo,
	const char *name,
	git_submodule_ignore_t ignore);
GIT_EXTERN(git_submodule_update_t) git_submodule_update_strategy(
	git_submodule *submodule);
GIT_EXTERN(int) git_submodule_set_update(
	git_repository *repo,
	const char *name,
	git_submodule_update_t update);
GIT_EXTERN(git_submodule_recurse_t) git_submodule_fetch_recurse_submodules(
	git_submodule *submodule);
GIT_EXTERN(int) git_submodule_set_fetch_recurse_submodules(
	git_repository *repo,
	const char *name,
	git_submodule_recurse_t fetch_recurse_submodules);
GIT_EXTERN(int) git_submodule_init(git_submodule *submodule, int overwrite);
GIT_EXTERN(int) git_submodule_repo_init(
	git_repository **out,
	const git_submodule *sm,
	int use_gitlink);
GIT_EXTERN(int) git_submodule_sync(git_submodule *submodule);
GIT_EXTERN(int) git_submodule_open(
	git_repository **repo,
	git_submodule *submodule);
GIT_EXTERN(int) git_submodule_reload(git_submodule *submodule, int force);
GIT_EXTERN(int) git_submodule_status(
	unsigned int *status,
	git_repository *repo,
	const char *name,
	git_submodule_ignore_t ignore);
GIT_EXTERN(int) git_submodule_location(
	unsigned int *location_status,
	git_submodule *submodule);
GIT_END_DECL
#endif
