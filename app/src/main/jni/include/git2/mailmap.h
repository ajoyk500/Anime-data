
#ifndef INCLUDE_git_mailmap_h__
#define INCLUDE_git_mailmap_h__
#include "common.h"
#include "types.h"
#include "buffer.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_mailmap_new(git_mailmap **out);
GIT_EXTERN(void) git_mailmap_free(git_mailmap *mm);
GIT_EXTERN(int) git_mailmap_add_entry(
	git_mailmap *mm, const char *real_name, const char *real_email,
	const char *replace_name, const char *replace_email);
GIT_EXTERN(int) git_mailmap_from_buffer(
	git_mailmap **out, const char *buf, size_t len);
GIT_EXTERN(int) git_mailmap_from_repository(
	git_mailmap **out, git_repository *repo);
GIT_EXTERN(int) git_mailmap_resolve(
	const char **real_name, const char **real_email,
	const git_mailmap *mm, const char *name, const char *email);
GIT_EXTERN(int) git_mailmap_resolve_signature(
	git_signature **out, const git_mailmap *mm, const git_signature *sig);
GIT_END_DECL
#endif
