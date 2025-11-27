
#ifndef INCLUDE_git_tree_h__
#define INCLUDE_git_tree_h__
#include "common.h"
#include "types.h"
#include "oid.h"
#include "object.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_tree_lookup(
	git_tree **out, git_repository *repo, const git_oid *id);
GIT_EXTERN(int) git_tree_lookup_prefix(
	git_tree **out,
	git_repository *repo,
	const git_oid *id,
	size_t len);
GIT_EXTERN(void) git_tree_free(git_tree *tree);
GIT_EXTERN(const git_oid *) git_tree_id(const git_tree *tree);
GIT_EXTERN(git_repository *) git_tree_owner(const git_tree *tree);
GIT_EXTERN(size_t) git_tree_entrycount(const git_tree *tree);
GIT_EXTERN(const git_tree_entry *) git_tree_entry_byname(
	const git_tree *tree, const char *filename);
GIT_EXTERN(const git_tree_entry *) git_tree_entry_byindex(
	const git_tree *tree, size_t idx);
GIT_EXTERN(const git_tree_entry *) git_tree_entry_byid(
	const git_tree *tree, const git_oid *id);
GIT_EXTERN(int) git_tree_entry_bypath(
	git_tree_entry **out,
	const git_tree *root,
	const char *path);
GIT_EXTERN(int) git_tree_entry_dup(git_tree_entry **dest, const git_tree_entry *source);
GIT_EXTERN(void) git_tree_entry_free(git_tree_entry *entry);
GIT_EXTERN(const char *) git_tree_entry_name(const git_tree_entry *entry);
GIT_EXTERN(const git_oid *) git_tree_entry_id(const git_tree_entry *entry);
GIT_EXTERN(git_object_t) git_tree_entry_type(const git_tree_entry *entry);
GIT_EXTERN(git_filemode_t) git_tree_entry_filemode(const git_tree_entry *entry);
GIT_EXTERN(git_filemode_t) git_tree_entry_filemode_raw(const git_tree_entry *entry);
GIT_EXTERN(int) git_tree_entry_cmp(const git_tree_entry *e1, const git_tree_entry *e2);
GIT_EXTERN(int) git_tree_entry_to_object(
	git_object **object_out,
	git_repository *repo,
	const git_tree_entry *entry);
GIT_EXTERN(int) git_treebuilder_new(
	git_treebuilder **out, git_repository *repo, const git_tree *source);
GIT_EXTERN(int) git_treebuilder_clear(git_treebuilder *bld);
GIT_EXTERN(size_t) git_treebuilder_entrycount(git_treebuilder *bld);
GIT_EXTERN(void) git_treebuilder_free(git_treebuilder *bld);
GIT_EXTERN(const git_tree_entry *) git_treebuilder_get(
	git_treebuilder *bld, const char *filename);
GIT_EXTERN(int) git_treebuilder_insert(
	const git_tree_entry **out,
	git_treebuilder *bld,
	const char *filename,
	const git_oid *id,
	git_filemode_t filemode);
GIT_EXTERN(int) git_treebuilder_remove(
	git_treebuilder *bld, const char *filename);
typedef int GIT_CALLBACK(git_treebuilder_filter_cb)(
	const git_tree_entry *entry, void *payload);
GIT_EXTERN(int) git_treebuilder_filter(
	git_treebuilder *bld,
	git_treebuilder_filter_cb filter,
	void *payload);
GIT_EXTERN(int) git_treebuilder_write(
	git_oid *id, git_treebuilder *bld);
typedef int GIT_CALLBACK(git_treewalk_cb)(
	const char *root, const git_tree_entry *entry, void *payload);
typedef enum {
	GIT_TREEWALK_PRE = 0, 
	GIT_TREEWALK_POST = 1 
} git_treewalk_mode;
GIT_EXTERN(int) git_tree_walk(
	const git_tree *tree,
	git_treewalk_mode mode,
	git_treewalk_cb callback,
	void *payload);
GIT_EXTERN(int) git_tree_dup(git_tree **out, git_tree *source);
typedef enum {
	GIT_TREE_UPDATE_UPSERT,
	GIT_TREE_UPDATE_REMOVE
} git_tree_update_t;
typedef struct {
	git_tree_update_t action;
	git_oid id;
	git_filemode_t filemode;
	const char *path;
} git_tree_update;
GIT_EXTERN(int) git_tree_create_updated(git_oid *out, git_repository *repo, git_tree *baseline, size_t nupdates, const git_tree_update *updates);
GIT_END_DECL
#endif
