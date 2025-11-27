
#ifndef INCLUDE_git_index_h__
#define INCLUDE_git_index_h__
#include "common.h"
#include "indexer.h"
#include "types.h"
#include "oid.h"
#include "strarray.h"
GIT_BEGIN_DECL
typedef struct {
	int32_t seconds;
	uint32_t nanoseconds;
} git_index_time;
typedef struct git_index_entry {
	git_index_time ctime;
	git_index_time mtime;
	uint32_t dev;
	uint32_t ino;
	uint32_t mode;
	uint32_t uid;
	uint32_t gid;
	uint32_t file_size;
	git_oid id;
	uint16_t flags;
	uint16_t flags_extended;
	const char *path;
} git_index_entry;
#define GIT_INDEX_ENTRY_NAMEMASK  (0x0fff)
#define GIT_INDEX_ENTRY_STAGEMASK (0x3000)
#define GIT_INDEX_ENTRY_STAGESHIFT 12
typedef enum {
	GIT_INDEX_ENTRY_EXTENDED  = (0x4000),
	GIT_INDEX_ENTRY_VALID     = (0x8000)
} git_index_entry_flag_t;
#define GIT_INDEX_ENTRY_STAGE(E) \
	(((E)->flags & GIT_INDEX_ENTRY_STAGEMASK) >> GIT_INDEX_ENTRY_STAGESHIFT)
#define GIT_INDEX_ENTRY_STAGE_SET(E,S) do { \
	(E)->flags = ((E)->flags & ~GIT_INDEX_ENTRY_STAGEMASK) | \
		(((S) & 0x03) << GIT_INDEX_ENTRY_STAGESHIFT); } while (0)
typedef enum {
	GIT_INDEX_ENTRY_INTENT_TO_ADD  =  (1 << 13),
	GIT_INDEX_ENTRY_SKIP_WORKTREE  =  (1 << 14),
	GIT_INDEX_ENTRY_EXTENDED_FLAGS =  (GIT_INDEX_ENTRY_INTENT_TO_ADD | GIT_INDEX_ENTRY_SKIP_WORKTREE),
	GIT_INDEX_ENTRY_UPTODATE       =  (1 << 2)
} git_index_entry_extended_flag_t;
typedef enum {
	GIT_INDEX_CAPABILITY_IGNORE_CASE = 1,
	GIT_INDEX_CAPABILITY_NO_FILEMODE = 2,
	GIT_INDEX_CAPABILITY_NO_SYMLINKS = 4,
	GIT_INDEX_CAPABILITY_FROM_OWNER  = -1
} git_index_capability_t;
typedef int GIT_CALLBACK(git_index_matched_path_cb)(
	const char *path, const char *matched_pathspec, void *payload);
typedef enum {
	GIT_INDEX_ADD_DEFAULT = 0,
	GIT_INDEX_ADD_FORCE = (1u << 0),
	GIT_INDEX_ADD_DISABLE_PATHSPEC_MATCH = (1u << 1),
	GIT_INDEX_ADD_CHECK_PATHSPEC = (1u << 2)
} git_index_add_option_t;
typedef enum {
	GIT_INDEX_STAGE_ANY = -1,
	GIT_INDEX_STAGE_NORMAL = 0,
	GIT_INDEX_STAGE_ANCESTOR = 1,
	GIT_INDEX_STAGE_OURS = 2,
	GIT_INDEX_STAGE_THEIRS = 3
} git_index_stage_t;
#ifdef GIT_EXPERIMENTAL_SHA256
typedef struct git_index_options {
	unsigned int version; 
	git_oid_t oid_type;
} git_index_options;
#define GIT_INDEX_OPTIONS_VERSION 1
#define GIT_INDEX_OPTIONS_INIT { GIT_INDEX_OPTIONS_VERSION }
GIT_EXTERN(int) git_index_options_init(
	git_index_options *opts,
	unsigned int version);
GIT_EXTERN(int) git_index_open(
	git_index **index_out,
	const char *index_path,
	const git_index_options *opts);
GIT_EXTERN(int) git_index_new(git_index **index_out, const git_index_options *opts);
#else
GIT_EXTERN(int) git_index_open(git_index **index_out, const char *index_path);
GIT_EXTERN(int) git_index_new(git_index **index_out);
#endif
GIT_EXTERN(void) git_index_free(git_index *index);
GIT_EXTERN(git_repository *) git_index_owner(const git_index *index);
GIT_EXTERN(int) git_index_caps(const git_index *index);
GIT_EXTERN(int) git_index_set_caps(git_index *index, int caps);
GIT_EXTERN(unsigned int) git_index_version(git_index *index);
GIT_EXTERN(int) git_index_set_version(git_index *index, unsigned int version);
GIT_EXTERN(int) git_index_read(git_index *index, int force);
GIT_EXTERN(int) git_index_write(git_index *index);
GIT_EXTERN(const char *) git_index_path(const git_index *index);
#ifndef GIT_DEPRECATE_HARD
GIT_EXTERN(const git_oid *) git_index_checksum(git_index *index);
#endif
GIT_EXTERN(int) git_index_read_tree(git_index *index, const git_tree *tree);
GIT_EXTERN(int) git_index_write_tree(git_oid *out, git_index *index);
GIT_EXTERN(int) git_index_write_tree_to(git_oid *out, git_index *index, git_repository *repo);
GIT_EXTERN(size_t) git_index_entrycount(const git_index *index);
GIT_EXTERN(int) git_index_clear(git_index *index);
GIT_EXTERN(const git_index_entry *) git_index_get_byindex(
	git_index *index, size_t n);
GIT_EXTERN(const git_index_entry *) git_index_get_bypath(
	git_index *index, const char *path, int stage);
GIT_EXTERN(int) git_index_remove(git_index *index, const char *path, int stage);
GIT_EXTERN(int) git_index_remove_directory(
	git_index *index, const char *dir, int stage);
GIT_EXTERN(int) git_index_add(git_index *index, const git_index_entry *source_entry);
GIT_EXTERN(int) git_index_entry_stage(const git_index_entry *entry);
GIT_EXTERN(int) git_index_entry_is_conflict(const git_index_entry *entry);
GIT_EXTERN(int) git_index_iterator_new(
	git_index_iterator **iterator_out,
	git_index *index);
GIT_EXTERN(int) git_index_iterator_next(
	const git_index_entry **out,
	git_index_iterator *iterator);
GIT_EXTERN(void) git_index_iterator_free(git_index_iterator *iterator);
GIT_EXTERN(int) git_index_add_bypath(git_index *index, const char *path);
GIT_EXTERN(int) git_index_add_from_buffer(
	git_index *index,
	const git_index_entry *entry,
	const void *buffer, size_t len);
GIT_EXTERN(int) git_index_remove_bypath(git_index *index, const char *path);
GIT_EXTERN(int) git_index_add_all(
	git_index *index,
	const git_strarray *pathspec,
	unsigned int flags,
	git_index_matched_path_cb callback,
	void *payload);
GIT_EXTERN(int) git_index_remove_all(
	git_index *index,
	const git_strarray *pathspec,
	git_index_matched_path_cb callback,
	void *payload);
GIT_EXTERN(int) git_index_update_all(
	git_index *index,
	const git_strarray *pathspec,
	git_index_matched_path_cb callback,
	void *payload);
GIT_EXTERN(int) git_index_find(size_t *at_pos, git_index *index, const char *path);
GIT_EXTERN(int) git_index_find_prefix(size_t *at_pos, git_index *index, const char *prefix);
GIT_EXTERN(int) git_index_conflict_add(
	git_index *index,
	const git_index_entry *ancestor_entry,
	const git_index_entry *our_entry,
	const git_index_entry *their_entry);
GIT_EXTERN(int) git_index_conflict_get(
	const git_index_entry **ancestor_out,
	const git_index_entry **our_out,
	const git_index_entry **their_out,
	git_index *index,
	const char *path);
GIT_EXTERN(int) git_index_conflict_remove(git_index *index, const char *path);
GIT_EXTERN(int) git_index_conflict_cleanup(git_index *index);
GIT_EXTERN(int) git_index_has_conflicts(const git_index *index);
GIT_EXTERN(int) git_index_conflict_iterator_new(
	git_index_conflict_iterator **iterator_out,
	git_index *index);
GIT_EXTERN(int) git_index_conflict_next(
	const git_index_entry **ancestor_out,
	const git_index_entry **our_out,
	const git_index_entry **their_out,
	git_index_conflict_iterator *iterator);
GIT_EXTERN(void) git_index_conflict_iterator_free(
	git_index_conflict_iterator *iterator);
GIT_END_DECL
#endif
