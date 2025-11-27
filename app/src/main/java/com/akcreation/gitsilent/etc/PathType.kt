package com.akcreation.gitsilent.etc

import com.akcreation.gitsilent.utils.FsUtils.absolutePathPrefix
import com.akcreation.gitsilent.utils.FsUtils.contentUriPathPrefix
import com.akcreation.gitsilent.utils.FsUtils.fileUriPathPrefix

enum class PathType {
   INVALID,  
   CONTENT_URI, 
   FILE_URI,  
   ABSOLUTE  
   ;
   companion object {
      fun getType(path:String): PathType {
         return if(path.startsWith(absolutePathPrefix)) {
            ABSOLUTE
         }else if(path.startsWith(contentUriPathPrefix)) {
            CONTENT_URI
         }else if(path.startsWith(fileUriPathPrefix)) {
            FILE_URI
         }else {
            INVALID
         }
      }
   }
}
