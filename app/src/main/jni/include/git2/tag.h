
#ifndef INCLUDE_git_tag_h__
#define INCLUDE_git_tag_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "object.h"
#include "strarray.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_tag_lookup(
	git_tag **out, git_repository *repo, const git_oid *id);
GIT_EXTERN(int) git_tag_lookup_prefix(
	git_tag **out, git_repository *repo, const git_oid *id, size_t len);
GIT_EXTERN(void) git_tag_free(git_tag *tag);
GIT_EXTERN(const git_oid *) git_tag_id(const git_tag *tag);
GIT_EXTERN(git_repository *) git_tag_owner(const git_tag *tag);
GIT_EXTERN(int) git_tag_target(git_object **target_out, const git_tag *tag);
GIT_EXTERN(const git_oid *) git_tag_target_id(const git_tag *tag);
GIT_EXTERN(git_object_t) git_tag_target_type(const git_tag *tag);
GIT_EXTERN(const char *) git_tag_name(const git_tag *tag);
GIT_EXTERN(const git_signature *) git_tag_tagger(const git_tag *tag);
GIT_EXTERN(const char *) git_tag_message(const git_tag *tag);
GIT_EXTERN(int) git_tag_create(
	git_oid *oid,
	git_repository *repo,
	const char *tag_name,
	const git_object *target,
	const git_signature *tagger,
	const char *message,
	int force);
GIT_EXTERN(int) git_tag_annotation_create(
	git_oid *oid,
	git_repository *repo,
	const char *tag_name,
	const git_object *target,
	const git_signature *tagger,
	const char *message);
GIT_EXTERN(int) git_tag_create_from_buffer(
	git_oid *oid,
	git_repository *repo,
	const char *buffer,
	int force);
GIT_EXTERN(int) git_tag_create_lightweight(
	git_oid *oid,
	git_repository *repo,
	const char *tag_name,
	const git_object *target,
	int force);
GIT_EXTERN(int) git_tag_delete(
	git_repository *repo,
	const char *tag_name);
GIT_EXTERN(int) git_tag_list(
	git_strarray *tag_names,
	git_repository *repo);
GIT_EXTERN(int) git_tag_list_match(
	git_strarray *tag_names,
	const char *pattern,
	git_repository *repo);
typedef int GIT_CALLBACK(git_tag_foreach_cb)(const char *name, git_oid *oid, void *payload);
GIT_EXTERN(int) git_tag_foreach(
	git_repository *repo,
	git_tag_foreach_cb callback,
	void *payload);
GIT_EXTERN(int) git_tag_peel(
	git_object **tag_target_out,
	const git_tag *tag);
GIT_EXTERN(int) git_tag_dup(git_tag **out, git_tag *source);
GIT_EXTERN(int) git_tag_name_is_valid(int *valid, const char *name);
GIT_END_DECL
#endif
