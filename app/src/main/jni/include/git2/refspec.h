
#ifndef INCLUDE_git_refspec_h__
#define INCLUDE_git_refspec_h__
#include "common.h"
#include "types.h"
#include "net.h"
#include "buffer.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_refspec_parse(git_refspec **refspec, const char *input, int is_fetch);
GIT_EXTERN(void) git_refspec_free(git_refspec *refspec);
GIT_EXTERN(const char *) git_refspec_src(const git_refspec *refspec);
GIT_EXTERN(const char *) git_refspec_dst(const git_refspec *refspec);
GIT_EXTERN(const char *) git_refspec_string(const git_refspec *refspec);
GIT_EXTERN(int) git_refspec_force(const git_refspec *refspec);
GIT_EXTERN(git_direction) git_refspec_direction(const git_refspec *spec);
GIT_EXTERN(int) git_refspec_src_matches_negative(const git_refspec *refspec, const char *refname);
GIT_EXTERN(int) git_refspec_src_matches(const git_refspec *refspec, const char *refname);
GIT_EXTERN(int) git_refspec_dst_matches(const git_refspec *refspec, const char *refname);
GIT_EXTERN(int) git_refspec_transform(git_buf *out, const git_refspec *spec, const char *name);
GIT_EXTERN(int) git_refspec_rtransform(git_buf *out, const git_refspec *spec, const char *name);
GIT_END_DECL
#endif
