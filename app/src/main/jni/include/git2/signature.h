
#ifndef INCLUDE_git_signature_h__
#define INCLUDE_git_signature_h__
#include "common.h"
#include "types.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_signature_new(git_signature **out, const char *name, const char *email, git_time_t time, int offset);
GIT_EXTERN(int) git_signature_now(git_signature **out, const char *name, const char *email);
GIT_EXTERN(int) git_signature_default_from_env(
	git_signature **author_out,
	git_signature **committer_out,
	git_repository *repo);
GIT_EXTERN(int) git_signature_default(git_signature **out, git_repository *repo);
GIT_EXTERN(int) git_signature_from_buffer(git_signature **out, const char *buf);
GIT_EXTERN(int) git_signature_dup(git_signature **dest, const git_signature *sig);
GIT_EXTERN(void) git_signature_free(git_signature *sig);
GIT_END_DECL
#endif
