
#ifndef INCLUDE_git_proxy_h__
#define INCLUDE_git_proxy_h__
#include "common.h"
#include "cert.h"
#include "credential.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_PROXY_NONE,
	GIT_PROXY_AUTO,
	GIT_PROXY_SPECIFIED
} git_proxy_t;
typedef struct {
	unsigned int version;
	git_proxy_t type;
	const char *url;
	git_credential_acquire_cb credentials;
	git_transport_certificate_check_cb certificate_check;
	void *payload;
} git_proxy_options;
#define GIT_PROXY_OPTIONS_VERSION 1
#define GIT_PROXY_OPTIONS_INIT {GIT_PROXY_OPTIONS_VERSION}
GIT_EXTERN(int) git_proxy_options_init(git_proxy_options *opts, unsigned int version);
GIT_END_DECL
#endif
