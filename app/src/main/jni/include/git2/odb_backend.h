
#ifndef INCLUDE_git_odb_backend_h__
#define INCLUDE_git_odb_backend_h__
#include "common.h"
#include "types.h"
#include "indexer.h"
GIT_BEGIN_DECL
typedef struct {
	unsigned int version; 
	git_oid_t oid_type;
} git_odb_backend_pack_options;
#define GIT_ODB_BACKEND_PACK_OPTIONS_VERSION 1
#define GIT_ODB_BACKEND_PACK_OPTIONS_INIT \
	{ GIT_ODB_BACKEND_PACK_OPTIONS_VERSION }
typedef enum {
	GIT_ODB_BACKEND_LOOSE_FSYNC = (1 << 0)
} git_odb_backend_loose_flag_t;
typedef struct {
	unsigned int version; 
	uint32_t flags;
	int compression_level;
	unsigned int dir_mode;
	unsigned int file_mode;
	git_oid_t oid_type;
} git_odb_backend_loose_options;
#define GIT_ODB_BACKEND_LOOSE_OPTIONS_VERSION 1
#define GIT_ODB_BACKEND_LOOSE_OPTIONS_INIT \
	{ GIT_ODB_BACKEND_LOOSE_OPTIONS_VERSION, 0, -1 }
#ifdef GIT_EXPERIMENTAL_SHA256
GIT_EXTERN(int) git_odb_backend_pack(
	git_odb_backend **out,
	const char *objects_dir,
	const git_odb_backend_pack_options *opts);
GIT_EXTERN(int) git_odb_backend_one_pack(
	git_odb_backend **out,
	const char *index_file,
	const git_odb_backend_pack_options *opts);
GIT_EXTERN(int) git_odb_backend_loose(
	git_odb_backend **out,
	const char *objects_dir,
	git_odb_backend_loose_options *opts);
#else
GIT_EXTERN(int) git_odb_backend_pack(
	git_odb_backend **out,
	const char *objects_dir);
GIT_EXTERN(int) git_odb_backend_one_pack(
	git_odb_backend **out,
	const char *index_file);
GIT_EXTERN(int) git_odb_backend_loose(
	git_odb_backend **out,
	const char *objects_dir,
	int compression_level,
	int do_fsync,
	unsigned int dir_mode,
	unsigned int file_mode);
#endif
typedef enum {
	GIT_STREAM_RDONLY = (1 << 1),
	GIT_STREAM_WRONLY = (1 << 2),
	GIT_STREAM_RW = (GIT_STREAM_RDONLY | GIT_STREAM_WRONLY)
} git_odb_stream_t;
struct git_odb_stream {
	git_odb_backend *backend;
	unsigned int mode;
	void *hash_ctx;
#ifdef GIT_EXPERIMENTAL_SHA256
	git_oid_t oid_type;
#endif
	git_object_size_t declared_size;
	git_object_size_t received_bytes;
	int GIT_CALLBACK(read)(git_odb_stream *stream, char *buffer, size_t len);
	int GIT_CALLBACK(write)(git_odb_stream *stream, const char *buffer, size_t len);
	int GIT_CALLBACK(finalize_write)(git_odb_stream *stream, const git_oid *oid);
	void GIT_CALLBACK(free)(git_odb_stream *stream);
};
struct git_odb_writepack {
	git_odb_backend *backend;
	int GIT_CALLBACK(append)(git_odb_writepack *writepack, const void *data, size_t size, git_indexer_progress *stats);
	int GIT_CALLBACK(commit)(git_odb_writepack *writepack, git_indexer_progress *stats);
	void GIT_CALLBACK(free)(git_odb_writepack *writepack);
};
GIT_END_DECL
#endif
