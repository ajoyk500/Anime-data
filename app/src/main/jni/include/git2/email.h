
#ifndef INCLUDE_git_email_h__
#define INCLUDE_git_email_h__
#include "common.h"
#include "diff.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_EMAIL_CREATE_DEFAULT = 0,
	GIT_EMAIL_CREATE_OMIT_NUMBERS = (1u << 0),
	GIT_EMAIL_CREATE_ALWAYS_NUMBER = (1u << 1),
	GIT_EMAIL_CREATE_NO_RENAMES = (1u << 2)
} git_email_create_flags_t;
typedef struct {
	unsigned int version;
	uint32_t flags;
	git_diff_options diff_opts;
	git_diff_find_options diff_find_opts;
	const char *subject_prefix;
	size_t start_number;
	size_t reroll_number;
} git_email_create_options;
#define GIT_EMAIL_CREATE_OPTIONS_VERSION 1
#define GIT_EMAIL_CREATE_OPTIONS_INIT \
{ \
	GIT_EMAIL_CREATE_OPTIONS_VERSION, \
	GIT_EMAIL_CREATE_DEFAULT, \
	{ GIT_DIFF_OPTIONS_VERSION, GIT_DIFF_SHOW_BINARY, GIT_SUBMODULE_IGNORE_UNSPECIFIED, {NULL,0}, NULL, NULL, NULL, 3 }, \
	GIT_DIFF_FIND_OPTIONS_INIT \
}
GIT_EXTERN(int) git_email_create_from_commit(
	git_buf *out,
	git_commit *commit,
	const git_email_create_options *opts);
GIT_END_DECL
#endif
