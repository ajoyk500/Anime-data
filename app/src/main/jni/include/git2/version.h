
#ifndef INCLUDE_git_version_h__
#define INCLUDE_git_version_h__
#include "common.h"
GIT_BEGIN_DECL
#define LIBGIT2_VERSION           "1.9.1"
#define LIBGIT2_VERSION_MAJOR      1
#define LIBGIT2_VERSION_MINOR      9
#define LIBGIT2_VERSION_REVISION   1
#define LIBGIT2_VERSION_PATCH      0
#define LIBGIT2_VERSION_PRERELEASE NULL
#define LIBGIT2_SOVERSION         "1.9"
#define LIBGIT2_VERSION_NUMBER (    \
    (LIBGIT2_VERSION_MAJOR * 1000000) + \
    (LIBGIT2_VERSION_MINOR * 10000) +   \
    (LIBGIT2_VERSION_REVISION * 100))
#define LIBGIT2_VERSION_CHECK(major, minor, revision) \
	(LIBGIT2_VERSION_NUMBER >= ((major)*1000000)+((minor)*10000)+((revision)*100))
GIT_END_DECL
#endif
