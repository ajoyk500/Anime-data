package com.akcreation.gitsilent.constants


enum class LineBreak(val value: String, val visibleValue: String) {
    CR("\r", "\\r"),
    LF("\n", "\\n"),
    CRLF("\r\n", "\\r\\n")
    ;
    companion object {
        val list = listOf(CR, LF, CRLF)
        fun getType(value:String, default: LineBreak?): LineBreak? {
            return if(value == CR.value) {
                CR
            }else if(value == LF.value) {
                LF
            }else if(value == CRLF.value) {
                CRLF
            }else {
                default
            }
        }
    }
}
