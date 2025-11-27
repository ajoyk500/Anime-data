
#ifndef INCLUDE_git_trace_h__
#define INCLUDE_git_trace_h__
#include "common.h"
#include "types.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_TRACE_NONE = 0,
	GIT_TRACE_FATAL = 1,
	GIT_TRACE_ERROR = 2,
	GIT_TRACE_WARN = 3,
	GIT_TRACE_INFO = 4,
	GIT_TRACE_DEBUG = 5,
	GIT_TRACE_TRACE = 6
} git_trace_level_t;
typedef void GIT_CALLBACK(git_trace_cb)(
	git_trace_level_t level,
	const char *msg);
GIT_EXTERN(int) git_trace_set(git_trace_level_t level, git_trace_cb cb);
GIT_END_DECL
#endif
