
#ifndef INCLUDE_sys_git_stream_h__
#define INCLUDE_sys_git_stream_h__
#include "git2/common.h"
#include "git2/types.h"
#include "git2/proxy.h"
GIT_BEGIN_DECL
#define GIT_STREAM_VERSION 1
typedef struct git_stream {
	int version;
	unsigned int encrypted : 1,
	             proxy_support : 1;
	int timeout;
	int connect_timeout;
	int GIT_CALLBACK(connect)(struct git_stream *);
	int GIT_CALLBACK(certificate)(git_cert **, struct git_stream *);
	int GIT_CALLBACK(set_proxy)(struct git_stream *, const git_proxy_options *proxy_opts);
	ssize_t GIT_CALLBACK(read)(struct git_stream *, void *, size_t);
	ssize_t GIT_CALLBACK(write)(struct git_stream *, const char *, size_t, int);
	int GIT_CALLBACK(close)(struct git_stream *);
	void GIT_CALLBACK(free)(struct git_stream *);
} git_stream;
typedef struct {
	int version;
	int GIT_CALLBACK(init)(git_stream **out, const char *host, const char *port);
	int GIT_CALLBACK(wrap)(git_stream **out, git_stream *in, const char *host);
} git_stream_registration;
typedef enum {
	GIT_STREAM_STANDARD = 1,
	GIT_STREAM_TLS = 2
} git_stream_t;
GIT_EXTERN(int) git_stream_register(
	git_stream_t type, git_stream_registration *registration);
#ifndef GIT_DEPRECATE_HARD
typedef int GIT_CALLBACK(git_stream_cb)(git_stream **out, const char *host, const char *port);
GIT_EXTERN(int) git_stream_register_tls(git_stream_cb ctor);
#endif
GIT_END_DECL
#endif
