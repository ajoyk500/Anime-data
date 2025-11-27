
#ifndef INCLUDE_sys_git_repository_h__
#define INCLUDE_sys_git_repository_h__
#include "git2/common.h"
#include "git2/types.h"
#include "git2/oid.h"
GIT_BEGIN_DECL
#ifdef GIT_EXPERIMENTAL_SHA256
typedef struct git_repository_new_options {
	unsigned int version; 
	git_oid_t oid_type;
} git_repository_new_options;
#define GIT_REPOSITORY_NEW_OPTIONS_VERSION 1
#define GIT_REPOSITORY_NEW_OPTIONS_INIT { GIT_REPOSITORY_NEW_OPTIONS_VERSION }
GIT_EXTERN(int) git_repository_new_options_init(
	git_repository_new_options *opts,
	unsigned int version);
GIT_EXTERN(int) git_repository_new(git_repository **out, git_repository_new_options *opts);
#else
GIT_EXTERN(int) git_repository_new(git_repository **out);
#endif
GIT_EXTERN(int) git_repository__cleanup(git_repository *repo);
GIT_EXTERN(int) git_repository_reinit_filesystem(
	git_repository *repo,
	int recurse_submodules);
GIT_EXTERN(int) git_repository_set_config(git_repository *repo, git_config *config);
GIT_EXTERN(int) git_repository_set_odb(git_repository *repo, git_odb *odb);
GIT_EXTERN(int) git_repository_set_refdb(git_repository *repo, git_refdb *refdb);
GIT_EXTERN(int) git_repository_set_index(git_repository *repo, git_index *index);
GIT_EXTERN(int) git_repository_set_bare(git_repository *repo);
GIT_EXTERN(int) git_repository_submodule_cache_all(
	git_repository *repo);
GIT_EXTERN(int) git_repository_submodule_cache_clear(
	git_repository *repo);
GIT_END_DECL
#endif
