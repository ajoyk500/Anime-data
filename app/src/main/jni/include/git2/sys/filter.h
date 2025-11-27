
#ifndef INCLUDE_sys_git_filter_h__
#define INCLUDE_sys_git_filter_h__
#include "git2/filter.h"
GIT_BEGIN_DECL
GIT_EXTERN(git_filter *) git_filter_lookup(const char *name);
#define GIT_FILTER_CRLF  "crlf"
#define GIT_FILTER_IDENT "ident"
#define GIT_FILTER_CRLF_PRIORITY 0
#define GIT_FILTER_IDENT_PRIORITY 100
#define GIT_FILTER_DRIVER_PRIORITY 200
GIT_EXTERN(int) git_filter_list_new(
	git_filter_list **out,
	git_repository *repo,
	git_filter_mode_t mode,
	uint32_t options);
GIT_EXTERN(int) git_filter_list_push(
	git_filter_list *fl, git_filter *filter, void *payload);
GIT_EXTERN(size_t) git_filter_list_length(const git_filter_list *fl);
typedef struct git_filter_source git_filter_source;
GIT_EXTERN(git_repository *) git_filter_source_repo(const git_filter_source *src);
GIT_EXTERN(const char *) git_filter_source_path(const git_filter_source *src);
GIT_EXTERN(uint16_t) git_filter_source_filemode(const git_filter_source *src);
GIT_EXTERN(const git_oid *) git_filter_source_id(const git_filter_source *src);
GIT_EXTERN(git_filter_mode_t) git_filter_source_mode(const git_filter_source *src);
GIT_EXTERN(uint32_t) git_filter_source_flags(const git_filter_source *src);
typedef int GIT_CALLBACK(git_filter_init_fn)(git_filter *self);
typedef void GIT_CALLBACK(git_filter_shutdown_fn)(git_filter *self);
typedef int GIT_CALLBACK(git_filter_check_fn)(
	git_filter              *self,
	void                   **payload, 
	const git_filter_source *src,
	const char             **attr_values);
#ifndef GIT_DEPRECATE_HARD
typedef int GIT_CALLBACK(git_filter_apply_fn)(
	git_filter              *self,
	void                   **payload, 
	git_buf                 *to,
	const git_buf           *from,
	const git_filter_source *src);
#endif
typedef int GIT_CALLBACK(git_filter_stream_fn)(
	git_writestream        **out,
	git_filter              *self,
	void                   **payload,
	const git_filter_source *src,
	git_writestream         *next);
typedef void GIT_CALLBACK(git_filter_cleanup_fn)(
	git_filter              *self,
	void                    *payload);
struct git_filter {
	unsigned int           version;
	const char            *attributes;
	git_filter_init_fn     initialize;
	git_filter_shutdown_fn shutdown;
	git_filter_check_fn    check;
#ifdef GIT_DEPRECATE_HARD
	void *reserved;
#else
	git_filter_apply_fn    apply;
#endif
	git_filter_stream_fn   stream;
	git_filter_cleanup_fn  cleanup;
};
#define GIT_FILTER_VERSION 1
#define GIT_FILTER_INIT {GIT_FILTER_VERSION}
GIT_EXTERN(int) git_filter_init(git_filter *filter, unsigned int version);
GIT_EXTERN(int) git_filter_register(
	const char *name, git_filter *filter, int priority);
GIT_EXTERN(int) git_filter_unregister(const char *name);
GIT_END_DECL
#endif
