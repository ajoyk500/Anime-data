
#ifndef INCLUDE_git_common_h__
#define INCLUDE_git_common_h__
#include <time.h>
#include <stdlib.h>
#ifdef __cplusplus
# define GIT_BEGIN_DECL extern "C" {
# define GIT_END_DECL	}
#else
# define GIT_BEGIN_DECL 
# define GIT_END_DECL	
#endif
#if defined(_MSC_VER) && _MSC_VER < 1800
# include <stdint.h>
#elif !defined(__CLANG_INTTYPES_H)
# include <inttypes.h>
#endif
#ifdef DOCURIUM
typedef size_t size_t;
#endif
#if __GNUC__ >= 4
# define GIT_EXTERN(type) extern \
			 __attribute__((visibility("default"))) \
			 type
#elif defined(_MSC_VER)
# define GIT_EXTERN(type) __declspec(dllexport) type __cdecl
#else
# define GIT_EXTERN(type) extern type
#endif
#if defined(_MSC_VER)
# define GIT_CALLBACK(name) (__cdecl *name)
#else
# define GIT_CALLBACK(name) (*name)
#endif
#if defined(__GNUC__)
# define GIT_DEPRECATED(func) \
			 __attribute__((deprecated)) \
			 __attribute__((used)) \
			 func
#elif defined(_MSC_VER)
# define GIT_DEPRECATED(func) __declspec(deprecated) func
#else
# define GIT_DEPRECATED(func) func
#endif
#ifdef __GNUC__
# define GIT_FORMAT_PRINTF(a,b) __attribute__((format (printf, a, b)))
#else
# define GIT_FORMAT_PRINTF(a,b) 
#endif
#ifdef __amigaos4__
#include <netinet/in.h>
#endif
GIT_BEGIN_DECL
#if (defined(_WIN32) && !defined(__CYGWIN__)) || defined(AMIGA)
# define GIT_PATH_LIST_SEPARATOR ';'
#else
# define GIT_PATH_LIST_SEPARATOR ':'
#endif
#define GIT_PATH_MAX 4096
GIT_EXTERN(int) git_libgit2_version(int *major, int *minor, int *rev);
GIT_EXTERN(const char *) git_libgit2_prerelease(void);
typedef enum {
	GIT_FEATURE_THREADS        = (1 << 0),
	GIT_FEATURE_HTTPS          = (1 << 1),
	GIT_FEATURE_SSH	           = (1 << 2),
	GIT_FEATURE_NSEC           = (1 << 3),
	GIT_FEATURE_HTTP_PARSER    = (1 << 4),
	GIT_FEATURE_REGEX          = (1 << 5),
	GIT_FEATURE_I18N           = (1 << 6),
	GIT_FEATURE_AUTH_NTLM      = (1 << 7),
	GIT_FEATURE_AUTH_NEGOTIATE = (1 << 8),
	GIT_FEATURE_COMPRESSION    = (1 << 9),
	GIT_FEATURE_SHA1           = (1 << 10),
	GIT_FEATURE_SHA256         = (1 << 11)
} git_feature_t;
GIT_EXTERN(int) git_libgit2_features(void);
GIT_EXTERN(const char *) git_libgit2_feature_backend(
	git_feature_t feature);
typedef enum {
	GIT_OPT_GET_MWINDOW_SIZE,
	GIT_OPT_SET_MWINDOW_SIZE,
	GIT_OPT_GET_MWINDOW_MAPPED_LIMIT,
	GIT_OPT_SET_MWINDOW_MAPPED_LIMIT,
	GIT_OPT_GET_SEARCH_PATH,
	GIT_OPT_SET_SEARCH_PATH,
	GIT_OPT_SET_CACHE_OBJECT_LIMIT,
	GIT_OPT_SET_CACHE_MAX_SIZE,
	GIT_OPT_ENABLE_CACHING,
	GIT_OPT_GET_CACHED_MEMORY,
	GIT_OPT_GET_TEMPLATE_PATH,
	GIT_OPT_SET_TEMPLATE_PATH,
	GIT_OPT_SET_SSL_CERT_LOCATIONS,
	GIT_OPT_SET_USER_AGENT,
	GIT_OPT_ENABLE_STRICT_OBJECT_CREATION,
	GIT_OPT_ENABLE_STRICT_SYMBOLIC_REF_CREATION,
	GIT_OPT_SET_SSL_CIPHERS,
	GIT_OPT_GET_USER_AGENT,
	GIT_OPT_ENABLE_OFS_DELTA,
	GIT_OPT_ENABLE_FSYNC_GITDIR,
	GIT_OPT_GET_WINDOWS_SHAREMODE,
	GIT_OPT_SET_WINDOWS_SHAREMODE,
	GIT_OPT_ENABLE_STRICT_HASH_VERIFICATION,
	GIT_OPT_SET_ALLOCATOR,
	GIT_OPT_ENABLE_UNSAVED_INDEX_SAFETY,
	GIT_OPT_GET_PACK_MAX_OBJECTS,
	GIT_OPT_SET_PACK_MAX_OBJECTS,
	GIT_OPT_DISABLE_PACK_KEEP_FILE_CHECKS,
	GIT_OPT_ENABLE_HTTP_EXPECT_CONTINUE,
	GIT_OPT_GET_MWINDOW_FILE_LIMIT,
	GIT_OPT_SET_MWINDOW_FILE_LIMIT,
	GIT_OPT_SET_ODB_PACKED_PRIORITY,
	GIT_OPT_SET_ODB_LOOSE_PRIORITY,
	GIT_OPT_GET_EXTENSIONS,
	GIT_OPT_SET_EXTENSIONS,
	GIT_OPT_GET_OWNER_VALIDATION,
	GIT_OPT_SET_OWNER_VALIDATION,
	GIT_OPT_GET_HOMEDIR,
	GIT_OPT_SET_HOMEDIR,
	GIT_OPT_SET_SERVER_CONNECT_TIMEOUT,
	GIT_OPT_GET_SERVER_CONNECT_TIMEOUT,
	GIT_OPT_SET_SERVER_TIMEOUT,
	GIT_OPT_GET_SERVER_TIMEOUT,
	GIT_OPT_SET_USER_AGENT_PRODUCT,
	GIT_OPT_GET_USER_AGENT_PRODUCT,
	GIT_OPT_ADD_SSL_X509_CERT
} git_libgit2_opt_t;
GIT_EXTERN(int) git_libgit2_opts(int option, ...);
GIT_END_DECL
#endif
