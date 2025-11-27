
#ifndef INCLUDE_sys_git_midx_h__
#define INCLUDE_sys_git_midx_h__
#include "git2/common.h"
#include "git2/types.h"
GIT_BEGIN_DECL
typedef struct {
	unsigned int version;
#ifdef GIT_EXPERIMENTAL_SHA256
	git_oid_t oid_type;
#endif
} git_midx_writer_options;
#define GIT_MIDX_WRITER_OPTIONS_VERSION 1
#define GIT_MIDX_WRITER_OPTIONS_INIT { \
		GIT_MIDX_WRITER_OPTIONS_VERSION \
	}
GIT_EXTERN(int) git_midx_writer_options_init(
	git_midx_writer_options *opts,
	unsigned int version);
GIT_EXTERN(int) git_midx_writer_new(
		git_midx_writer **out,
		const char *pack_dir
#ifdef GIT_EXPERIMENTAL_SHA256
		, git_midx_writer_options *options
#endif
		);
GIT_EXTERN(void) git_midx_writer_free(git_midx_writer *w);
GIT_EXTERN(int) git_midx_writer_add(
		git_midx_writer *w,
		const char *idx_path);
GIT_EXTERN(int) git_midx_writer_commit(
		git_midx_writer *w);
GIT_EXTERN(int) git_midx_writer_dump(
		git_buf *midx,
		git_midx_writer *w);
GIT_END_DECL
#endif
