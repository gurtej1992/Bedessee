package com.bedessee.salesca.login

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NoLogin(
    val name: String,
    val email: String,
    @SerializedName("SALES REP UPDATE WEB LINK1") val link1: String,
    @SerializedName("MENU DISPLAY1") val menuLabel1: String?,
    @SerializedName("MENU DISPLAY2") val menuLabel2: String?,
    @SerializedName("MENU DISPLAY3") val menuLabel3: String?,
    @SerializedName("SALES REP UPDATE WEB LINK2") val link2: String,
    @SerializedName("SALES REP UPDATE WEB LINK3") val link3: String,
    @SerializedName("FILE TO PROCESS FOR LINK") val fileToProcessBefore: String,
    @SerializedName("FILE TO PROCESS FOR LINK2") val fileToProcessBefore2: String,
    @SerializedName("FILE TO PROCESS FOR LINK3") val fileToProcessBefore3: String
) : Serializable