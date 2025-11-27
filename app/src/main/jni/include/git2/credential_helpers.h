
#ifndef INCLUDE_git_credential_helpers_h__
#define INCLUDE_git_credential_helpers_h__
#include "transport.h"
GIT_BEGIN_DECL
typedef struct git_credential_userpass_payload {
	const char *username;
	const char *password;
} git_credential_userpass_payload;
GIT_EXTERN(int) git_credential_userpass(
		git_credential **out,
		const char *url,
		const char *user_from_url,
		unsigned int allowed_types,
		void *payload);
GIT_END_DECL
#endif
