
#ifndef INCLUDE_sys_git_diff_h__
#define INCLUDE_sys_git_diff_h__
#include "git2/common.h"
#include "git2/types.h"
#include "git2/oid.h"
#include "git2/diff.h"
#include "git2/status.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_diff_print_callback__to_buf(
	const git_diff_delta *delta,
	const git_diff_hunk *hunk,
	const git_diff_line *line,
	void *payload); 
GIT_EXTERN(int) git_diff_print_callback__to_file_handle(
	const git_diff_delta *delta,
	const git_diff_hunk *hunk,
	const git_diff_line *line,
	void *payload); 
typedef struct {
	unsigned int version;
	size_t stat_calls; 
	size_t oid_calculations; 
} git_diff_perfdata;
#define GIT_DIFF_PERFDATA_VERSION 1
#define GIT_DIFF_PERFDATA_INIT {GIT_DIFF_PERFDATA_VERSION,0,0}
GIT_EXTERN(int) git_diff_get_perfdata(
	git_diff_perfdata *out, const git_diff *diff);
GIT_EXTERN(int) git_status_list_get_perfdata(
	git_diff_perfdata *out, const git_status_list *status);
GIT_END_DECL
#endif
