
#ifndef INCLUDE_git_cert_h__
#define INCLUDE_git_cert_h__
#include "common.h"
#include "types.h"
GIT_BEGIN_DECL
typedef enum git_cert_t {
	GIT_CERT_NONE,
	GIT_CERT_X509,
	GIT_CERT_HOSTKEY_LIBSSH2,
	GIT_CERT_STRARRAY
} git_cert_t;
struct git_cert {
	git_cert_t cert_type;
};
typedef int GIT_CALLBACK(git_transport_certificate_check_cb)(git_cert *cert, int valid, const char *host, void *payload);
typedef enum {
	GIT_CERT_SSH_MD5 = (1 << 0),
	GIT_CERT_SSH_SHA1 = (1 << 1),
	GIT_CERT_SSH_SHA256 = (1 << 2),
	GIT_CERT_SSH_RAW = (1 << 3)
} git_cert_ssh_t;
typedef enum {
	GIT_CERT_SSH_RAW_TYPE_UNKNOWN = 0,
	GIT_CERT_SSH_RAW_TYPE_RSA = 1,
	GIT_CERT_SSH_RAW_TYPE_DSS = 2,
	GIT_CERT_SSH_RAW_TYPE_KEY_ECDSA_256 = 3,
	GIT_CERT_SSH_RAW_TYPE_KEY_ECDSA_384 = 4,
	GIT_CERT_SSH_RAW_TYPE_KEY_ECDSA_521 = 5,
	GIT_CERT_SSH_RAW_TYPE_KEY_ED25519 = 6
} git_cert_ssh_raw_type_t;
typedef struct {
	git_cert parent; 
	git_cert_ssh_t type;
	unsigned char hash_md5[16];
	unsigned char hash_sha1[20];
	unsigned char hash_sha256[32];
	git_cert_ssh_raw_type_t raw_type;
	const char *hostkey;
	size_t hostkey_len;
} git_cert_hostkey;
typedef struct {
	git_cert parent; 
	void *data;
	size_t len;
} git_cert_x509;
GIT_END_DECL
#endif
