
#ifndef INCLUDE_sys_git_path_h__
#define INCLUDE_sys_git_path_h__
#include "git2/common.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_PATH_GITFILE_GITIGNORE,
	GIT_PATH_GITFILE_GITMODULES,
	GIT_PATH_GITFILE_GITATTRIBUTES
} git_path_gitfile;
typedef enum {
	GIT_PATH_FS_GENERIC,
	GIT_PATH_FS_NTFS,
	GIT_PATH_FS_HFS
} git_path_fs;
GIT_EXTERN(int) git_path_is_gitfile(const char *path, size_t pathlen, git_path_gitfile gitfile, git_path_fs fs);
GIT_END_DECL
#endif
