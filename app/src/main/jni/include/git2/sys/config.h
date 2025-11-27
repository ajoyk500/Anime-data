
#ifndef INCLUDE_sys_git_config_backend_h__
#define INCLUDE_sys_git_config_backend_h__
#include "git2/common.h"
#include "git2/types.h"
#include "git2/config.h"
GIT_BEGIN_DECL
typedef struct git_config_backend_entry {
	struct git_config_entry entry;
	void GIT_CALLBACK(free)(struct git_config_backend_entry *entry);
} git_config_backend_entry;
struct git_config_iterator {
	git_config_backend *backend;
	unsigned int flags;
	int GIT_CALLBACK(next)(git_config_backend_entry **entry, git_config_iterator *iter);
	void GIT_CALLBACK(free)(git_config_iterator *iter);
};
struct git_config_backend {
	unsigned int version;
	int readonly;
	struct git_config *cfg;
	int GIT_CALLBACK(open)(struct git_config_backend *, git_config_level_t level, const git_repository *repo);
	int GIT_CALLBACK(get)(struct git_config_backend *, const char *key, git_config_backend_entry **entry);
	int GIT_CALLBACK(set)(struct git_config_backend *, const char *key, const char *value);
	int GIT_CALLBACK(set_multivar)(git_config_backend *cfg, const char *name, const char *regexp, const char *value);
	int GIT_CALLBACK(del)(struct git_config_backend *, const char *key);
	int GIT_CALLBACK(del_multivar)(struct git_config_backend *, const char *key, const char *regexp);
	int GIT_CALLBACK(iterator)(git_config_iterator **, struct git_config_backend *);
	int GIT_CALLBACK(snapshot)(struct git_config_backend **, struct git_config_backend *);
	int GIT_CALLBACK(lock)(struct git_config_backend *);
	int GIT_CALLBACK(unlock)(struct git_config_backend *, int success);
	void GIT_CALLBACK(free)(struct git_config_backend *);
};
#define GIT_CONFIG_BACKEND_VERSION 1
#define GIT_CONFIG_BACKEND_INIT {GIT_CONFIG_BACKEND_VERSION}
GIT_EXTERN(int) git_config_init_backend(
	git_config_backend *backend,
	unsigned int version);
GIT_EXTERN(int) git_config_add_backend(
	git_config *cfg,
	git_config_backend *file,
	git_config_level_t level,
	const git_repository *repo,
	int force);
typedef struct {
	unsigned int version;
	const char *backend_type;
	const char *origin_path;
} git_config_backend_memory_options;
#define GIT_CONFIG_BACKEND_MEMORY_OPTIONS_VERSION 1
#define GIT_CONFIG_BACKEND_MEMORY_OPTIONS_INIT { GIT_CONFIG_BACKEND_MEMORY_OPTIONS_VERSION }
extern int git_config_backend_from_string(
	git_config_backend **out,
	const char *cfg,
	size_t len,
	git_config_backend_memory_options *opts);
extern int git_config_backend_from_values(
	git_config_backend **out,
	const char **values,
	size_t len,
	git_config_backend_memory_options *opts);
GIT_END_DECL
#endif
