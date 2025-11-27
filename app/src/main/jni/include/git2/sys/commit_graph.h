
#ifndef INCLUDE_sys_git_commit_graph_h__
#define INCLUDE_sys_git_commit_graph_h__
#include "git2/common.h"
#include "git2/types.h"
GIT_BEGIN_DECL
typedef struct {
	unsigned int version;
#ifdef GIT_EXPERIMENTAL_SHA256
	git_oid_t oid_type;
#endif
} git_commit_graph_open_options;
#define GIT_COMMIT_GRAPH_OPEN_OPTIONS_VERSION 1
#define GIT_COMMIT_GRAPH_OPEN_OPTIONS_INIT { \
		GIT_COMMIT_GRAPH_OPEN_OPTIONS_VERSION \
	}
GIT_EXTERN(int) git_commit_graph_open_options_init(
	git_commit_graph_open_options *opts,
	unsigned int version);
GIT_EXTERN(int) git_commit_graph_open(
	git_commit_graph **cgraph_out,
	const char *objects_dir
#ifdef GIT_EXPERIMENTAL_SHA256
	, const git_commit_graph_open_options *options
#endif
	);
GIT_EXTERN(void) git_commit_graph_free(git_commit_graph *cgraph);
typedef enum {
	GIT_COMMIT_GRAPH_SPLIT_STRATEGY_SINGLE_FILE = 0
} git_commit_graph_split_strategy_t;
typedef struct {
	unsigned int version;
#ifdef GIT_EXPERIMENTAL_SHA256
	git_oid_t oid_type;
#endif
	git_commit_graph_split_strategy_t split_strategy;
	float size_multiple;
	size_t max_commits;
} git_commit_graph_writer_options;
#define GIT_COMMIT_GRAPH_WRITER_OPTIONS_VERSION 1
#define GIT_COMMIT_GRAPH_WRITER_OPTIONS_INIT { \
		GIT_COMMIT_GRAPH_WRITER_OPTIONS_VERSION \
	}
GIT_EXTERN(int) git_commit_graph_writer_options_init(
	git_commit_graph_writer_options *opts,
	unsigned int version);
GIT_EXTERN(int) git_commit_graph_writer_new(
		git_commit_graph_writer **out,
		const char *objects_info_dir,
		const git_commit_graph_writer_options *options);
GIT_EXTERN(void) git_commit_graph_writer_free(git_commit_graph_writer *w);
GIT_EXTERN(int) git_commit_graph_writer_add_index_file(
		git_commit_graph_writer *w,
		git_repository *repo,
		const char *idx_path);
GIT_EXTERN(int) git_commit_graph_writer_add_revwalk(
		git_commit_graph_writer *w,
		git_revwalk *walk);
GIT_EXTERN(int) git_commit_graph_writer_commit(
		git_commit_graph_writer *w);
GIT_EXTERN(int) git_commit_graph_writer_dump(
		git_buf *buffer,
		git_commit_graph_writer *w);
GIT_END_DECL
#endif
