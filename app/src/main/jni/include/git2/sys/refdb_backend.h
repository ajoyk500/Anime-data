
#ifndef INCLUDE_sys_git_refdb_backend_h__
#define INCLUDE_sys_git_refdb_backend_h__
#include "git2/common.h"
#include "git2/types.h"
#include "git2/oid.h"
GIT_BEGIN_DECL
struct git_reference_iterator {
	git_refdb *db;
	int GIT_CALLBACK(next)(
		git_reference **ref,
		git_reference_iterator *iter);
	int GIT_CALLBACK(next_name)(
		const char **ref_name,
		git_reference_iterator *iter);
	void GIT_CALLBACK(free)(
		git_reference_iterator *iter);
};
struct git_refdb_backend {
	unsigned int version; 
	int GIT_CALLBACK(exists)(
		int *exists,
		git_refdb_backend *backend,
		const char *ref_name);
	int GIT_CALLBACK(lookup)(
		git_reference **out,
		git_refdb_backend *backend,
		const char *ref_name);
	int GIT_CALLBACK(iterator)(
		git_reference_iterator **iter,
		struct git_refdb_backend *backend,
		const char *glob);
	int GIT_CALLBACK(write)(git_refdb_backend *backend,
		     const git_reference *ref, int force,
		     const git_signature *who, const char *message,
		     const git_oid *old, const char *old_target);
	int GIT_CALLBACK(rename)(
		git_reference **out, git_refdb_backend *backend,
		const char *old_name, const char *new_name, int force,
		const git_signature *who, const char *message);
	int GIT_CALLBACK(del)(git_refdb_backend *backend, const char *ref_name, const git_oid *old_id, const char *old_target);
	int GIT_CALLBACK(compress)(git_refdb_backend *backend);
	int GIT_CALLBACK(has_log)(git_refdb_backend *backend, const char *refname);
	int GIT_CALLBACK(ensure_log)(git_refdb_backend *backend, const char *refname);
	void GIT_CALLBACK(free)(git_refdb_backend *backend);
	int GIT_CALLBACK(reflog_read)(git_reflog **out, git_refdb_backend *backend, const char *name);
	int GIT_CALLBACK(reflog_write)(git_refdb_backend *backend, git_reflog *reflog);
	int GIT_CALLBACK(reflog_rename)(git_refdb_backend *_backend, const char *old_name, const char *new_name);
	int GIT_CALLBACK(reflog_delete)(git_refdb_backend *backend, const char *name);
	int GIT_CALLBACK(lock)(void **payload_out, git_refdb_backend *backend, const char *refname);
	int GIT_CALLBACK(unlock)(git_refdb_backend *backend, void *payload, int success, int update_reflog,
		      const git_reference *ref, const git_signature *sig, const char *message);
};
#define GIT_REFDB_BACKEND_VERSION 1
#define GIT_REFDB_BACKEND_INIT {GIT_REFDB_BACKEND_VERSION}
GIT_EXTERN(int) git_refdb_init_backend(
	git_refdb_backend *backend,
	unsigned int version);
GIT_EXTERN(int) git_refdb_backend_fs(
	git_refdb_backend **backend_out,
	git_repository *repo);
GIT_EXTERN(int) git_refdb_set_backend(
	git_refdb *refdb,
	git_refdb_backend *backend);
GIT_END_DECL
#endif
