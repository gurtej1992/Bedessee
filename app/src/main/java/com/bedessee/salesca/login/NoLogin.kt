package com.bedessee.salesca.login

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//data class NoLogin(
//    val name: String,
//    val email: String,
//    val admin: String,
//    @SerializedName("SALES REP UPDATE WEB LINK1") val link1: String,
//    @SerializedName("MENU DISPLAY1") val menuLabel1: String?,
//    @SerializedName("MENU DISPLAY2") val menuLabel2: String?,
//    @SerializedName("MENU DISPLAY3") val menuLabel3: String?,
//    @SerializedName("MENU DISPLAY4") val menuLabel4: String?,
//    @SerializedName("MENU DISPLAY5") val menuLabel5: String?,
//    @SerializedName("MENU DISPLAY6") val menuLabel6: String?,
//    @SerializedName("MENU DISPLAY7") val menuLabel7: String?,
//    @SerializedName("MENU DISPLAY8") val menuLabel8: String?,
//    @SerializedName("SALES REP UPDATE WEB LINK2") val link2: String,
//    @SerializedName("SALES REP UPDATE WEB LINK3") val link3: String,
//    @SerializedName("SALES REP UPDATE WEB LINK4") val link4: String,
//    @SerializedName("SALES REP UPDATE WEB LINK5") val link5: String,
//    @SerializedName("SALES REP UPDATE WEB LINK6") val link6: String,
//    @SerializedName("SALES REP UPDATE WEB LINK7") val link7: String,
//    @SerializedName("SALES REP UPDATE WEB LINK8") val link8: String,
//    @SerializedName("FILE TO PROCESS FOR LINK1") val fileToProcessBefore: String,
//    @SerializedName("FILE TO PROCESS FOR LINK2") val fileToProcessBefore2: String,
//    @SerializedName("FILE TO PROCESS FOR LINK3") val fileToProcessBefore3: String,
//    @SerializedName("FILE TO PROCESS FOR LINK4") val fileToProcessBefore4: String,
//    @SerializedName("FILE TO PROCESS FOR LINK5") val fileToProcessBefore5: String,
//    @SerializedName("FILE TO PROCESS FOR LINK6") val fileToProcessBefore6: String,
//    @SerializedName("FILE TO PROCESS FOR LINK7") val fileToProcessBefore7: String,
//    @SerializedName("FILE TO PROCESS FOR LINK8") val fileToProcessBefore8: String
//
//) : Serializable


data class NoLogin( @SerializedName("email"   ) var email   : String?          = null,
                    @SerializedName("name"    ) var name    : String?          = null,
                    @SerializedName("admin"   ) var admin   : String?          = null,
                    @SerializedName("menus"   ) var menus   : ArrayList<Menus> = arrayListOf(),
                    @SerializedName("comment" ) var comment : String?          = null)

data class Menus(@SerializedName("link"         ) var link        : String?  = null,
                 @SerializedName("menu"         ) var menu        : String?  = null,
                 @SerializedName("process"      ) var process     : String?  = null,
                 @SerializedName("daily_update" ) var dailyUpdate : Boolean? = null
)

