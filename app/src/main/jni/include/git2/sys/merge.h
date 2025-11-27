
#ifndef INCLUDE_sys_git_merge_h__
#define INCLUDE_sys_git_merge_h__
#include "git2/common.h"
#include "git2/types.h"
#include "git2/index.h"
#include "git2/merge.h"
GIT_BEGIN_DECL
typedef struct git_merge_driver git_merge_driver;
GIT_EXTERN(git_merge_driver *) git_merge_driver_lookup(const char *name);
#define GIT_MERGE_DRIVER_TEXT   "text"
#define GIT_MERGE_DRIVER_BINARY "binary"
#define GIT_MERGE_DRIVER_UNION  "union"
typedef struct git_merge_driver_source git_merge_driver_source;
GIT_EXTERN(git_repository *) git_merge_driver_source_repo(
	const git_merge_driver_source *src);
GIT_EXTERN(const git_index_entry *) git_merge_driver_source_ancestor(
	const git_merge_driver_source *src);
GIT_EXTERN(const git_index_entry *) git_merge_driver_source_ours(
	const git_merge_driver_source *src);
GIT_EXTERN(const git_index_entry *) git_merge_driver_source_theirs(
	const git_merge_driver_source *src);
GIT_EXTERN(const git_merge_file_options *) git_merge_driver_source_file_options(
	const git_merge_driver_source *src);
typedef int GIT_CALLBACK(git_merge_driver_init_fn)(git_merge_driver *self);
typedef void GIT_CALLBACK(git_merge_driver_shutdown_fn)(git_merge_driver *self);
typedef int GIT_CALLBACK(git_merge_driver_apply_fn)(
	git_merge_driver *self,
	const char **path_out,
	uint32_t *mode_out,
	git_buf *merged_out,
	const char *filter_name,
	const git_merge_driver_source *src);
struct git_merge_driver {
	unsigned int                 version;
	git_merge_driver_init_fn     initialize;
	git_merge_driver_shutdown_fn shutdown;
	git_merge_driver_apply_fn    apply;
};
#define GIT_MERGE_DRIVER_VERSION 1
GIT_EXTERN(int) git_merge_driver_register(
	const char *name, git_merge_driver *driver);
GIT_EXTERN(int) git_merge_driver_unregister(const char *name);
GIT_END_DECL
#endif
