
#ifndef INCLUDE_git_odb_h__
#define INCLUDE_git_odb_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "oidarray.h"
#include "indexer.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_ODB_LOOKUP_NO_REFRESH = (1 << 0)
} git_odb_lookup_flags_t;
typedef int GIT_CALLBACK(git_odb_foreach_cb)(const git_oid *id, void *payload);
typedef struct {
	unsigned int version; 
	git_oid_t oid_type;
} git_odb_options;
#define GIT_ODB_OPTIONS_VERSION 1
#define GIT_ODB_OPTIONS_INIT { GIT_ODB_OPTIONS_VERSION }
#ifdef GIT_EXPERIMENTAL_SHA256
GIT_EXTERN(int) git_odb_new(git_odb **odb, const git_odb_options *opts);
GIT_EXTERN(int) git_odb_open(
	git_odb **odb_out,
	const char *objects_dir,
	const git_odb_options *opts);
#else
GIT_EXTERN(int) git_odb_new(git_odb **odb);
GIT_EXTERN(int) git_odb_open(git_odb **odb_out, const char *objects_dir);
#endif
GIT_EXTERN(int) git_odb_add_disk_alternate(git_odb *odb, const char *path);
GIT_EXTERN(void) git_odb_free(git_odb *db);
GIT_EXTERN(int) git_odb_read(git_odb_object **obj, git_odb *db, const git_oid *id);
GIT_EXTERN(int) git_odb_read_prefix(git_odb_object **obj, git_odb *db, const git_oid *short_id, size_t len);
GIT_EXTERN(int) git_odb_read_header(size_t *len_out, git_object_t *type_out, git_odb *db, const git_oid *id);
GIT_EXTERN(int) git_odb_exists(git_odb *db, const git_oid *id);
GIT_EXTERN(int) git_odb_exists_ext(git_odb *db, const git_oid *id, unsigned int flags);
GIT_EXTERN(int) git_odb_exists_prefix(
	git_oid *out, git_odb *db, const git_oid *short_id, size_t len);
typedef struct git_odb_expand_id {
	git_oid id;
	unsigned short length;
	git_object_t type;
} git_odb_expand_id;
GIT_EXTERN(int) git_odb_expand_ids(
	git_odb *db,
	git_odb_expand_id *ids,
	size_t count);
GIT_EXTERN(int) git_odb_refresh(git_odb *db);
GIT_EXTERN(int) git_odb_foreach(
	git_odb *db,
	git_odb_foreach_cb cb,
	void *payload);
GIT_EXTERN(int) git_odb_write(git_oid *out, git_odb *odb, const void *data, size_t len, git_object_t type);
GIT_EXTERN(int) git_odb_open_wstream(git_odb_stream **out, git_odb *db, git_object_size_t size, git_object_t type);
GIT_EXTERN(int) git_odb_stream_write(git_odb_stream *stream, const char *buffer, size_t len);
GIT_EXTERN(int) git_odb_stream_finalize_write(git_oid *out, git_odb_stream *stream);
GIT_EXTERN(int) git_odb_stream_read(git_odb_stream *stream, char *buffer, size_t len);
GIT_EXTERN(void) git_odb_stream_free(git_odb_stream *stream);
GIT_EXTERN(int) git_odb_open_rstream(
	git_odb_stream **out,
	size_t *len,
	git_object_t *type,
	git_odb *db,
	const git_oid *oid);
GIT_EXTERN(int) git_odb_write_pack(
	git_odb_writepack **out,
	git_odb *db,
	git_indexer_progress_cb progress_cb,
	void *progress_payload);
GIT_EXTERN(int) git_odb_write_multi_pack_index(
	git_odb *db);
#ifdef GIT_EXPERIMENTAL_SHA256
GIT_EXTERN(int) git_odb_hash(
	git_oid *oid,
	const void *data,
	size_t len,
	git_object_t object_type,
	git_oid_t oid_type);
GIT_EXTERN(int) git_odb_hashfile(
	git_oid *oid,
	const char *path,
	git_object_t object_type,
	git_oid_t oid_type);
#else
GIT_EXTERN(int) git_odb_hash(git_oid *oid, const void *data, size_t len, git_object_t object_type);
GIT_EXTERN(int) git_odb_hashfile(git_oid *oid, const char *path, git_object_t object_type);
#endif
GIT_EXTERN(int) git_odb_object_dup(git_odb_object **dest, git_odb_object *source);
GIT_EXTERN(void) git_odb_object_free(git_odb_object *object);
GIT_EXTERN(const git_oid *) git_odb_object_id(git_odb_object *object);
GIT_EXTERN(const void *) git_odb_object_data(git_odb_object *object);
GIT_EXTERN(size_t) git_odb_object_size(git_odb_object *object);
GIT_EXTERN(git_object_t) git_odb_object_type(git_odb_object *object);
GIT_EXTERN(int) git_odb_add_backend(git_odb *odb, git_odb_backend *backend, int priority);
GIT_EXTERN(int) git_odb_add_alternate(git_odb *odb, git_odb_backend *backend, int priority);
GIT_EXTERN(size_t) git_odb_num_backends(git_odb *odb);
GIT_EXTERN(int) git_odb_get_backend(git_odb_backend **out, git_odb *odb, size_t pos);
GIT_EXTERN(int) git_odb_set_commit_graph(git_odb *odb, git_commit_graph *cgraph);
GIT_END_DECL
#endif
