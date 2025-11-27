
#ifndef INCLUDE_git_types_h__
#define INCLUDE_git_types_h__
#include "common.h"
GIT_BEGIN_DECL
#include <sys/types.h>
#ifdef __amigaos4__
#include <stdint.h>
#endif
#if defined(_MSC_VER)
typedef __int64 git_off_t;
typedef __time64_t git_time_t;
#elif defined(__MINGW32__)
typedef off64_t git_off_t;
typedef __time64_t git_time_t;
#elif defined(__HAIKU__)
typedef __haiku_std_int64 git_off_t;
typedef __haiku_std_int64 git_time_t;
#else 
typedef int64_t git_off_t;
typedef int64_t git_time_t; 
#endif
typedef uint64_t git_object_size_t;
#include "buffer.h"
#include "oid.h"
typedef enum {
	GIT_OBJECT_ANY =      -2, 
	GIT_OBJECT_INVALID =  -1, 
	GIT_OBJECT_COMMIT =    1, 
	GIT_OBJECT_TREE =      2, 
	GIT_OBJECT_BLOB =      3, 
	GIT_OBJECT_TAG =       4, 
	GIT_OBJECT_OFS_DELTA = 6, 
	GIT_OBJECT_REF_DELTA = 7  
} git_object_t;
typedef struct git_odb git_odb;
typedef struct git_odb_backend git_odb_backend;
typedef struct git_odb_object git_odb_object;
typedef struct git_odb_stream git_odb_stream;
typedef struct git_odb_writepack git_odb_writepack;
typedef struct git_midx_writer git_midx_writer;
typedef struct git_refdb git_refdb;
typedef struct git_refdb_backend git_refdb_backend;
typedef struct git_commit_graph git_commit_graph;
typedef struct git_commit_graph_writer git_commit_graph_writer;
typedef struct git_repository git_repository;
typedef struct git_worktree git_worktree;
typedef struct git_object git_object;
typedef struct git_revwalk git_revwalk;
typedef struct git_tag git_tag;
typedef struct git_blob git_blob;
typedef struct git_commit git_commit;
typedef struct git_tree_entry git_tree_entry;
typedef struct git_tree git_tree;
typedef struct git_treebuilder git_treebuilder;
typedef struct git_index git_index;
typedef struct git_index_iterator git_index_iterator;
typedef struct git_index_conflict_iterator git_index_conflict_iterator;
typedef struct git_config git_config;
typedef struct git_config_backend git_config_backend;
typedef struct git_reflog_entry git_reflog_entry;
typedef struct git_reflog git_reflog;
typedef struct git_note git_note;
typedef struct git_packbuilder git_packbuilder;
typedef struct git_time {
	git_time_t time; 
	int offset; 
	char sign; 
} git_time;
typedef struct git_signature {
	char *name; 
	char *email; 
	git_time when; 
} git_signature;
typedef struct git_reference git_reference;
typedef struct git_reference_iterator  git_reference_iterator;
typedef struct git_transaction git_transaction;
typedef struct git_annotated_commit git_annotated_commit;
typedef struct git_status_list git_status_list;
typedef struct git_rebase git_rebase;
typedef enum {
	GIT_REFERENCE_INVALID  = 0, 
	GIT_REFERENCE_DIRECT   = 1, 
	GIT_REFERENCE_SYMBOLIC = 2, 
	GIT_REFERENCE_ALL      = GIT_REFERENCE_DIRECT | GIT_REFERENCE_SYMBOLIC
} git_reference_t;
typedef enum {
	GIT_BRANCH_LOCAL = 1,
	GIT_BRANCH_REMOTE = 2,
	GIT_BRANCH_ALL = GIT_BRANCH_LOCAL|GIT_BRANCH_REMOTE
} git_branch_t;
typedef enum {
	GIT_FILEMODE_UNREADABLE          = 0000000,
	GIT_FILEMODE_TREE                = 0040000,
	GIT_FILEMODE_BLOB                = 0100644,
	GIT_FILEMODE_BLOB_EXECUTABLE     = 0100755,
	GIT_FILEMODE_LINK                = 0120000,
	GIT_FILEMODE_COMMIT              = 0160000
} git_filemode_t;
typedef struct git_refspec git_refspec;
typedef struct git_remote git_remote;
typedef struct git_transport git_transport;
typedef struct git_push git_push;
typedef struct git_remote_head git_remote_head;
typedef struct git_remote_callbacks git_remote_callbacks;
typedef struct git_cert git_cert;
typedef struct git_submodule git_submodule;
typedef enum {
	GIT_SUBMODULE_UPDATE_CHECKOUT = 1,
	GIT_SUBMODULE_UPDATE_REBASE   = 2,
	GIT_SUBMODULE_UPDATE_MERGE    = 3,
	GIT_SUBMODULE_UPDATE_NONE     = 4,
	GIT_SUBMODULE_UPDATE_DEFAULT  = 0
} git_submodule_update_t;
typedef enum {
	GIT_SUBMODULE_IGNORE_UNSPECIFIED  = -1, 
	GIT_SUBMODULE_IGNORE_NONE      = 1,  
	GIT_SUBMODULE_IGNORE_UNTRACKED = 2,  
	GIT_SUBMODULE_IGNORE_DIRTY     = 3,  
	GIT_SUBMODULE_IGNORE_ALL       = 4   
} git_submodule_ignore_t;
typedef enum {
	GIT_SUBMODULE_RECURSE_NO = 0,
	GIT_SUBMODULE_RECURSE_YES = 1,
	GIT_SUBMODULE_RECURSE_ONDEMAND = 2
} git_submodule_recurse_t;
typedef struct git_writestream git_writestream;
struct git_writestream {
	int GIT_CALLBACK(write)(git_writestream *stream, const char *buffer, size_t len);
	int GIT_CALLBACK(close)(git_writestream *stream);
	void GIT_CALLBACK(free)(git_writestream *stream);
};
typedef struct git_mailmap git_mailmap;
GIT_END_DECL
#endif
