
#ifndef INCLUDE_sys_git_remote_h
#define INCLUDE_sys_git_remote_h
#include "git2/remote.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_REMOTE_CAPABILITY_TIP_OID = (1 << 0),
	GIT_REMOTE_CAPABILITY_REACHABLE_OID = (1 << 1),
	GIT_REMOTE_CAPABILITY_PUSH_OPTIONS = (1 << 2),
} git_remote_capability_t;
GIT_EXTERN(void) git_remote_connect_options_dispose(
		git_remote_connect_options *opts);
GIT_END_DECL
#endif
