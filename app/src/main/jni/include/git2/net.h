
#ifndef INCLUDE_git_net_h__
#define INCLUDE_git_net_h__
#include "common.h"
#include "oid.h"
#include "types.h"
GIT_BEGIN_DECL
#define GIT_DEFAULT_PORT "9418"
typedef enum {
	GIT_DIRECTION_FETCH = 0,
	GIT_DIRECTION_PUSH  = 1
} git_direction;
struct git_remote_head {
	int local; 
	git_oid oid;
	git_oid loid;
	char *name;
	char *symref_target;
};
GIT_END_DECL
#endif
