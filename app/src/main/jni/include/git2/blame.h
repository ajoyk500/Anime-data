
#ifndef INCLUDE_git_blame_h__
#define INCLUDE_git_blame_h__
#include "common.h"
#include "oid.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_BLAME_NORMAL = 0,
	GIT_BLAME_TRACK_COPIES_SAME_FILE = (1<<0),
	GIT_BLAME_TRACK_COPIES_SAME_COMMIT_MOVES = (1<<1),
	GIT_BLAME_TRACK_COPIES_SAME_COMMIT_COPIES = (1<<2),
	GIT_BLAME_TRACK_COPIES_ANY_COMMIT_COPIES = (1<<3),
	GIT_BLAME_FIRST_PARENT = (1<<4),
	GIT_BLAME_USE_MAILMAP = (1<<5),
	GIT_BLAME_IGNORE_WHITESPACE = (1<<6)
} git_blame_flag_t;
typedef struct git_blame_options {
	unsigned int version;
	unsigned int flags;
	uint16_t min_match_characters;
	git_oid newest_commit;
	git_oid oldest_commit;
	size_t min_line;
	size_t max_line;
} git_blame_options;
#define GIT_BLAME_OPTIONS_VERSION 1
#define GIT_BLAME_OPTIONS_INIT {GIT_BLAME_OPTIONS_VERSION}
GIT_EXTERN(int) git_blame_options_init(
	git_blame_options *opts,
	unsigned int version);
typedef struct git_blame_hunk {
	size_t lines_in_hunk;
	git_oid final_commit_id;
	size_t final_start_line_number;
	git_signature *final_signature;
	git_signature *final_committer;
	git_oid orig_commit_id;
	const char *orig_path;
	size_t orig_start_line_number;
	git_signature *orig_signature;
	git_signature *orig_committer;
	const char *summary;
	char boundary;
} git_blame_hunk;
typedef struct git_blame_line {
	const char *ptr;
	size_t len;
} git_blame_line;
typedef struct git_blame git_blame;
GIT_EXTERN(size_t) git_blame_linecount(git_blame *blame);
GIT_EXTERN(size_t) git_blame_hunkcount(git_blame *blame);
GIT_EXTERN(const git_blame_hunk *) git_blame_hunk_byindex(
	git_blame *blame,
	size_t index);
GIT_EXTERN(const git_blame_hunk *) git_blame_hunk_byline(
	git_blame *blame,
	size_t lineno);
GIT_EXTERN(const git_blame_line *) git_blame_line_byindex(
	git_blame *blame,
	size_t idx);
#ifndef GIT_DEPRECATE_HARD
GIT_EXTERN(uint32_t) git_blame_get_hunk_count(git_blame *blame);
GIT_EXTERN(const git_blame_hunk *) git_blame_get_hunk_byindex(
	git_blame *blame,
	uint32_t index);
GIT_EXTERN(const git_blame_hunk *) git_blame_get_hunk_byline(
	git_blame *blame,
	size_t lineno);
#endif
GIT_EXTERN(int) git_blame_file(
	git_blame **out,
	git_repository *repo,
	const char *path,
	git_blame_options *options);
GIT_EXTERN(int) git_blame_file_from_buffer(
	git_blame **out,
	git_repository *repo,
	const char *path,
	const char *contents,
	size_t contents_len,
	git_blame_options *options);
GIT_EXTERN(int) git_blame_buffer(
	git_blame **out,
	git_blame *base,
	const char *buffer,
	size_t buffer_len);
GIT_EXTERN(void) git_blame_free(git_blame *blame);
GIT_END_DECL
#endif
