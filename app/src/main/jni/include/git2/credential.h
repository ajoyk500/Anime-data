
#ifndef INCLUDE_git_credential_h__
#define INCLUDE_git_credential_h__
#include "common.h"
GIT_BEGIN_DECL
typedef enum {
	GIT_CREDENTIAL_USERPASS_PLAINTEXT = (1u << 0),
	GIT_CREDENTIAL_SSH_KEY = (1u << 1),
	GIT_CREDENTIAL_SSH_CUSTOM = (1u << 2),
	GIT_CREDENTIAL_DEFAULT = (1u << 3),
	GIT_CREDENTIAL_SSH_INTERACTIVE = (1u << 4),
	GIT_CREDENTIAL_USERNAME = (1u << 5),
	GIT_CREDENTIAL_SSH_MEMORY = (1u << 6)
} git_credential_t;
typedef struct git_credential git_credential;
typedef struct git_credential_userpass_plaintext git_credential_userpass_plaintext;
typedef struct git_credential_username git_credential_username;
typedef struct git_credential git_credential_default;
typedef struct git_credential_ssh_key git_credential_ssh_key;
typedef struct git_credential_ssh_interactive git_credential_ssh_interactive;
typedef struct git_credential_ssh_custom git_credential_ssh_custom;
typedef int GIT_CALLBACK(git_credential_acquire_cb)(
	git_credential **out,
	const char *url,
	const char *username_from_url,
	unsigned int allowed_types,
	void *payload);
GIT_EXTERN(void) git_credential_free(git_credential *cred);
GIT_EXTERN(int) git_credential_has_username(git_credential *cred);
GIT_EXTERN(const char *) git_credential_get_username(git_credential *cred);
GIT_EXTERN(int) git_credential_userpass_plaintext_new(
	git_credential **out,
	const char *username,
	const char *password);
GIT_EXTERN(int) git_credential_default_new(git_credential **out);
GIT_EXTERN(int) git_credential_username_new(git_credential **out, const char *username);
GIT_EXTERN(int) git_credential_ssh_key_new(
	git_credential **out,
	const char *username,
	const char *publickey,
	const char *privatekey,
	const char *passphrase);
GIT_EXTERN(int) git_credential_ssh_key_memory_new(
	git_credential **out,
	const char *username,
	const char *publickey,
	const char *privatekey,
	const char *passphrase);
#ifndef LIBSSH2_VERSION
typedef struct _LIBSSH2_SESSION LIBSSH2_SESSION;
typedef struct _LIBSSH2_USERAUTH_KBDINT_PROMPT LIBSSH2_USERAUTH_KBDINT_PROMPT;
typedef struct _LIBSSH2_USERAUTH_KBDINT_RESPONSE LIBSSH2_USERAUTH_KBDINT_RESPONSE;
#endif
typedef void GIT_CALLBACK(git_credential_ssh_interactive_cb)(
	const char *name,
	int name_len,
	const char *instruction, int instruction_len,
	int num_prompts, const LIBSSH2_USERAUTH_KBDINT_PROMPT *prompts,
	LIBSSH2_USERAUTH_KBDINT_RESPONSE *responses,
	void **abstract);
GIT_EXTERN(int) git_credential_ssh_interactive_new(
	git_credential **out,
	const char *username,
	git_credential_ssh_interactive_cb prompt_callback,
	void *payload);
GIT_EXTERN(int) git_credential_ssh_key_from_agent(
	git_credential **out,
	const char *username);
typedef int GIT_CALLBACK(git_credential_sign_cb)(
	LIBSSH2_SESSION *session,
	unsigned char **sig, size_t *sig_len,
	const unsigned char *data, size_t data_len,
	void **abstract);
GIT_EXTERN(int) git_credential_ssh_custom_new(
	git_credential **out,
	const char *username,
	const char *publickey,
	size_t publickey_len,
	git_credential_sign_cb sign_callback,
	void *payload);
GIT_END_DECL
#endif
