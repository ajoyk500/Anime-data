
#ifndef INCLUDE_git_attr_h__
#define INCLUDE_git_attr_h__
#include "common.h"
#include "types.h"
GIT_BEGIN_DECL
#define GIT_ATTR_IS_TRUE(attr)	(git_attr_value(attr) == GIT_ATTR_VALUE_TRUE)
#define GIT_ATTR_IS_FALSE(attr) (git_attr_value(attr) == GIT_ATTR_VALUE_FALSE)
#define GIT_ATTR_IS_UNSPECIFIED(attr) (git_attr_value(attr) == GIT_ATTR_VALUE_UNSPECIFIED)
#define GIT_ATTR_HAS_VALUE(attr) (git_attr_value(attr) == GIT_ATTR_VALUE_STRING)
typedef enum {
	GIT_ATTR_VALUE_UNSPECIFIED = 0, 
	GIT_ATTR_VALUE_TRUE,   
	GIT_ATTR_VALUE_FALSE,  
	GIT_ATTR_VALUE_STRING  
} git_attr_value_t;
GIT_EXTERN(git_attr_value_t) git_attr_value(const char *attr);
#define GIT_ATTR_CHECK_FILE_THEN_INDEX	0
#define GIT_ATTR_CHECK_INDEX_THEN_FILE	1
#define GIT_ATTR_CHECK_INDEX_ONLY	2
#define GIT_ATTR_CHECK_NO_SYSTEM        (1 << 2)
#define GIT_ATTR_CHECK_INCLUDE_HEAD     (1 << 3)
#define GIT_ATTR_CHECK_INCLUDE_COMMIT   (1 << 4)
typedef struct {
	unsigned int version;
	unsigned int flags;
#ifdef GIT_DEPRECATE_HARD
	void *reserved;
#else
	git_oid *commit_id;
#endif
	git_oid attr_commit_id;
} git_attr_options;
#define GIT_ATTR_OPTIONS_VERSION 1
#define GIT_ATTR_OPTIONS_INIT {GIT_ATTR_OPTIONS_VERSION}
GIT_EXTERN(int) git_attr_get(
	const char **value_out,
	git_repository *repo,
	uint32_t flags,
	const char *path,
	const char *name);
GIT_EXTERN(int) git_attr_get_ext(
	const char **value_out,
	git_repository *repo,
	git_attr_options *opts,
	const char *path,
	const char *name);
GIT_EXTERN(int) git_attr_get_many(
	const char **values_out,
	git_repository *repo,
	uint32_t flags,
	const char *path,
	size_t num_attr,
	const char **names);
GIT_EXTERN(int) git_attr_get_many_ext(
	const char **values_out,
	git_repository *repo,
	git_attr_options *opts,
	const char *path,
	size_t num_attr,
	const char **names);
typedef int GIT_CALLBACK(git_attr_foreach_cb)(const char *name, const char *value, void *payload);
GIT_EXTERN(int) git_attr_foreach(
	git_repository *repo,
	uint32_t flags,
	const char *path,
	git_attr_foreach_cb callback,
	void *payload);
GIT_EXTERN(int) git_attr_foreach_ext(
	git_repository *repo,
	git_attr_options *opts,
	const char *path,
	git_attr_foreach_cb callback,
	void *payload);
GIT_EXTERN(int) git_attr_cache_flush(
	git_repository *repo);
GIT_EXTERN(int) git_attr_add_macro(
	git_repository *repo,
	const char *name,
	const char *values);
GIT_END_DECL
#endif
