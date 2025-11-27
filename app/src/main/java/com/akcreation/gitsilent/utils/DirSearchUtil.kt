package com.akcreation.gitsilent.utils

import java.io.File

object DirSearchUtil {
    fun recursiveFakeBreadthFirstSearch(
        dir: File,
        match: (srcIdx:Int, srcItem: File) -> Boolean,
        matchedCallback: (srcIdx:Int, srcItem: File) -> Unit,
        canceled: () -> Boolean
    ) {
        if(canceled()) {
            return
        }
        val files = dir.listFiles()
        if(files == null || files.isEmpty()) {
            return
        }
        val subdirs = mutableListOf<File>()
        for((idx, f) in files.withIndex()) {
            if(canceled()) {
                return
            }
            if(match(idx, f)) {
                matchedCallback(idx, f)
            }
            if(f.isDirectory) {
                subdirs.add(f)
            }
        }
        for(sub in subdirs) {
            recursiveFakeBreadthFirstSearch(dir = sub, match = match, matchedCallback=matchedCallback, canceled = canceled)
        }
    }
    fun realBreadthFirstSearch(
        dir: File,
        match: (srcIdx:Int, srcItem: File) -> Boolean,
        matchedCallback: (srcIdx:Int, srcItem: File) -> Unit,
        canceled: () -> Boolean
    ) {
        if(canceled()) {
            return
        }
        val subDirs = mutableListOf<File>()
        addAllFilesToList(dir, subDirs)
        while (subDirs.isNotEmpty()) {
            val subDirsCopy = subDirs.toList()
            subDirs.clear()
            for((idx, f) in subDirsCopy.withIndex()) {
                if(canceled()) {
                    return
                }
                if(match(idx, f)) {
                    matchedCallback(idx, f)
                }
                if(f.isDirectory) {
                    addAllFilesToList(f, subDirs)
                }
            }
        }
    }
    private fun addAllFilesToList(
        dir:File,
        subDirs:MutableList<File>,
    ) {
        dir.listFiles()?.let {
            if(it.isNotEmpty()) {
                subDirs.addAll(it)
            }
        }
    }
}
