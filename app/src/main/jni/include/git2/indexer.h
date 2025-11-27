
#ifndef INCLUDE_git_indexer_h__
#define INCLUDE_git_indexer_h__
#include "common.h"
#include "types.h"
#include "oid.h"
GIT_BEGIN_DECL
typedef struct git_indexer git_indexer;
typedef struct git_indexer_progress {
	unsigned int total_objects;
	unsigned int indexed_objects;
	unsigned int received_objects;
	unsigned int local_objects;
	unsigned int total_deltas;
	unsigned int indexed_deltas;
	size_t received_bytes;
} git_indexer_progress;
typedef int GIT_CALLBACK(git_indexer_progress_cb)(const git_indexer_progress *stats, void *payload);
typedef struct git_indexer_options {
	unsigned int version;
#ifdef GIT_EXPERIMENTAL_SHA256
	unsigned int mode;
	git_oid_t oid_type;
	git_odb *odb;
#endif
	git_indexer_progress_cb progress_cb;
	void *progress_cb_payload;
	unsigned char verify;
} git_indexer_options;
#define GIT_INDEXER_OPTIONS_VERSION 1
#define GIT_INDEXER_OPTIONS_INIT { GIT_INDEXER_OPTIONS_VERSION }
GIT_EXTERN(int) git_indexer_options_init(
	git_indexer_options *opts,
	unsigned int version);
#ifdef GIT_EXPERIMENTAL_SHA256
GIT_EXTERN(int) git_indexer_new(
		git_indexer **out,
		const char *path,
		git_indexer_options *opts);
#else
GIT_EXTERN(int) git_indexer_new(
		git_indexer **out,
		const char *path,
		unsigned int mode,
		git_odb *odb,
		git_indexer_options *opts);
#endif
GIT_EXTERN(int) git_indexer_append(git_indexer *idx, const void *data, size_t size, git_indexer_progress *stats);
GIT_EXTERN(int) git_indexer_commit(git_indexer *idx, git_indexer_progress *stats);
#ifndef GIT_DEPRECATE_HARD
GIT_EXTERN(const git_oid *) git_indexer_hash(const git_indexer *idx);
#endif
GIT_EXTERN(const char *) git_indexer_name(const git_indexer *idx);
GIT_EXTERN(void) git_indexer_free(git_indexer *idx);
GIT_END_DECL
#endif
