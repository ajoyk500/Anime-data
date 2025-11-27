
#ifndef INCLUDE_git_filter_h__
#define INCLUDE_git_filter_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "buffer.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_FILTER_TO_WORKTREE = 0,
	GIT_FILTER_SMUDGE = GIT_FILTER_TO_WORKTREE,
	GIT_FILTER_TO_ODB = 1,
	GIT_FILTER_CLEAN = GIT_FILTER_TO_ODB
} git_filter_mode_t;
typedef enum {
	GIT_FILTER_DEFAULT = 0u,
	GIT_FILTER_ALLOW_UNSAFE = (1u << 0),
	GIT_FILTER_NO_SYSTEM_ATTRIBUTES = (1u << 1),
	GIT_FILTER_ATTRIBUTES_FROM_HEAD = (1u << 2),
	GIT_FILTER_ATTRIBUTES_FROM_COMMIT = (1u << 3)
} git_filter_flag_t;
typedef struct {
	unsigned int version;
	uint32_t flags;
#ifdef GIT_DEPRECATE_HARD
	void *reserved;
#else
	git_oid *commit_id;
#endif
	git_oid attr_commit_id;
} git_filter_options;
#define GIT_FILTER_OPTIONS_VERSION 1
#define GIT_FILTER_OPTIONS_INIT {GIT_FILTER_OPTIONS_VERSION}
typedef struct git_filter git_filter;
typedef struct git_filter_list git_filter_list;
GIT_EXTERN(int) git_filter_list_load(
	git_filter_list **filters,
	git_repository *repo,
	git_blob *blob, 
	const char *path,
	git_filter_mode_t mode,
	uint32_t flags);
GIT_EXTERN(int) git_filter_list_load_ext(
	git_filter_list **filters,
	git_repository *repo,
	git_blob *blob,
	const char *path,
	git_filter_mode_t mode,
	git_filter_options *opts);
GIT_EXTERN(int) git_filter_list_contains(
	git_filter_list *filters,
	const char *name);
GIT_EXTERN(int) git_filter_list_apply_to_buffer(
	git_buf *out,
	git_filter_list *filters,
	const char *in,
	size_t in_len);
GIT_EXTERN(int) git_filter_list_apply_to_file(
	git_buf *out,
	git_filter_list *filters,
	git_repository *repo,
	const char *path);
GIT_EXTERN(int) git_filter_list_apply_to_blob(
	git_buf *out,
	git_filter_list *filters,
	git_blob *blob);
GIT_EXTERN(int) git_filter_list_stream_buffer(
	git_filter_list *filters,
	const char *buffer,
	size_t len,
	git_writestream *target);
GIT_EXTERN(int) git_filter_list_stream_file(
	git_filter_list *filters,
	git_repository *repo,
	const char *path,
	git_writestream *target);
GIT_EXTERN(int) git_filter_list_stream_blob(
	git_filter_list *filters,
	git_blob *blob,
	git_writestream *target);
GIT_EXTERN(void) git_filter_list_free(git_filter_list *filters);
GIT_END_DECL
#endif
