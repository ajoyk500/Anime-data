
#ifndef INCLUDE_sys_git_credential_h__
#define INCLUDE_sys_git_credential_h__
#include "git2/common.h"
#include "git2/credential.h"
GIT_BEGIN_DECL
struct git_credential {
	git_credential_t credtype; 
	void GIT_CALLBACK(free)(git_credential *cred);
};
struct git_credential_userpass_plaintext {
	git_credential parent; 
	char *username;        
	char *password;        
};
struct git_credential_username {
	git_credential parent; 
	char username[1];      
};
struct git_credential_ssh_key {
	git_credential parent; 
	char *username;        
	char *publickey;       
	char *privatekey;      
	char *passphrase;      
};
struct git_credential_ssh_interactive {
	git_credential parent; 
	char *username;        
	git_credential_ssh_interactive_cb prompt_callback;
	void *payload;         
};
struct git_credential_ssh_custom {
	git_credential parent; 
	char *username;        
	char *publickey;       
	size_t publickey_len;  
	git_credential_sign_cb sign_callback;
	void *payload;         
};
GIT_END_DECL
#endif
