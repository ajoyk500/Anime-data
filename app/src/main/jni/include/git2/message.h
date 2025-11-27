
#ifndef INCLUDE_git_message_h__
#define INCLUDE_git_message_h__
#include "common.h"
#include "buffer.h"
GIT_BEGIN_DECL
GIT_EXTERN(int) git_message_prettify(git_buf *out, const char *message, int strip_comments, char comment_char);
typedef struct {
  const char *key;
  const char *value;
} git_message_trailer;
typedef struct {
  git_message_trailer *trailers;
  size_t count;
  char *_trailer_block;
} git_message_trailer_array;
GIT_EXTERN(int) git_message_trailers(git_message_trailer_array *arr, const char *message);
GIT_EXTERN(void) git_message_trailer_array_free(git_message_trailer_array *arr);
GIT_END_DECL
#endif
