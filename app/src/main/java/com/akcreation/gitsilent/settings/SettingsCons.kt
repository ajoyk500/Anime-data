package com.akcreation.gitsilent.settings


object SettingsCons {
    val startPageMode_rememberLastQuit = 1  
    val startPageMode_userCustom = 3  
    val defaultConflictStartStr = "<<<<<<<"  
    val defaultConfilctSplitStr = "======="  
    val defaultConflictEndStr = ">>>>>>>"  
    val defaultFontSize = 16
    val defaultLineNumFontSize = 10
    val editor_defaultFileAssociationList = listOf(
        "*.md",
        "*.txt",
        "*.log",
        "*.markdown",
        "*.ini",
        "config",
        ".gitignore",
        ".gitconfig",
        ".gitmodules",
        ".gitattributes",
        "dockerfile",
        "makefile",
        "*.xsl",
        "vagrantfile"
    )
}
