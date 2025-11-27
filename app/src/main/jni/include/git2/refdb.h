
#ifndef INCLUDE_git_refdb_h__
#define INCLUDE_git_refdb_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "refs.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_refdb_new(git_refdb **out, git_repository *repo);
GIT_EXTERN(int) git_refdb_open(git_refdb **out, git_repository *repo);
GIT_EXTERN(int) git_refdb_compress(git_refdb *refdb);
GIT_EXTERN(void) git_refdb_free(git_refdb *refdb);
GIT_END_DECL
#endif
