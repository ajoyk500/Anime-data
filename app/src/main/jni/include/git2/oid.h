
#ifndef INCLUDE_git_oid_h__
#define INCLUDE_git_oid_h__
#include "common.h"
#include "experimental.h"
GIT_BEGIN_DECL
typedef enum {
#ifdef GIT_EXPERIMENTAL_SHA256
	GIT_OID_SHA1 = 1,  
	GIT_OID_SHA256 = 2 
#else
	GIT_OID_SHA1 = 1   
#endif
} git_oid_t;
#define GIT_OID_DEFAULT         GIT_OID_SHA1
#define GIT_OID_SHA1_SIZE       20
#define GIT_OID_SHA1_HEXSIZE   (GIT_OID_SHA1_SIZE * 2)
#ifndef GIT_EXPERIMENTAL_SHA256
# define GIT_OID_SHA1_ZERO   { { 0 } }
#else
# define GIT_OID_SHA1_ZERO   { GIT_OID_SHA1, { 0 } }
#endif
#define GIT_OID_SHA1_HEXZERO   "0000000000000000000000000000000000000000"
#ifdef GIT_EXPERIMENTAL_SHA256
# define GIT_OID_SHA256_SIZE     32
# define GIT_OID_SHA256_HEXSIZE (GIT_OID_SHA256_SIZE * 2)
# define GIT_OID_SHA256_ZERO { GIT_OID_SHA256, { 0 } }
# define GIT_OID_SHA256_HEXZERO "0000000000000000000000000000000000000000000000000000000000000000"
#endif
#ifdef GIT_EXPERIMENTAL_SHA256
# define GIT_OID_MAX_SIZE        GIT_OID_SHA256_SIZE
#else
# define GIT_OID_MAX_SIZE        GIT_OID_SHA1_SIZE
#endif
#ifdef GIT_EXPERIMENTAL_SHA256
# define GIT_OID_MAX_HEXSIZE     GIT_OID_SHA256_HEXSIZE
#else
# define GIT_OID_MAX_HEXSIZE     GIT_OID_SHA1_HEXSIZE
#endif
#define GIT_OID_MINPREFIXLEN 4
typedef struct git_oid {
#ifdef GIT_EXPERIMENTAL_SHA256
	unsigned char type;
#endif
	unsigned char id[GIT_OID_MAX_SIZE];
} git_oid;
#ifdef GIT_EXPERIMENTAL_SHA256
GIT_EXTERN(int) git_oid_fromstr(git_oid *out, const char *str, git_oid_t type);
GIT_EXTERN(int) git_oid_fromstrp(git_oid *out, const char *str, git_oid_t type);
GIT_EXTERN(int) git_oid_fromstrn(git_oid *out, const char *str, size_t length, git_oid_t type);
GIT_EXTERN(int) git_oid_fromraw(git_oid *out, const unsigned char *raw, git_oid_t type);
#else
GIT_EXTERN(int) git_oid_fromstr(git_oid *out, const char *str);
GIT_EXTERN(int) git_oid_fromstrp(git_oid *out, const char *str);
GIT_EXTERN(int) git_oid_fromstrn(git_oid *out, const char *str, size_t length);
GIT_EXTERN(int) git_oid_fromraw(git_oid *out, const unsigned char *raw);
#endif
GIT_EXTERN(int) git_oid_fmt(char *out, const git_oid *id);
GIT_EXTERN(int) git_oid_nfmt(char *out, size_t n, const git_oid *id);
GIT_EXTERN(int) git_oid_pathfmt(char *out, const git_oid *id);
GIT_EXTERN(char *) git_oid_tostr_s(const git_oid *oid);
GIT_EXTERN(char *) git_oid_tostr(char *out, size_t n, const git_oid *id);
GIT_EXTERN(int) git_oid_cpy(git_oid *out, const git_oid *src);
GIT_EXTERN(int) git_oid_cmp(const git_oid *a, const git_oid *b);
GIT_EXTERN(int) git_oid_equal(const git_oid *a, const git_oid *b);
GIT_EXTERN(int) git_oid_ncmp(const git_oid *a, const git_oid *b, size_t len);
GIT_EXTERN(int) git_oid_streq(const git_oid *id, const char *str);
GIT_EXTERN(int) git_oid_strcmp(const git_oid *id, const char *str);
GIT_EXTERN(int) git_oid_is_zero(const git_oid *id);
typedef struct git_oid_shorten git_oid_shorten;
GIT_EXTERN(git_oid_shorten *) git_oid_shorten_new(size_t min_length);
GIT_EXTERN(int) git_oid_shorten_add(git_oid_shorten *os, const char *text_id);
GIT_EXTERN(void) git_oid_shorten_free(git_oid_shorten *os);
GIT_END_DECL
#endif
