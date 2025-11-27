
#ifndef INCLUDE_git_blob_h__
#define INCLUDE_git_blob_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "object.h"
#include "buffer.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_blob_lookup(
	git_blob **blob,
	git_repository *repo,
	const git_oid *id);
GIT_EXTERN(int) git_blob_lookup_prefix(git_blob **blob, git_repository *repo, const git_oid *id, size_t len);
GIT_EXTERN(void) git_blob_free(git_blob *blob);
GIT_EXTERN(const git_oid *) git_blob_id(const git_blob *blob);
GIT_EXTERN(git_repository *) git_blob_owner(const git_blob *blob);
GIT_EXTERN(const void *) git_blob_rawcontent(const git_blob *blob);
GIT_EXTERN(git_object_size_t) git_blob_rawsize(const git_blob *blob);
typedef enum {
	GIT_BLOB_FILTER_CHECK_FOR_BINARY = (1 << 0),
	GIT_BLOB_FILTER_NO_SYSTEM_ATTRIBUTES = (1 << 1),
	GIT_BLOB_FILTER_ATTRIBUTES_FROM_HEAD = (1 << 2),
	GIT_BLOB_FILTER_ATTRIBUTES_FROM_COMMIT = (1 << 3)
} git_blob_filter_flag_t;
typedef struct {
	int version;
	uint32_t flags;
#ifdef GIT_DEPRECATE_HARD
	void *reserved;
#else
	git_oid *commit_id;
#endif
	git_oid attr_commit_id;
} git_blob_filter_options;
#define GIT_BLOB_FILTER_OPTIONS_VERSION 1
#define GIT_BLOB_FILTER_OPTIONS_INIT { \
		GIT_BLOB_FILTER_OPTIONS_VERSION, \
		GIT_BLOB_FILTER_CHECK_FOR_BINARY \
	}
GIT_EXTERN(int) git_blob_filter_options_init(
	git_blob_filter_options *opts,
	unsigned int version);
GIT_EXTERN(int) git_blob_filter(
	git_buf *out,
	git_blob *blob,
	const char *as_path,
	git_blob_filter_options *opts);
GIT_EXTERN(int) git_blob_create_from_workdir(git_oid *id, git_repository *repo, const char *relative_path);
GIT_EXTERN(int) git_blob_create_from_disk(
	git_oid *id,
	git_repository *repo,
	const char *path);
GIT_EXTERN(int) git_blob_create_from_stream(
	git_writestream **out,
	git_repository *repo,
	const char *hintpath);
GIT_EXTERN(int) git_blob_create_from_stream_commit(
	git_oid *out,
	git_writestream *stream);
GIT_EXTERN(int) git_blob_create_from_buffer(
	git_oid *id, git_repository *repo, const void *buffer, size_t len);
GIT_EXTERN(int) git_blob_is_binary(const git_blob *blob);
GIT_EXTERN(int) git_blob_data_is_binary(const char *data, size_t len);
GIT_EXTERN(int) git_blob_dup(git_blob **out, git_blob *source);
GIT_END_DECL
#endif
