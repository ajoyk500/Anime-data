
#ifndef INCLUDE_git_pack_h__
#define INCLUDE_git_pack_h__
#include "common.h"
#include "oid.h"
#include "indexer.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_PACKBUILDER_ADDING_OBJECTS = 0,
	GIT_PACKBUILDER_DELTAFICATION = 1
} git_packbuilder_stage_t;
GIT_EXTERN(int) git_packbuilder_new(git_packbuilder **out, git_repository *repo);
GIT_EXTERN(unsigned int) git_packbuilder_set_threads(git_packbuilder *pb, unsigned int n);
GIT_EXTERN(int) git_packbuilder_insert(git_packbuilder *pb, const git_oid *id, const char *name);
GIT_EXTERN(int) git_packbuilder_insert_tree(git_packbuilder *pb, const git_oid *id);
GIT_EXTERN(int) git_packbuilder_insert_commit(git_packbuilder *pb, const git_oid *id);
GIT_EXTERN(int) git_packbuilder_insert_walk(git_packbuilder *pb, git_revwalk *walk);
GIT_EXTERN(int) git_packbuilder_insert_recur(git_packbuilder *pb, const git_oid *id, const char *name);
GIT_EXTERN(int) git_packbuilder_write_buf(git_buf *buf, git_packbuilder *pb);
GIT_EXTERN(int) git_packbuilder_write(
	git_packbuilder *pb,
	const char *path,
	unsigned int mode,
	git_indexer_progress_cb progress_cb,
	void *progress_cb_payload);
#ifndef GIT_DEPRECATE_HARD
GIT_EXTERN(const git_oid *) git_packbuilder_hash(git_packbuilder *pb);
#endif
GIT_EXTERN(const char *) git_packbuilder_name(git_packbuilder *pb);
typedef int GIT_CALLBACK(git_packbuilder_foreach_cb)(void *buf, size_t size, void *payload);
GIT_EXTERN(int) git_packbuilder_foreach(git_packbuilder *pb, git_packbuilder_foreach_cb cb, void *payload);
GIT_EXTERN(size_t) git_packbuilder_object_count(git_packbuilder *pb);
GIT_EXTERN(size_t) git_packbuilder_written(git_packbuilder *pb);
typedef int GIT_CALLBACK(git_packbuilder_progress)(
	int stage,
	uint32_t current,
	uint32_t total,
	void *payload);
GIT_EXTERN(int) git_packbuilder_set_callbacks(
	git_packbuilder *pb,
	git_packbuilder_progress progress_cb,
	void *progress_cb_payload);
GIT_EXTERN(void) git_packbuilder_free(git_packbuilder *pb);
GIT_END_DECL
#endif
