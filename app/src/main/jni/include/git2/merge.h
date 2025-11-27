
#ifndef INCLUDE_git_merge_h__
#define INCLUDE_git_merge_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "oidarray.h"
#include "checkout.h"
#include "index.h"
#include "annotated_commit.h"
GIT_BEGIN_DECL
typedef struct {
	unsigned int version;
	const char *ptr;
	size_t size;
	const char *path;
	unsigned int mode;
} git_merge_file_input;
#define GIT_MERGE_FILE_INPUT_VERSION 1
#define GIT_MERGE_FILE_INPUT_INIT {GIT_MERGE_FILE_INPUT_VERSION}
GIT_EXTERN(int) git_merge_file_input_init(
	git_merge_file_input *opts,
	unsigned int version);
typedef enum {
	GIT_MERGE_FIND_RENAMES = (1 << 0),
	GIT_MERGE_FAIL_ON_CONFLICT = (1 << 1),
	GIT_MERGE_SKIP_REUC = (1 << 2),
	GIT_MERGE_NO_RECURSIVE = (1 << 3),
	GIT_MERGE_VIRTUAL_BASE = (1 << 4)
} git_merge_flag_t;
typedef enum {
	GIT_MERGE_FILE_FAVOR_NORMAL = 0,
	GIT_MERGE_FILE_FAVOR_OURS = 1,
	GIT_MERGE_FILE_FAVOR_THEIRS = 2,
	GIT_MERGE_FILE_FAVOR_UNION = 3
} git_merge_file_favor_t;
typedef enum {
	GIT_MERGE_FILE_DEFAULT = 0,
	GIT_MERGE_FILE_STYLE_MERGE = (1 << 0),
	GIT_MERGE_FILE_STYLE_DIFF3 = (1 << 1),
	GIT_MERGE_FILE_SIMPLIFY_ALNUM = (1 << 2),
	GIT_MERGE_FILE_IGNORE_WHITESPACE = (1 << 3),
	GIT_MERGE_FILE_IGNORE_WHITESPACE_CHANGE = (1 << 4),
	GIT_MERGE_FILE_IGNORE_WHITESPACE_EOL = (1 << 5),
	GIT_MERGE_FILE_DIFF_PATIENCE = (1 << 6),
	GIT_MERGE_FILE_DIFF_MINIMAL = (1 << 7),
	GIT_MERGE_FILE_STYLE_ZDIFF3 = (1 << 8),
	GIT_MERGE_FILE_ACCEPT_CONFLICTS = (1 << 9)
} git_merge_file_flag_t;
#define GIT_MERGE_CONFLICT_MARKER_SIZE	7
typedef struct {
	unsigned int version;
	const char *ancestor_label;
	const char *our_label;
	const char *their_label;
	git_merge_file_favor_t favor;
	uint32_t flags;
	unsigned short marker_size;
} git_merge_file_options;
#define GIT_MERGE_FILE_OPTIONS_VERSION 1
#define GIT_MERGE_FILE_OPTIONS_INIT {GIT_MERGE_FILE_OPTIONS_VERSION}
GIT_EXTERN(int) git_merge_file_options_init(git_merge_file_options *opts, unsigned int version);
typedef struct {
	unsigned int automergeable;
	const char *path;
	unsigned int mode;
	const char *ptr;
	size_t len;
} git_merge_file_result;
typedef struct {
	unsigned int version;
	uint32_t flags;
	unsigned int rename_threshold;
	unsigned int target_limit;
	git_diff_similarity_metric *metric;
	unsigned int recursion_limit;
	const char *default_driver;
	git_merge_file_favor_t file_favor;
	uint32_t file_flags;
} git_merge_options;
#define GIT_MERGE_OPTIONS_VERSION 1
#define GIT_MERGE_OPTIONS_INIT { \
	GIT_MERGE_OPTIONS_VERSION, GIT_MERGE_FIND_RENAMES }
GIT_EXTERN(int) git_merge_options_init(git_merge_options *opts, unsigned int version);
typedef enum {
	GIT_MERGE_ANALYSIS_NONE = 0,
	GIT_MERGE_ANALYSIS_NORMAL = (1 << 0),
	GIT_MERGE_ANALYSIS_UP_TO_DATE = (1 << 1),
	GIT_MERGE_ANALYSIS_FASTFORWARD = (1 << 2),
	GIT_MERGE_ANALYSIS_UNBORN = (1 << 3)
} git_merge_analysis_t;
typedef enum {
	GIT_MERGE_PREFERENCE_NONE = 0,
	GIT_MERGE_PREFERENCE_NO_FASTFORWARD = (1 << 0),
	GIT_MERGE_PREFERENCE_FASTFORWARD_ONLY = (1 << 1)
} git_merge_preference_t;
GIT_EXTERN(int) git_merge_analysis(
	git_merge_analysis_t *analysis_out,
	git_merge_preference_t *preference_out,
	git_repository *repo,
	const git_annotated_commit **their_heads,
	size_t their_heads_len);
GIT_EXTERN(int) git_merge_analysis_for_ref(
	git_merge_analysis_t *analysis_out,
	git_merge_preference_t *preference_out,
	git_repository *repo,
	git_reference *our_ref,
	const git_annotated_commit **their_heads,
	size_t their_heads_len);
GIT_EXTERN(int) git_merge_base(
	git_oid *out,
	git_repository *repo,
	const git_oid *one,
	const git_oid *two);
GIT_EXTERN(int) git_merge_bases(
	git_oidarray *out,
	git_repository *repo,
	const git_oid *one,
	const git_oid *two);
GIT_EXTERN(int) git_merge_base_many(
	git_oid *out,
	git_repository *repo,
	size_t length,
	const git_oid input_array[]);
GIT_EXTERN(int) git_merge_bases_many(
	git_oidarray *out,
	git_repository *repo,
	size_t length,
	const git_oid input_array[]);
GIT_EXTERN(int) git_merge_base_octopus(
	git_oid *out,
	git_repository *repo,
	size_t length,
	const git_oid input_array[]);
GIT_EXTERN(int) git_merge_file(
	git_merge_file_result *out,
	const git_merge_file_input *ancestor,
	const git_merge_file_input *ours,
	const git_merge_file_input *theirs,
	const git_merge_file_options *opts);
GIT_EXTERN(int) git_merge_file_from_index(
	git_merge_file_result *out,
	git_repository *repo,
	const git_index_entry *ancestor,
	const git_index_entry *ours,
	const git_index_entry *theirs,
	const git_merge_file_options *opts);
GIT_EXTERN(void) git_merge_file_result_free(git_merge_file_result *result);
GIT_EXTERN(int) git_merge_trees(
	git_index **out,
	git_repository *repo,
	const git_tree *ancestor_tree,
	const git_tree *our_tree,
	const git_tree *their_tree,
	const git_merge_options *opts);
GIT_EXTERN(int) git_merge_commits(
	git_index **out,
	git_repository *repo,
	const git_commit *our_commit,
	const git_commit *their_commit,
	const git_merge_options *opts);
GIT_EXTERN(int) git_merge(
	git_repository *repo,
	const git_annotated_commit **their_heads,
	size_t their_heads_len,
	const git_merge_options *merge_opts,
	const git_checkout_options *checkout_opts);
GIT_END_DECL
#endif
