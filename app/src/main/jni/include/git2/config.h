
#ifndef INCLUDE_git_config_h__
#define INCLUDE_git_config_h__
#include "common.h"
#include "types.h"
#include "buffer.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_CONFIG_LEVEL_PROGRAMDATA = 1,
	GIT_CONFIG_LEVEL_SYSTEM = 2,
	GIT_CONFIG_LEVEL_XDG = 3,
	GIT_CONFIG_LEVEL_GLOBAL = 4,
	GIT_CONFIG_LEVEL_LOCAL = 5,
	GIT_CONFIG_LEVEL_WORKTREE = 6,
	GIT_CONFIG_LEVEL_APP = 7,
	GIT_CONFIG_HIGHEST_LEVEL = -1
} git_config_level_t;
typedef struct git_config_entry {
	const char *name;
	const char *value;
	const char *backend_type;
	const char *origin_path;
	unsigned int include_depth;
	git_config_level_t level;
} git_config_entry;
GIT_EXTERN(void) git_config_entry_free(git_config_entry *entry);
typedef int GIT_CALLBACK(git_config_foreach_cb)(const git_config_entry *entry, void *payload);
typedef struct git_config_iterator git_config_iterator;
typedef enum {
	GIT_CONFIGMAP_FALSE = 0,
	GIT_CONFIGMAP_TRUE = 1,
	GIT_CONFIGMAP_INT32,
	GIT_CONFIGMAP_STRING
} git_configmap_t;
typedef struct {
	git_configmap_t type;
	const char *str_match;
	int map_value;
} git_configmap;
GIT_EXTERN(int) git_config_find_global(git_buf *out);
GIT_EXTERN(int) git_config_find_xdg(git_buf *out);
GIT_EXTERN(int) git_config_find_system(git_buf *out);
GIT_EXTERN(int) git_config_find_programdata(git_buf *out);
GIT_EXTERN(int) git_config_open_default(git_config **out);
GIT_EXTERN(int) git_config_new(git_config **out);
GIT_EXTERN(int) git_config_add_file_ondisk(
	git_config *cfg,
	const char *path,
	git_config_level_t level,
	const git_repository *repo,
	int force);
GIT_EXTERN(int) git_config_open_ondisk(git_config **out, const char *path);
GIT_EXTERN(int) git_config_open_level(
	git_config **out,
	const git_config *parent,
	git_config_level_t level);
GIT_EXTERN(int) git_config_open_global(git_config **out, git_config *config);
GIT_EXTERN(int) git_config_set_writeorder(
	git_config *cfg,
	git_config_level_t *levels,
	size_t len);
GIT_EXTERN(int) git_config_snapshot(git_config **out, git_config *config);
GIT_EXTERN(void) git_config_free(git_config *cfg);
GIT_EXTERN(int) git_config_get_entry(
	git_config_entry **out,
	const git_config *cfg,
	const char *name);
GIT_EXTERN(int) git_config_get_int32(int32_t *out, const git_config *cfg, const char *name);
GIT_EXTERN(int) git_config_get_int64(int64_t *out, const git_config *cfg, const char *name);
GIT_EXTERN(int) git_config_get_bool(int *out, const git_config *cfg, const char *name);
GIT_EXTERN(int) git_config_get_path(git_buf *out, const git_config *cfg, const char *name);
GIT_EXTERN(int) git_config_get_string(const char **out, const git_config *cfg, const char *name);
GIT_EXTERN(int) git_config_get_string_buf(git_buf *out, const git_config *cfg, const char *name);
GIT_EXTERN(int) git_config_get_multivar_foreach(const git_config *cfg, const char *name, const char *regexp, git_config_foreach_cb callback, void *payload);
GIT_EXTERN(int) git_config_multivar_iterator_new(git_config_iterator **out, const git_config *cfg, const char *name, const char *regexp);
GIT_EXTERN(int) git_config_next(git_config_entry **entry, git_config_iterator *iter);
GIT_EXTERN(void) git_config_iterator_free(git_config_iterator *iter);
GIT_EXTERN(int) git_config_set_int32(git_config *cfg, const char *name, int32_t value);
GIT_EXTERN(int) git_config_set_int64(git_config *cfg, const char *name, int64_t value);
GIT_EXTERN(int) git_config_set_bool(git_config *cfg, const char *name, int value);
GIT_EXTERN(int) git_config_set_string(git_config *cfg, const char *name, const char *value);
GIT_EXTERN(int) git_config_set_multivar(git_config *cfg, const char *name, const char *regexp, const char *value);
GIT_EXTERN(int) git_config_delete_entry(git_config *cfg, const char *name);
GIT_EXTERN(int) git_config_delete_multivar(git_config *cfg, const char *name, const char *regexp);
GIT_EXTERN(int) git_config_foreach(
	const git_config *cfg,
	git_config_foreach_cb callback,
	void *payload);
GIT_EXTERN(int) git_config_iterator_new(git_config_iterator **out, const git_config *cfg);
GIT_EXTERN(int) git_config_iterator_glob_new(git_config_iterator **out, const git_config *cfg, const char *regexp);
GIT_EXTERN(int) git_config_foreach_match(
	const git_config *cfg,
	const char *regexp,
	git_config_foreach_cb callback,
	void *payload);
GIT_EXTERN(int) git_config_get_mapped(
	int *out,
	const git_config *cfg,
	const char *name,
	const git_configmap *maps,
	size_t map_n);
GIT_EXTERN(int) git_config_lookup_map_value(
	int *out,
	const git_configmap *maps,
	size_t map_n,
	const char *value);
GIT_EXTERN(int) git_config_parse_bool(int *out, const char *value);
GIT_EXTERN(int) git_config_parse_int32(int32_t *out, const char *value);
GIT_EXTERN(int) git_config_parse_int64(int64_t *out, const char *value);
GIT_EXTERN(int) git_config_parse_path(git_buf *out, const char *value);
GIT_EXTERN(int) git_config_backend_foreach_match(
	git_config_backend *backend,
	const char *regexp,
	git_config_foreach_cb callback,
	void *payload);
GIT_EXTERN(int) git_config_lock(git_transaction **tx, git_config *cfg);
GIT_END_DECL
#endif
