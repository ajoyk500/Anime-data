
#ifndef INCLUDE_git_object_h__
#define INCLUDE_git_object_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "buffer.h"
GIT_BEGIN_DECL
#define GIT_OBJECT_SIZE_MAX UINT64_MAX
GIT_EXTERN(int) git_object_lookup(
		git_object **object,
		git_repository *repo,
		const git_oid *id,
		git_object_t type);
GIT_EXTERN(int) git_object_lookup_prefix(
		git_object **object_out,
		git_repository *repo,
		const git_oid *id,
		size_t len,
		git_object_t type);
GIT_EXTERN(int) git_object_lookup_bypath(
		git_object **out,
		const git_object *treeish,
		const char *path,
		git_object_t type);
GIT_EXTERN(const git_oid *) git_object_id(const git_object *obj);
GIT_EXTERN(int) git_object_short_id(git_buf *out, const git_object *obj);
GIT_EXTERN(git_object_t) git_object_type(const git_object *obj);
GIT_EXTERN(git_repository *) git_object_owner(const git_object *obj);
GIT_EXTERN(void) git_object_free(git_object *object);
GIT_EXTERN(const char *) git_object_type2string(git_object_t type);
GIT_EXTERN(git_object_t) git_object_string2type(const char *str);
GIT_EXTERN(int) git_object_typeisloose(git_object_t type);
GIT_EXTERN(int) git_object_peel(
	git_object **peeled,
	const git_object *object,
	git_object_t target_type);
GIT_EXTERN(int) git_object_dup(git_object **dest, git_object *source);
#ifdef GIT_EXPERIMENTAL_SHA256
GIT_EXTERN(int) git_object_rawcontent_is_valid(
	int *valid,
	const char *buf,
	size_t len,
	git_object_t object_type,
	git_oid_t oid_type);
#else
GIT_EXTERN(int) git_object_rawcontent_is_valid(
	int *valid,
	const char *buf,
	size_t len,
	git_object_t object_type);
#endif
GIT_END_DECL
#endif
