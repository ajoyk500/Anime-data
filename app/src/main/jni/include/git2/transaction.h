
#ifndef INCLUDE_git_transaction_h__
#define INCLUDE_git_transaction_h__
#include "common.h"
#include "types.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_transaction_new(git_transaction **out, git_repository *repo);
GIT_EXTERN(int) git_transaction_lock_ref(git_transaction *tx, const char *refname);
GIT_EXTERN(int) git_transaction_set_target(git_transaction *tx, const char *refname, const git_oid *target, const git_signature *sig, const char *msg);
GIT_EXTERN(int) git_transaction_set_symbolic_target(git_transaction *tx, const char *refname, const char *target, const git_signature *sig, const char *msg);
GIT_EXTERN(int) git_transaction_set_reflog(git_transaction *tx, const char *refname, const git_reflog *reflog);
GIT_EXTERN(int) git_transaction_remove(git_transaction *tx, const char *refname);
GIT_EXTERN(int) git_transaction_commit(git_transaction *tx);
GIT_EXTERN(void) git_transaction_free(git_transaction *tx);
GIT_END_DECL
#endif
