
#ifndef INCLUDE_git_reset_h__
#define INCLUDE_git_reset_h__
#include "common.h"
#include "types.h"
#include "strarray.h"
#include "checkout.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_RESET_SOFT  = 1, 
	GIT_RESET_MIXED = 2, 
	GIT_RESET_HARD  = 3  
} git_reset_t;
GIT_EXTERN(int) git_reset(
	git_repository *repo,
	const git_object *target,
	git_reset_t reset_type,
	const git_checkout_options *checkout_opts);
GIT_EXTERN(int) git_reset_from_annotated(
	git_repository *repo,
	const git_annotated_commit *target,
	git_reset_t reset_type,
	const git_checkout_options *checkout_opts);
GIT_EXTERN(int) git_reset_default(
	git_repository *repo,
	const git_object *target,
	const git_strarray* pathspecs);
GIT_END_DECL
#endif
