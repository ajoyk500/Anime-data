
#ifndef INCLUDE_sys_git_transport_h
#define INCLUDE_sys_git_transport_h
#include "git2/net.h"
#include "git2/oidarray.h"
#include "git2/proxy.h"
#include "git2/remote.h"
#include "git2/strarray.h"
#include "git2/transport.h"
#include "git2/types.h"
GIT_BEGIN_DECL
typedef struct {
	const git_remote_head * const *refs;
	size_t refs_len;
	git_oid *shallow_roots;
	size_t shallow_roots_len;
	int depth;
} git_fetch_negotiation;
struct git_transport {
	unsigned int version; 
	int GIT_CALLBACK(connect)(
		git_transport *transport,
		const char *url,
		int direction,
		const git_remote_connect_options *connect_opts);
	int GIT_CALLBACK(set_connect_opts)(
		git_transport *transport,
		const git_remote_connect_options *connect_opts);
	int GIT_CALLBACK(capabilities)(
		unsigned int *capabilities,
		git_transport *transport);
#ifdef GIT_EXPERIMENTAL_SHA256
	int GIT_CALLBACK(oid_type)(
		git_oid_t *object_type,
		git_transport *transport);
#endif
	int GIT_CALLBACK(ls)(
		const git_remote_head ***out,
		size_t *size,
		git_transport *transport);
	int GIT_CALLBACK(push)(
		git_transport *transport,
		git_push *push);
	int GIT_CALLBACK(negotiate_fetch)(
		git_transport *transport,
		git_repository *repo,
		const git_fetch_negotiation *fetch_data);
	int GIT_CALLBACK(shallow_roots)(
		git_oidarray *out,
		git_transport *transport);
	int GIT_CALLBACK(download_pack)(
		git_transport *transport,
		git_repository *repo,
		git_indexer_progress *stats);
	int GIT_CALLBACK(is_connected)(git_transport *transport);
	void GIT_CALLBACK(cancel)(git_transport *transport);
	int GIT_CALLBACK(close)(git_transport *transport);
	void GIT_CALLBACK(free)(git_transport *transport);
};
#define GIT_TRANSPORT_VERSION 1
#define GIT_TRANSPORT_INIT {GIT_TRANSPORT_VERSION}
GIT_EXTERN(int) git_transport_init(
	git_transport *opts,
	unsigned int version);
GIT_EXTERN(int) git_transport_new(git_transport **out, git_remote *owner, const char *url);
GIT_EXTERN(int) git_transport_ssh_with_paths(git_transport **out, git_remote *owner, void *payload);
GIT_EXTERN(int) git_transport_register(
	const char *prefix,
	git_transport_cb cb,
	void *param);
GIT_EXTERN(int) git_transport_unregister(
	const char *prefix);
GIT_EXTERN(int) git_transport_dummy(
	git_transport **out,
	git_remote *owner,
	 void *payload);
GIT_EXTERN(int) git_transport_local(
	git_transport **out,
	git_remote *owner,
	 void *payload);
GIT_EXTERN(int) git_transport_smart(
	git_transport **out,
	git_remote *owner,
	 void *payload);
GIT_EXTERN(int) git_transport_smart_certificate_check(git_transport *transport, git_cert *cert, int valid, const char *hostname);
GIT_EXTERN(int) git_transport_smart_credentials(git_credential **out, git_transport *transport, const char *user, int methods);
GIT_EXTERN(int) git_transport_remote_connect_options(
		git_remote_connect_options *out,
		git_transport *transport);
typedef enum {
	GIT_SERVICE_UPLOADPACK_LS = 1,
	GIT_SERVICE_UPLOADPACK = 2,
	GIT_SERVICE_RECEIVEPACK_LS = 3,
	GIT_SERVICE_RECEIVEPACK = 4
} git_smart_service_t;
typedef struct git_smart_subtransport git_smart_subtransport;
typedef struct git_smart_subtransport_stream git_smart_subtransport_stream;
struct git_smart_subtransport_stream {
	git_smart_subtransport *subtransport; 
	int GIT_CALLBACK(read)(
		git_smart_subtransport_stream *stream,
		char *buffer,
		size_t buf_size,
		size_t *bytes_read);
	int GIT_CALLBACK(write)(
		git_smart_subtransport_stream *stream,
		const char *buffer,
		size_t len);
	void GIT_CALLBACK(free)(
		git_smart_subtransport_stream *stream);
};
struct git_smart_subtransport {
	int GIT_CALLBACK(action)(
			git_smart_subtransport_stream **out,
			git_smart_subtransport *transport,
			const char *url,
			git_smart_service_t action);
	int GIT_CALLBACK(close)(git_smart_subtransport *transport);
	void GIT_CALLBACK(free)(git_smart_subtransport *transport);
};
typedef int GIT_CALLBACK(git_smart_subtransport_cb)(
	git_smart_subtransport **out,
	git_transport *owner,
	void *param);
typedef struct git_smart_subtransport_definition {
	git_smart_subtransport_cb callback;
	unsigned rpc;
	void *param;
} git_smart_subtransport_definition;
GIT_EXTERN(int) git_smart_subtransport_http(
	git_smart_subtransport **out,
	git_transport *owner,
	void *param);
GIT_EXTERN(int) git_smart_subtransport_git(
	git_smart_subtransport **out,
	git_transport *owner,
	void *param);
GIT_EXTERN(int) git_smart_subtransport_ssh(
	git_smart_subtransport **out,
	git_transport *owner,
	void *param);
GIT_END_DECL
#endif
