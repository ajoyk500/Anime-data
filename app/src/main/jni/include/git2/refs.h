
#ifndef INCLUDE_git_refs_h__
#define INCLUDE_git_refs_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "strarray.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_reference_lookup(git_reference **out, git_repository *repo, const char *name);
GIT_EXTERN(int) git_reference_name_to_id(
	git_oid *out, git_repository *repo, const char *name);
GIT_EXTERN(int) git_reference_dwim(git_reference **out, git_repository *repo, const char *shorthand);
GIT_EXTERN(int) git_reference_symbolic_create_matching(git_reference **out, git_repository *repo, const char *name, const char *target, int force, const char *current_value, const char *log_message);
GIT_EXTERN(int) git_reference_symbolic_create(git_reference **out, git_repository *repo, const char *name, const char *target, int force, const char *log_message);
GIT_EXTERN(int) git_reference_create(git_reference **out, git_repository *repo, const char *name, const git_oid *id, int force, const char *log_message);
GIT_EXTERN(int) git_reference_create_matching(git_reference **out, git_repository *repo, const char *name, const git_oid *id, int force, const git_oid *current_id, const char *log_message);
GIT_EXTERN(const git_oid *) git_reference_target(const git_reference *ref);
GIT_EXTERN(const git_oid *) git_reference_target_peel(const git_reference *ref);
GIT_EXTERN(const char *) git_reference_symbolic_target(const git_reference *ref);
GIT_EXTERN(git_reference_t) git_reference_type(const git_reference *ref);
GIT_EXTERN(const char *) git_reference_name(const git_reference *ref);
GIT_EXTERN(int) git_reference_resolve(git_reference **out, const git_reference *ref);
GIT_EXTERN(git_repository *) git_reference_owner(const git_reference *ref);
GIT_EXTERN(int) git_reference_symbolic_set_target(
	git_reference **out,
	git_reference *ref,
	const char *target,
	const char *log_message);
GIT_EXTERN(int) git_reference_set_target(
	git_reference **out,
	git_reference *ref,
	const git_oid *id,
	const char *log_message);
GIT_EXTERN(int) git_reference_rename(
	git_reference **new_ref,
	git_reference *ref,
	const char *new_name,
	int force,
	const char *log_message);
GIT_EXTERN(int) git_reference_delete(git_reference *ref);
GIT_EXTERN(int) git_reference_remove(git_repository *repo, const char *name);
GIT_EXTERN(int) git_reference_list(git_strarray *array, git_repository *repo);
typedef int GIT_CALLBACK(git_reference_foreach_cb)(git_reference *reference, void *payload);
typedef int GIT_CALLBACK(git_reference_foreach_name_cb)(const char *name, void *payload);
GIT_EXTERN(int) git_reference_foreach(
	git_repository *repo,
	git_reference_foreach_cb callback,
	void *payload);
GIT_EXTERN(int) git_reference_foreach_name(
	git_repository *repo,
	git_reference_foreach_name_cb callback,
	void *payload);
GIT_EXTERN(int) git_reference_dup(git_reference **dest, git_reference *source);
GIT_EXTERN(void) git_reference_free(git_reference *ref);
GIT_EXTERN(int) git_reference_cmp(
	const git_reference *ref1,
	const git_reference *ref2);
GIT_EXTERN(int) git_reference_iterator_new(
	git_reference_iterator **out,
	git_repository *repo);
GIT_EXTERN(int) git_reference_iterator_glob_new(
	git_reference_iterator **out,
	git_repository *repo,
	const char *glob);
GIT_EXTERN(int) git_reference_next(git_reference **out, git_reference_iterator *iter);
GIT_EXTERN(int) git_reference_next_name(const char **out, git_reference_iterator *iter);
GIT_EXTERN(void) git_reference_iterator_free(git_reference_iterator *iter);
GIT_EXTERN(int) git_reference_foreach_glob(
	git_repository *repo,
	const char *glob,
	git_reference_foreach_name_cb callback,
	void *payload);
GIT_EXTERN(int) git_reference_has_log(git_repository *repo, const char *refname);
GIT_EXTERN(int) git_reference_ensure_log(git_repository *repo, const char *refname);
GIT_EXTERN(int) git_reference_is_branch(const git_reference *ref);
GIT_EXTERN(int) git_reference_is_remote(const git_reference *ref);
GIT_EXTERN(int) git_reference_is_tag(const git_reference *ref);
GIT_EXTERN(int) git_reference_is_note(const git_reference *ref);
typedef enum {
	GIT_REFERENCE_FORMAT_NORMAL = 0u,
	GIT_REFERENCE_FORMAT_ALLOW_ONELEVEL = (1u << 0),
	GIT_REFERENCE_FORMAT_REFSPEC_PATTERN = (1u << 1),
	GIT_REFERENCE_FORMAT_REFSPEC_SHORTHAND = (1u << 2)
} git_reference_format_t;
GIT_EXTERN(int) git_reference_normalize_name(
	char *buffer_out,
	size_t buffer_size,
	const char *name,
	unsigned int flags);
GIT_EXTERN(int) git_reference_peel(
	git_object **out,
	const git_reference *ref,
	git_object_t type);
GIT_EXTERN(int) git_reference_name_is_valid(int *valid, const char *refname);
GIT_EXTERN(const char *) git_reference_shorthand(const git_reference *ref);
GIT_END_DECL
#endif
