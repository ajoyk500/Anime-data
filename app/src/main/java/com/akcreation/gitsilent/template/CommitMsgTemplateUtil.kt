package com.akcreation.gitsilent.template

import com.akcreation.gitsilent.git.StatusTypeEntrySaver
import com.akcreation.gitsilent.utils.Libgit2Helper
import com.akcreation.gitsilent.utils.getFormatTimeFromSec
import com.akcreation.gitsilent.utils.getSecFromTime
import com.github.git24j.core.Repository
import java.time.format.DateTimeFormatter

object CommitMsgTemplateUtil {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val datePlaceHolder = PlaceHolder(pattern = "{{date}}", example = "2024-05-20")
    val timePlaceHolder = PlaceHolder(pattern = "{{time}}", example = "22:10:05")
    val usernamePlaceHolder = PlaceHolder(pattern = "{{username}}", example = "Tony")
    val emailPlaceHolder = PlaceHolder(pattern = "{{email}}", example = "tony@example.com")
    val filesCountPlaceHolder = PlaceHolder(pattern = "{{filesCount}}", example = "5")
    val fileNamesPlaceHolder = PlaceHolder(pattern = "{{fileNames}}", example = "file1.txt, file2.txt")
    val phList = listOf(
        datePlaceHolder,
        timePlaceHolder,
        usernamePlaceHolder,
        emailPlaceHolder,
        filesCountPlaceHolder,
        fileNamesPlaceHolder,
    )
    fun replace(
        repo:Repository,
        itemList: List<StatusTypeEntrySaver>?,
        msgTemplate:String,
    ):String {
        val (username, email) = Libgit2Helper.getGitUsernameAndEmail(repo)
        val now = getSecFromTime()
        return msgTemplate.replace(datePlaceHolder.pattern, getFormatTimeFromSec(now, dateFormatter))
            .replace(timePlaceHolder.pattern, getFormatTimeFromSec(now, timeFormatter))
            .replace(usernamePlaceHolder.pattern, username)
            .replace(emailPlaceHolder.pattern, email)
            .replace(filesCountPlaceHolder.pattern, ""+(itemList?.size?:0))
            .replace(fileNamesPlaceHolder.pattern,
                if(itemList.isNullOrEmpty()) {
                    ""
                }else {
                    genFileNames(itemList)
                }
            )
    }
    fun genFileNames(itemList:List<StatusTypeEntrySaver>, limitCharsLen:Int = 200): String {
        val split = ", "
        var count = 0;  
        val allFilesCount = itemList.size
        val out = StringBuilder()
        for(item in itemList) {  
            out.append(item.fileName).append(split)
            ++count
            if(out.length > limitCharsLen) {
                out.append("...omitted ${allFilesCount - count} file(s)")
                break
            }
        }
        return out.removeSuffix(split).toString()
    }
}
