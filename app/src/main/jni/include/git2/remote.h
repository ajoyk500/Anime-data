
#ifndef INCLUDE_git_remote_h__
#define INCLUDE_git_remote_h__
#include "common.h"
#include "repository.h"
#include "refspec.h"
#include "net.h"
#include "indexer.h"
#include "strarray.h"
#include "transport.h"
#include "pack.h"
#include "proxy.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_remote_create(
		git_remote **out,
		git_repository *repo,
		const char *name,
		const char *url);
typedef enum {
	GIT_REMOTE_REDIRECT_NONE = (1 << 0),
	GIT_REMOTE_REDIRECT_INITIAL = (1 << 1),
	GIT_REMOTE_REDIRECT_ALL = (1 << 2)
} git_remote_redirect_t;
typedef enum {
	GIT_REMOTE_CREATE_SKIP_INSTEADOF = (1 << 0),
	GIT_REMOTE_CREATE_SKIP_DEFAULT_FETCHSPEC = (1 << 1)
} git_remote_create_flags;
typedef enum {
	GIT_REMOTE_UPDATE_FETCHHEAD = (1 << 0),
	GIT_REMOTE_UPDATE_REPORT_UNCHANGED = (1 << 1)
} git_remote_update_flags;
typedef struct git_remote_create_options {
	unsigned int version;
	git_repository *repository;
	const char *name;
	const char *fetchspec;
	unsigned int flags;
} git_remote_create_options;
#define GIT_REMOTE_CREATE_OPTIONS_VERSION 1
#define GIT_REMOTE_CREATE_OPTIONS_INIT {GIT_REMOTE_CREATE_OPTIONS_VERSION}
GIT_EXTERN(int) git_remote_create_options_init(
		git_remote_create_options *opts,
		unsigned int version);
GIT_EXTERN(int) git_remote_create_with_opts(
		git_remote **out,
		const char *url,
		const git_remote_create_options *opts);
GIT_EXTERN(int) git_remote_create_with_fetchspec(
		git_remote **out,
		git_repository *repo,
		const char *name,
		const char *url,
		const char *fetch);
GIT_EXTERN(int) git_remote_create_anonymous(
		git_remote **out,
		git_repository *repo,
		const char *url);
GIT_EXTERN(int) git_remote_create_detached(
		git_remote **out,
		const char *url);
GIT_EXTERN(int) git_remote_lookup(git_remote **out, git_repository *repo, const char *name);
GIT_EXTERN(int) git_remote_dup(git_remote **dest, git_remote *source);
GIT_EXTERN(git_repository *) git_remote_owner(const git_remote *remote);
GIT_EXTERN(const char *) git_remote_name(const git_remote *remote);
GIT_EXTERN(const char *) git_remote_url(const git_remote *remote);
GIT_EXTERN(const char *) git_remote_pushurl(const git_remote *remote);
GIT_EXTERN(int) git_remote_set_url(git_repository *repo, const char *remote, const char *url);
GIT_EXTERN(int) git_remote_set_pushurl(git_repository *repo, const char *remote, const char *url);
GIT_EXTERN(int) git_remote_set_instance_url(git_remote *remote, const char *url);
GIT_EXTERN(int) git_remote_set_instance_pushurl(git_remote *remote, const char *url);
GIT_EXTERN(int) git_remote_add_fetch(git_repository *repo, const char *remote, const char *refspec);
GIT_EXTERN(int) git_remote_get_fetch_refspecs(git_strarray *array, const git_remote *remote);
GIT_EXTERN(int) git_remote_add_push(git_repository *repo, const char *remote, const char *refspec);
GIT_EXTERN(int) git_remote_get_push_refspecs(git_strarray *array, const git_remote *remote);
GIT_EXTERN(size_t) git_remote_refspec_count(const git_remote *remote);
GIT_EXTERN(const git_refspec *)git_remote_get_refspec(const git_remote *remote, size_t n);
GIT_EXTERN(int) git_remote_ls(const git_remote_head ***out,  size_t *size, git_remote *remote);
GIT_EXTERN(int) git_remote_connected(const git_remote *remote);
GIT_EXTERN(int) git_remote_stop(git_remote *remote);
GIT_EXTERN(int) git_remote_disconnect(git_remote *remote);
GIT_EXTERN(void) git_remote_free(git_remote *remote);
GIT_EXTERN(int) git_remote_list(git_strarray *out, git_repository *repo);
typedef enum git_remote_completion_t {
	GIT_REMOTE_COMPLETION_DOWNLOAD,
	GIT_REMOTE_COMPLETION_INDEXING,
	GIT_REMOTE_COMPLETION_ERROR
} git_remote_completion_t;
typedef int GIT_CALLBACK(git_push_transfer_progress_cb)(
	unsigned int current,
	unsigned int total,
	size_t bytes,
	void *payload);
typedef struct {
	char *src_refname;
	char *dst_refname;
	git_oid src;
	git_oid dst;
} git_push_update;
typedef int GIT_CALLBACK(git_push_negotiation)(
	const git_push_update **updates,
	size_t len,
	void *payload);
typedef int GIT_CALLBACK(git_push_update_reference_cb)(const char *refname, const char *status, void *data);
#ifndef GIT_DEPRECATE_HARD
typedef int GIT_CALLBACK(git_url_resolve_cb)(git_buf *url_resolved, const char *url, int direction, void *payload);
#endif
typedef int GIT_CALLBACK(git_remote_ready_cb)(git_remote *remote, int direction, void *payload);
struct git_remote_callbacks {
	unsigned int version; 
	git_transport_message_cb sideband_progress;
	int GIT_CALLBACK(completion)(git_remote_completion_t type,
		void *data);
	git_credential_acquire_cb credentials;
	git_transport_certificate_check_cb certificate_check;
	git_indexer_progress_cb transfer_progress;
#ifdef GIT_DEPRECATE_HARD
	void *reserved_update_tips;
#else
	int GIT_CALLBACK(update_tips)(const char *refname,
		const git_oid *a, const git_oid *b, void *data);
#endif
	git_packbuilder_progress pack_progress;
	git_push_transfer_progress_cb push_transfer_progress;
	git_push_update_reference_cb push_update_reference;
	git_push_negotiation push_negotiation;
	git_transport_cb transport;
	git_remote_ready_cb remote_ready;
	void *payload;
#ifdef GIT_DEPRECATE_HARD
	void *reserved;
#else
	git_url_resolve_cb resolve_url;
#endif
	int GIT_CALLBACK(update_refs)(
		const char *refname,
		const git_oid *a,
		const git_oid *b,
		git_refspec *spec,
		void *data);
};
#define GIT_REMOTE_CALLBACKS_VERSION 1
#define GIT_REMOTE_CALLBACKS_INIT {GIT_REMOTE_CALLBACKS_VERSION}
GIT_EXTERN(int) git_remote_init_callbacks(
	git_remote_callbacks *opts,
	unsigned int version);
typedef enum {
	GIT_FETCH_PRUNE_UNSPECIFIED,
	GIT_FETCH_PRUNE,
	GIT_FETCH_NO_PRUNE
} git_fetch_prune_t;
typedef enum {
	GIT_REMOTE_DOWNLOAD_TAGS_UNSPECIFIED = 0,
	GIT_REMOTE_DOWNLOAD_TAGS_AUTO,
	GIT_REMOTE_DOWNLOAD_TAGS_NONE,
	GIT_REMOTE_DOWNLOAD_TAGS_ALL
} git_remote_autotag_option_t;
typedef enum {
	GIT_FETCH_DEPTH_FULL = 0,
	GIT_FETCH_DEPTH_UNSHALLOW = 2147483647
} git_fetch_depth_t;
typedef struct {
	int version;
	git_remote_callbacks callbacks;
	git_fetch_prune_t prune;
	unsigned int update_fetchhead;
	git_remote_autotag_option_t download_tags;
	git_proxy_options proxy_opts;
	int depth;
	git_remote_redirect_t follow_redirects;
	git_strarray custom_headers;
} git_fetch_options;
#define GIT_FETCH_OPTIONS_VERSION 1
#define GIT_FETCH_OPTIONS_INIT { \
	GIT_FETCH_OPTIONS_VERSION, \
	GIT_REMOTE_CALLBACKS_INIT, \
	GIT_FETCH_PRUNE_UNSPECIFIED, \
	GIT_REMOTE_UPDATE_FETCHHEAD, \
	GIT_REMOTE_DOWNLOAD_TAGS_UNSPECIFIED, \
	GIT_PROXY_OPTIONS_INIT }
GIT_EXTERN(int) git_fetch_options_init(
	git_fetch_options *opts,
	unsigned int version);
typedef struct {
	unsigned int version;
	unsigned int pb_parallelism;
	git_remote_callbacks callbacks;
	git_proxy_options proxy_opts;
	git_remote_redirect_t follow_redirects;
	git_strarray custom_headers;
	git_strarray remote_push_options;
} git_push_options;
#define GIT_PUSH_OPTIONS_VERSION 1
#define GIT_PUSH_OPTIONS_INIT { GIT_PUSH_OPTIONS_VERSION, 1, GIT_REMOTE_CALLBACKS_INIT, GIT_PROXY_OPTIONS_INIT }
GIT_EXTERN(int) git_push_options_init(
	git_push_options *opts,
	unsigned int version);
typedef struct {
	unsigned int version;
	git_remote_callbacks callbacks;
	git_proxy_options proxy_opts;
	git_remote_redirect_t follow_redirects;
	git_strarray custom_headers;
} git_remote_connect_options;
#define GIT_REMOTE_CONNECT_OPTIONS_VERSION 1
#define GIT_REMOTE_CONNECT_OPTIONS_INIT { \
	GIT_REMOTE_CONNECT_OPTIONS_VERSION, \
	GIT_REMOTE_CALLBACKS_INIT, \
	GIT_PROXY_OPTIONS_INIT }
GIT_EXTERN(int) git_remote_connect_options_init(
		git_remote_connect_options *opts,
		unsigned int version);
GIT_EXTERN(int) git_remote_connect(
	git_remote *remote,
	git_direction direction,
	const git_remote_callbacks *callbacks,
	const git_proxy_options *proxy_opts,
	const git_strarray *custom_headers);
GIT_EXTERN(int) git_remote_connect_ext(
	git_remote *remote,
	git_direction direction,
	const git_remote_connect_options *opts);
 GIT_EXTERN(int) git_remote_download(
	git_remote *remote,
	const git_strarray *refspecs,
	const git_fetch_options *opts);
GIT_EXTERN(int) git_remote_upload(
	git_remote *remote,
	const git_strarray *refspecs,
	const git_push_options *opts);
GIT_EXTERN(int) git_remote_update_tips(
		git_remote *remote,
		const git_remote_callbacks *callbacks,
		unsigned int update_flags,
		git_remote_autotag_option_t download_tags,
		const char *reflog_message);
GIT_EXTERN(int) git_remote_fetch(
		git_remote *remote,
		const git_strarray *refspecs,
		const git_fetch_options *opts,
		const char *reflog_message);
GIT_EXTERN(int) git_remote_prune(
	git_remote *remote,
	const git_remote_callbacks *callbacks);
GIT_EXTERN(int) git_remote_push(
	git_remote *remote,
	const git_strarray *refspecs,
	const git_push_options *opts);
GIT_EXTERN(const git_indexer_progress *) git_remote_stats(git_remote *remote);
GIT_EXTERN(git_remote_autotag_option_t) git_remote_autotag(const git_remote *remote);
GIT_EXTERN(int) git_remote_set_autotag(git_repository *repo, const char *remote, git_remote_autotag_option_t value);
GIT_EXTERN(int) git_remote_prune_refs(const git_remote *remote);
GIT_EXTERN(int) git_remote_rename(
	git_strarray *problems,
	git_repository *repo,
	const char *name,
	const char *new_name);
GIT_EXTERN(int) git_remote_name_is_valid(int *valid, const char *remote_name);
GIT_EXTERN(int) git_remote_delete(git_repository *repo, const char *name);
GIT_EXTERN(int) git_remote_default_branch(git_buf *out, git_remote *remote);
GIT_END_DECL
#endif
