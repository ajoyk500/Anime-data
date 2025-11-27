
#ifndef INCLUDE_git_repository_h__
#define INCLUDE_git_repository_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "odb.h"
#include "buffer.h"
#include "commit.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_repository_open(git_repository **out, const char *path);
GIT_EXTERN(int) git_repository_open_from_worktree(git_repository **out, git_worktree *wt);
GIT_EXTERN(int) git_repository_wrap_odb(
	git_repository **out,
	git_odb *odb);
GIT_EXTERN(int) git_repository_discover(
		git_buf *out,
		const char *start_path,
		int across_fs,
		const char *ceiling_dirs);
typedef enum {
	GIT_REPOSITORY_OPEN_NO_SEARCH = (1 << 0),
	GIT_REPOSITORY_OPEN_CROSS_FS  = (1 << 1),
	GIT_REPOSITORY_OPEN_BARE      = (1 << 2),
	GIT_REPOSITORY_OPEN_NO_DOTGIT = (1 << 3),
	GIT_REPOSITORY_OPEN_FROM_ENV  = (1 << 4)
} git_repository_open_flag_t;
GIT_EXTERN(int) git_repository_open_ext(
	git_repository **out,
	const char *path,
	unsigned int flags,
	const char *ceiling_dirs);
GIT_EXTERN(int) git_repository_open_bare(git_repository **out, const char *bare_path);
GIT_EXTERN(void) git_repository_free(git_repository *repo);
GIT_EXTERN(int) git_repository_init(
	git_repository **out,
	const char *path,
	unsigned is_bare);
typedef enum {
	GIT_REPOSITORY_INIT_BARE              = (1u << 0),
	GIT_REPOSITORY_INIT_NO_REINIT         = (1u << 1),
	GIT_REPOSITORY_INIT_NO_DOTGIT_DIR     = (1u << 2),
	GIT_REPOSITORY_INIT_MKDIR             = (1u << 3),
	GIT_REPOSITORY_INIT_MKPATH            = (1u << 4),
	GIT_REPOSITORY_INIT_EXTERNAL_TEMPLATE = (1u << 5),
	GIT_REPOSITORY_INIT_RELATIVE_GITLINK  = (1u << 6)
} git_repository_init_flag_t;
typedef enum {
	GIT_REPOSITORY_INIT_SHARED_UMASK = 0,
	GIT_REPOSITORY_INIT_SHARED_GROUP = 0002775,
	GIT_REPOSITORY_INIT_SHARED_ALL   = 0002777
} git_repository_init_mode_t;
typedef struct {
	unsigned int version;
	uint32_t    flags;
	uint32_t    mode;
	const char *workdir_path;
	const char *description;
	const char *template_path;
	const char *initial_head;
	const char *origin_url;
#ifdef GIT_EXPERIMENTAL_SHA256
	git_oid_t oid_type;
#endif
} git_repository_init_options;
#define GIT_REPOSITORY_INIT_OPTIONS_VERSION 1
#define GIT_REPOSITORY_INIT_OPTIONS_INIT {GIT_REPOSITORY_INIT_OPTIONS_VERSION}
GIT_EXTERN(int) git_repository_init_options_init(
	git_repository_init_options *opts,
	unsigned int version);
GIT_EXTERN(int) git_repository_init_ext(
	git_repository **out,
	const char *repo_path,
	git_repository_init_options *opts);
GIT_EXTERN(int) git_repository_head(git_reference **out, git_repository *repo);
GIT_EXTERN(int) git_repository_head_for_worktree(git_reference **out, git_repository *repo,
	const char *name);
GIT_EXTERN(int) git_repository_head_detached(git_repository *repo);
GIT_EXTERN(int) git_repository_head_detached_for_worktree(git_repository *repo,
	const char *name);
GIT_EXTERN(int) git_repository_head_unborn(git_repository *repo);
GIT_EXTERN(int) git_repository_is_empty(git_repository *repo);
typedef enum {
	GIT_REPOSITORY_ITEM_GITDIR,
	GIT_REPOSITORY_ITEM_WORKDIR,
	GIT_REPOSITORY_ITEM_COMMONDIR,
	GIT_REPOSITORY_ITEM_INDEX,
	GIT_REPOSITORY_ITEM_OBJECTS,
	GIT_REPOSITORY_ITEM_REFS,
	GIT_REPOSITORY_ITEM_PACKED_REFS,
	GIT_REPOSITORY_ITEM_REMOTES,
	GIT_REPOSITORY_ITEM_CONFIG,
	GIT_REPOSITORY_ITEM_INFO,
	GIT_REPOSITORY_ITEM_HOOKS,
	GIT_REPOSITORY_ITEM_LOGS,
	GIT_REPOSITORY_ITEM_MODULES,
	GIT_REPOSITORY_ITEM_WORKTREES,
	GIT_REPOSITORY_ITEM_WORKTREE_CONFIG,
	GIT_REPOSITORY_ITEM__LAST
} git_repository_item_t;
GIT_EXTERN(int) git_repository_item_path(git_buf *out, const git_repository *repo, git_repository_item_t item);
GIT_EXTERN(const char *) git_repository_path(const git_repository *repo);
GIT_EXTERN(const char *) git_repository_workdir(const git_repository *repo);
GIT_EXTERN(const char *) git_repository_commondir(const git_repository *repo);
GIT_EXTERN(int) git_repository_set_workdir(
	git_repository *repo, const char *workdir, int update_gitlink);
GIT_EXTERN(int) git_repository_is_bare(const git_repository *repo);
GIT_EXTERN(int) git_repository_is_worktree(const git_repository *repo);
GIT_EXTERN(int) git_repository_config(git_config **out, git_repository *repo);
GIT_EXTERN(int) git_repository_config_snapshot(git_config **out, git_repository *repo);
GIT_EXTERN(int) git_repository_odb(git_odb **out, git_repository *repo);
GIT_EXTERN(int) git_repository_refdb(git_refdb **out, git_repository *repo);
GIT_EXTERN(int) git_repository_index(git_index **out, git_repository *repo);
GIT_EXTERN(int) git_repository_message(git_buf *out, git_repository *repo);
GIT_EXTERN(int) git_repository_message_remove(git_repository *repo);
GIT_EXTERN(int) git_repository_state_cleanup(git_repository *repo);
typedef int GIT_CALLBACK(git_repository_fetchhead_foreach_cb)(const char *ref_name,
	const char *remote_url,
	const git_oid *oid,
	unsigned int is_merge,
	void *payload);
GIT_EXTERN(int) git_repository_fetchhead_foreach(
	git_repository *repo,
	git_repository_fetchhead_foreach_cb callback,
	void *payload);
typedef int GIT_CALLBACK(git_repository_mergehead_foreach_cb)(const git_oid *oid,
	void *payload);
GIT_EXTERN(int) git_repository_mergehead_foreach(
	git_repository *repo,
	git_repository_mergehead_foreach_cb callback,
	void *payload);
GIT_EXTERN(int) git_repository_hashfile(
	git_oid *out,
	git_repository *repo,
	const char *path,
	git_object_t type,
	const char *as_path);
GIT_EXTERN(int) git_repository_set_head(
	git_repository *repo,
	const char *refname);
GIT_EXTERN(int) git_repository_set_head_detached(
	git_repository *repo,
	const git_oid *committish);
GIT_EXTERN(int) git_repository_set_head_detached_from_annotated(
	git_repository *repo,
	const git_annotated_commit *committish);
GIT_EXTERN(int) git_repository_detach_head(
	git_repository *repo);
typedef enum {
	GIT_REPOSITORY_STATE_NONE,
	GIT_REPOSITORY_STATE_MERGE,
	GIT_REPOSITORY_STATE_REVERT,
	GIT_REPOSITORY_STATE_REVERT_SEQUENCE,
	GIT_REPOSITORY_STATE_CHERRYPICK,
	GIT_REPOSITORY_STATE_CHERRYPICK_SEQUENCE,
	GIT_REPOSITORY_STATE_BISECT,
	GIT_REPOSITORY_STATE_REBASE,
	GIT_REPOSITORY_STATE_REBASE_INTERACTIVE,
	GIT_REPOSITORY_STATE_REBASE_MERGE,
	GIT_REPOSITORY_STATE_APPLY_MAILBOX,
	GIT_REPOSITORY_STATE_APPLY_MAILBOX_OR_REBASE
} git_repository_state_t;
GIT_EXTERN(int) git_repository_state(git_repository *repo);
GIT_EXTERN(int) git_repository_set_namespace(git_repository *repo, const char *nmspace);
GIT_EXTERN(const char *) git_repository_get_namespace(git_repository *repo);
GIT_EXTERN(int) git_repository_is_shallow(git_repository *repo);
GIT_EXTERN(int) git_repository_ident(const char **name, const char **email, const git_repository *repo);
GIT_EXTERN(int) git_repository_set_ident(git_repository *repo, const char *name, const char *email);
GIT_EXTERN(git_oid_t) git_repository_oid_type(git_repository *repo);
GIT_EXTERN(int) git_repository_commit_parents(git_commitarray *commits, git_repository *repo);
GIT_END_DECL
#endif
