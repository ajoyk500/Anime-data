
#ifndef INCLUDE_sys_git_odb_backend_h__
#define INCLUDE_sys_git_odb_backend_h__
#include "git2/common.h"
#include "git2/types.h"
#include "git2/oid.h"
#include "git2/odb.h"
GIT_BEGIN_DECL
struct git_odb_backend {
	unsigned int version;
	git_odb *odb;
	int GIT_CALLBACK(read)(
		void **, size_t *, git_object_t *, git_odb_backend *, const git_oid *);
	int GIT_CALLBACK(read_prefix)(
		git_oid *, void **, size_t *, git_object_t *,
		git_odb_backend *, const git_oid *, size_t);
	int GIT_CALLBACK(read_header)(
		size_t *, git_object_t *, git_odb_backend *, const git_oid *);
	int GIT_CALLBACK(write)(
		git_odb_backend *, const git_oid *, const void *, size_t, git_object_t);
	int GIT_CALLBACK(writestream)(
		git_odb_stream **, git_odb_backend *, git_object_size_t, git_object_t);
	int GIT_CALLBACK(readstream)(
		git_odb_stream **, size_t *, git_object_t *,
		git_odb_backend *, const git_oid *);
	int GIT_CALLBACK(exists)(
		git_odb_backend *, const git_oid *);
	int GIT_CALLBACK(exists_prefix)(
		git_oid *, git_odb_backend *, const git_oid *, size_t);
	int GIT_CALLBACK(refresh)(git_odb_backend *);
	int GIT_CALLBACK(foreach)(
		git_odb_backend *, git_odb_foreach_cb cb, void *payload);
	int GIT_CALLBACK(writepack)(
		git_odb_writepack **, git_odb_backend *, git_odb *odb,
		git_indexer_progress_cb progress_cb, void *progress_payload);
	int GIT_CALLBACK(writemidx)(git_odb_backend *);
	int GIT_CALLBACK(freshen)(git_odb_backend *, const git_oid *);
	void GIT_CALLBACK(free)(git_odb_backend *);
};
#define GIT_ODB_BACKEND_VERSION 1
#define GIT_ODB_BACKEND_INIT {GIT_ODB_BACKEND_VERSION}
GIT_EXTERN(int) git_odb_init_backend(
	git_odb_backend *backend,
	unsigned int version);
GIT_EXTERN(void *) git_odb_backend_data_alloc(git_odb_backend *backend, size_t len);
GIT_EXTERN(void) git_odb_backend_data_free(git_odb_backend *backend, void *data);
#ifndef GIT_DEPRECATE_HARD
GIT_EXTERN(void *) git_odb_backend_malloc(git_odb_backend *backend, size_t len);
#endif
GIT_END_DECL
#endif
