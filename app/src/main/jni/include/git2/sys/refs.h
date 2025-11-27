
#ifndef INCLUDE_sys_git_refdb_h__
#define INCLUDE_sys_git_refdb_h__
#include "git2/common.h"
#include "git2/types.h"
#include "git2/oid.h"
GIT_BEGIN_DECL
GIT_EXTERN(git_reference *) git_reference__alloc(
	const char *name,
	const git_oid *oid,
	const git_oid *peel);
GIT_EXTERN(git_reference *) git_reference__alloc_symbolic(
	const char *name,
	const char *target);
GIT_END_DECL
#endif
