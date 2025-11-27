
#ifndef INCLUDE_git_transport_h__
#define INCLUDE_git_transport_h__
#include "indexer.h"
#include "net.h"
#include "types.h"
#include "cert.h"
#include "credential.h"
GIT_BEGIN_DECL
typedef int GIT_CALLBACK(git_transport_message_cb)(const char *str, int len, void *payload);
typedef int GIT_CALLBACK(git_transport_cb)(git_transport **out, git_remote *owner, void *param);
GIT_END_DECL
#endif
