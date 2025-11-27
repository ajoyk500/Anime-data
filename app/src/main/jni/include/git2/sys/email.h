
#ifndef INCLUDE_sys_git_email_h__
#define INCLUDE_sys_git_email_h__
#include "git2/common.h"
#include "git2/diff.h"
#include "git2/email.h"
#include "git2/types.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_email_create_from_diff(
	git_buf *out,
	git_diff *diff,
	size_t patch_idx,
	size_t patch_count,
	const git_oid *commit_id,
	const char *summary,
	const char *body,
	const git_signature *author,
	const git_email_create_options *opts);
GIT_END_DECL
#endif
