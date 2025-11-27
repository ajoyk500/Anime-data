
#ifndef INCLUDE_git_diff_h__
#define INCLUDE_git_diff_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "tree.h"
#include "refs.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_DIFF_NORMAL = 0,
	GIT_DIFF_REVERSE = (1u << 0),
	GIT_DIFF_INCLUDE_IGNORED = (1u << 1),
	GIT_DIFF_RECURSE_IGNORED_DIRS = (1u << 2),
	GIT_DIFF_INCLUDE_UNTRACKED = (1u << 3),
	GIT_DIFF_RECURSE_UNTRACKED_DIRS = (1u << 4),
	GIT_DIFF_INCLUDE_UNMODIFIED = (1u << 5),
	GIT_DIFF_INCLUDE_TYPECHANGE = (1u << 6),
	GIT_DIFF_INCLUDE_TYPECHANGE_TREES = (1u << 7),
	GIT_DIFF_IGNORE_FILEMODE = (1u << 8),
	GIT_DIFF_IGNORE_SUBMODULES = (1u << 9),
	GIT_DIFF_IGNORE_CASE = (1u << 10),
	GIT_DIFF_INCLUDE_CASECHANGE = (1u << 11),
	GIT_DIFF_DISABLE_PATHSPEC_MATCH = (1u << 12),
	GIT_DIFF_SKIP_BINARY_CHECK = (1u << 13),
	GIT_DIFF_ENABLE_FAST_UNTRACKED_DIRS = (1u << 14),
	GIT_DIFF_UPDATE_INDEX = (1u << 15),
	GIT_DIFF_INCLUDE_UNREADABLE = (1u << 16),
	GIT_DIFF_INCLUDE_UNREADABLE_AS_UNTRACKED = (1u << 17),
	GIT_DIFF_INDENT_HEURISTIC = (1u << 18),
	GIT_DIFF_IGNORE_BLANK_LINES = (1u << 19),
	GIT_DIFF_FORCE_TEXT = (1u << 20),
	GIT_DIFF_FORCE_BINARY = (1u << 21),
	GIT_DIFF_IGNORE_WHITESPACE = (1u << 22),
	GIT_DIFF_IGNORE_WHITESPACE_CHANGE = (1u << 23),
	GIT_DIFF_IGNORE_WHITESPACE_EOL = (1u << 24),
	GIT_DIFF_SHOW_UNTRACKED_CONTENT = (1u << 25),
	GIT_DIFF_SHOW_UNMODIFIED = (1u << 26),
	GIT_DIFF_PATIENCE = (1u << 28),
	GIT_DIFF_MINIMAL = (1u << 29),
	GIT_DIFF_SHOW_BINARY = (1u << 30)
} git_diff_option_t;
typedef struct git_diff git_diff;
typedef enum {
	GIT_DIFF_FLAG_BINARY     = (1u << 0), 
	GIT_DIFF_FLAG_NOT_BINARY = (1u << 1), 
	GIT_DIFF_FLAG_VALID_ID   = (1u << 2), 
	GIT_DIFF_FLAG_EXISTS     = (1u << 3), 
	GIT_DIFF_FLAG_VALID_SIZE = (1u << 4)  
} git_diff_flag_t;
typedef enum {
	GIT_DELTA_UNMODIFIED = 0,  
	GIT_DELTA_ADDED = 1,	   
	GIT_DELTA_DELETED = 2,	   
	GIT_DELTA_MODIFIED = 3,    
	GIT_DELTA_RENAMED = 4,     
	GIT_DELTA_COPIED = 5,      
	GIT_DELTA_IGNORED = 6,     
	GIT_DELTA_UNTRACKED = 7,   
	GIT_DELTA_TYPECHANGE = 8,  
	GIT_DELTA_UNREADABLE = 9,  
	GIT_DELTA_CONFLICTED = 10  
} git_delta_t;
typedef struct {
	git_oid            id;
	const char        *path;
	git_object_size_t  size;
	uint32_t           flags;
	uint16_t           mode;
	uint16_t           id_abbrev;
} git_diff_file;
typedef struct {
	git_delta_t   status;
	uint32_t      flags;	   
	uint16_t      similarity;  
	uint16_t      nfiles;	   
	git_diff_file old_file;
	git_diff_file new_file;
} git_diff_delta;
typedef int GIT_CALLBACK(git_diff_notify_cb)(
	const git_diff *diff_so_far,
	const git_diff_delta *delta_to_add,
	const char *matched_pathspec,
	void *payload);
typedef int GIT_CALLBACK(git_diff_progress_cb)(
	const git_diff *diff_so_far,
	const char *old_path,
	const char *new_path,
	void *payload);
typedef struct {
	unsigned int version;      
	uint32_t flags;
	git_submodule_ignore_t ignore_submodules;
	git_strarray       pathspec;
	git_diff_notify_cb   notify_cb;
	git_diff_progress_cb progress_cb;
	void                *payload;
	uint32_t    context_lines;
	uint32_t    interhunk_lines;
	git_oid_t   oid_type;
	uint16_t    id_abbrev;
	git_off_t   max_size;
	const char *old_prefix;
	const char *new_prefix;
} git_diff_options;
#define GIT_DIFF_OPTIONS_VERSION 1
#define GIT_DIFF_OPTIONS_INIT \
	{GIT_DIFF_OPTIONS_VERSION, 0, GIT_SUBMODULE_IGNORE_UNSPECIFIED, {NULL,0}, NULL, NULL, NULL, 3}
GIT_EXTERN(int) git_diff_options_init(
	git_diff_options *opts,
	unsigned int version);
typedef int GIT_CALLBACK(git_diff_file_cb)(
	const git_diff_delta *delta,
	float progress,
	void *payload);
#define GIT_DIFF_HUNK_HEADER_SIZE	128
typedef enum {
	GIT_DIFF_BINARY_NONE,
	GIT_DIFF_BINARY_LITERAL,
	GIT_DIFF_BINARY_DELTA
} git_diff_binary_t;
typedef struct {
	git_diff_binary_t type;
	const char *data;
	size_t datalen;
	size_t inflatedlen;
} git_diff_binary_file;
typedef struct {
	unsigned int contains_data;
	git_diff_binary_file old_file; 
	git_diff_binary_file new_file; 
} git_diff_binary;
typedef int GIT_CALLBACK(git_diff_binary_cb)(
	const git_diff_delta *delta,
	const git_diff_binary *binary,
	void *payload);
typedef struct {
	int    old_start;     
	int    old_lines;     
	int    new_start;     
	int    new_lines;     
	size_t header_len;    
	char   header[GIT_DIFF_HUNK_HEADER_SIZE];   
} git_diff_hunk;
typedef int GIT_CALLBACK(git_diff_hunk_cb)(
	const git_diff_delta *delta,
	const git_diff_hunk *hunk,
	void *payload);
typedef enum {
	GIT_DIFF_LINE_CONTEXT   = ' ',
	GIT_DIFF_LINE_ADDITION  = '+',
	GIT_DIFF_LINE_DELETION  = '-',
	GIT_DIFF_LINE_CONTEXT_EOFNL = '=', 
	GIT_DIFF_LINE_ADD_EOFNL = '>',     
	GIT_DIFF_LINE_DEL_EOFNL = '<',     
	GIT_DIFF_LINE_FILE_HDR  = 'F',
	GIT_DIFF_LINE_HUNK_HDR  = 'H',
	GIT_DIFF_LINE_BINARY    = 'B' 
} git_diff_line_t;
typedef struct {
	char   origin;       
	int    old_lineno;   
	int    new_lineno;   
	int    num_lines;    
	size_t content_len;  
	git_off_t content_offset; 
	const char *content; 
} git_diff_line;
typedef int GIT_CALLBACK(git_diff_line_cb)(
	const git_diff_delta *delta, 
	const git_diff_hunk *hunk,   
	const git_diff_line *line,   
	void *payload);              
typedef enum {
	GIT_DIFF_FIND_BY_CONFIG = 0,
	GIT_DIFF_FIND_RENAMES = (1u << 0),
	GIT_DIFF_FIND_RENAMES_FROM_REWRITES = (1u << 1),
	GIT_DIFF_FIND_COPIES = (1u << 2),
	GIT_DIFF_FIND_COPIES_FROM_UNMODIFIED = (1u << 3),
	GIT_DIFF_FIND_REWRITES = (1u << 4),
	GIT_DIFF_BREAK_REWRITES = (1u << 5),
	GIT_DIFF_FIND_AND_BREAK_REWRITES =
		(GIT_DIFF_FIND_REWRITES | GIT_DIFF_BREAK_REWRITES),
	GIT_DIFF_FIND_FOR_UNTRACKED = (1u << 6),
	GIT_DIFF_FIND_ALL = (0x0ff),
	GIT_DIFF_FIND_IGNORE_LEADING_WHITESPACE = 0,
	GIT_DIFF_FIND_IGNORE_WHITESPACE = (1u << 12),
	GIT_DIFF_FIND_DONT_IGNORE_WHITESPACE = (1u << 13),
	GIT_DIFF_FIND_EXACT_MATCH_ONLY = (1u << 14),
	GIT_DIFF_BREAK_REWRITES_FOR_RENAMES_ONLY  = (1u << 15),
	GIT_DIFF_FIND_REMOVE_UNMODIFIED = (1u << 16)
} git_diff_find_t;
typedef struct {
	int GIT_CALLBACK(file_signature)(
		void **out, const git_diff_file *file,
		const char *fullpath, void *payload);
	int GIT_CALLBACK(buffer_signature)(
		void **out, const git_diff_file *file,
		const char *buf, size_t buflen, void *payload);
	void GIT_CALLBACK(free_signature)(void *sig, void *payload);
	int GIT_CALLBACK(similarity)(int *score, void *siga, void *sigb, void *payload);
	void *payload;
} git_diff_similarity_metric;
typedef struct {
	unsigned int version;
	uint32_t flags;
	uint16_t rename_threshold;
	uint16_t rename_from_rewrite_threshold;
	uint16_t copy_threshold;
	uint16_t break_rewrite_threshold;
	size_t rename_limit;
	git_diff_similarity_metric *metric;
} git_diff_find_options;
#define GIT_DIFF_FIND_OPTIONS_VERSION 1
#define GIT_DIFF_FIND_OPTIONS_INIT {GIT_DIFF_FIND_OPTIONS_VERSION}
GIT_EXTERN(int) git_diff_find_options_init(
	git_diff_find_options *opts,
	unsigned int version);
GIT_EXTERN(void) git_diff_free(git_diff *diff);
GIT_EXTERN(int) git_diff_tree_to_tree(
	git_diff **diff,
	git_repository *repo,
	git_tree *old_tree,
	git_tree *new_tree,
	const git_diff_options *opts);
GIT_EXTERN(int) git_diff_tree_to_index(
	git_diff **diff,
	git_repository *repo,
	git_tree *old_tree,
	git_index *index,
	const git_diff_options *opts);
GIT_EXTERN(int) git_diff_index_to_workdir(
	git_diff **diff,
	git_repository *repo,
	git_index *index,
	const git_diff_options *opts);
GIT_EXTERN(int) git_diff_tree_to_workdir(
	git_diff **diff,
	git_repository *repo,
	git_tree *old_tree,
	const git_diff_options *opts);
GIT_EXTERN(int) git_diff_tree_to_workdir_with_index(
	git_diff **diff,
	git_repository *repo,
	git_tree *old_tree,
	const git_diff_options *opts);
GIT_EXTERN(int) git_diff_index_to_index(
	git_diff **diff,
	git_repository *repo,
	git_index *old_index,
	git_index *new_index,
	const git_diff_options *opts);
GIT_EXTERN(int) git_diff_merge(
	git_diff *onto,
	const git_diff *from);
GIT_EXTERN(int) git_diff_find_similar(
	git_diff *diff,
	const git_diff_find_options *options);
GIT_EXTERN(size_t) git_diff_num_deltas(const git_diff *diff);
GIT_EXTERN(size_t) git_diff_num_deltas_of_type(
	const git_diff *diff, git_delta_t type);
GIT_EXTERN(const git_diff_delta *) git_diff_get_delta(
	const git_diff *diff, size_t idx);
GIT_EXTERN(int) git_diff_is_sorted_icase(const git_diff *diff);
GIT_EXTERN(int) git_diff_foreach(
	git_diff *diff,
	git_diff_file_cb file_cb,
	git_diff_binary_cb binary_cb,
	git_diff_hunk_cb hunk_cb,
	git_diff_line_cb line_cb,
	void *payload);
GIT_EXTERN(char) git_diff_status_char(git_delta_t status);
typedef enum {
	GIT_DIFF_FORMAT_PATCH        = 1u, 
	GIT_DIFF_FORMAT_PATCH_HEADER = 2u, 
	GIT_DIFF_FORMAT_RAW          = 3u, 
	GIT_DIFF_FORMAT_NAME_ONLY    = 4u, 
	GIT_DIFF_FORMAT_NAME_STATUS  = 5u, 
	GIT_DIFF_FORMAT_PATCH_ID     = 6u  
} git_diff_format_t;
GIT_EXTERN(int) git_diff_print(
	git_diff *diff,
	git_diff_format_t format,
	git_diff_line_cb print_cb,
	void *payload);
GIT_EXTERN(int) git_diff_to_buf(
	git_buf *out,
	git_diff *diff,
	git_diff_format_t format);
GIT_EXTERN(int) git_diff_blobs(
	const git_blob *old_blob,
	const char *old_as_path,
	const git_blob *new_blob,
	const char *new_as_path,
	const git_diff_options *options,
	git_diff_file_cb file_cb,
	git_diff_binary_cb binary_cb,
	git_diff_hunk_cb hunk_cb,
	git_diff_line_cb line_cb,
	void *payload);
GIT_EXTERN(int) git_diff_blob_to_buffer(
	const git_blob *old_blob,
	const char *old_as_path,
	const char *buffer,
	size_t buffer_len,
	const char *buffer_as_path,
	const git_diff_options *options,
	git_diff_file_cb file_cb,
	git_diff_binary_cb binary_cb,
	git_diff_hunk_cb hunk_cb,
	git_diff_line_cb line_cb,
	void *payload);
GIT_EXTERN(int) git_diff_buffers(
	const void *old_buffer,
	size_t old_len,
	const char *old_as_path,
	const void *new_buffer,
	size_t new_len,
	const char *new_as_path,
	const git_diff_options *options,
	git_diff_file_cb file_cb,
	git_diff_binary_cb binary_cb,
	git_diff_hunk_cb hunk_cb,
	git_diff_line_cb line_cb,
	void *payload);
typedef struct {
	unsigned int version;
	git_oid_t oid_type;
} git_diff_parse_options;
#define GIT_DIFF_PARSE_OPTIONS_VERSION 1
#define GIT_DIFF_PARSE_OPTIONS_INIT \
	{ GIT_DIFF_PARSE_OPTIONS_VERSION, GIT_OID_DEFAULT }
GIT_EXTERN(int) git_diff_from_buffer(
	git_diff **out,
	const char *content,
	size_t content_len
#ifdef GIT_EXPERIMENTAL_SHA256
	, git_diff_parse_options *opts
#endif
	);
typedef struct git_diff_stats git_diff_stats;
typedef enum {
	GIT_DIFF_STATS_NONE = 0,
	GIT_DIFF_STATS_FULL = (1u << 0),
	GIT_DIFF_STATS_SHORT = (1u << 1),
	GIT_DIFF_STATS_NUMBER = (1u << 2),
	GIT_DIFF_STATS_INCLUDE_SUMMARY = (1u << 3)
} git_diff_stats_format_t;
GIT_EXTERN(int) git_diff_get_stats(
	git_diff_stats **out,
	git_diff *diff);
GIT_EXTERN(size_t) git_diff_stats_files_changed(
	const git_diff_stats *stats);
GIT_EXTERN(size_t) git_diff_stats_insertions(
	const git_diff_stats *stats);
GIT_EXTERN(size_t) git_diff_stats_deletions(
	const git_diff_stats *stats);
GIT_EXTERN(int) git_diff_stats_to_buf(
	git_buf *out,
	const git_diff_stats *stats,
	git_diff_stats_format_t format,
	size_t width);
GIT_EXTERN(void) git_diff_stats_free(git_diff_stats *stats);
typedef struct git_diff_patchid_options {
	unsigned int version;
} git_diff_patchid_options;
#define GIT_DIFF_PATCHID_OPTIONS_VERSION 1
#define GIT_DIFF_PATCHID_OPTIONS_INIT { GIT_DIFF_PATCHID_OPTIONS_VERSION }
GIT_EXTERN(int) git_diff_patchid_options_init(
	git_diff_patchid_options *opts,
	unsigned int version);
GIT_EXTERN(int) git_diff_patchid(git_oid *out, git_diff *diff, git_diff_patchid_options *opts);
GIT_END_DECL
#endif
