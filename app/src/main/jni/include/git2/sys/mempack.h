
#ifndef INCLUDE_sys_git_odb_mempack_h__
#define INCLUDE_sys_git_odb_mempack_h__
#include "git2/common.h"
#include "git2/types.h"
#include "git2/oid.h"
#include "git2/odb.h"
#include "git2/buffer.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_mempack_new(git_odb_backend **out);
GIT_EXTERN(int) git_mempack_write_thin_pack(git_odb_backend *backend, git_packbuilder *pb);
GIT_EXTERN(int) git_mempack_dump(git_buf *pack, git_repository *repo, git_odb_backend *backend);
GIT_EXTERN(int) git_mempack_reset(git_odb_backend *backend);
GIT_EXTERN(int) git_mempack_object_count(size_t *count, git_odb_backend *backend);
GIT_END_DECL
#endif
