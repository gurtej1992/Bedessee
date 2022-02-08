package com.bedessee.salesca.reportsmenu


import com.google.gson.annotations.SerializedName

data class ReportsMenu(
    @SerializedName("SIDE MENU DISPLAY")
    var sideMenuDisplay: String,
    @SerializedName("DEVICE FOLDER")
    var deviceFolder: String,
    @SerializedName("POP UP TYPE")
    var popupType: String,
    @SerializedName("FILE NAME")
    var filename: String,
    @SerializedName("FILE CREATED ON")
    var fileCreated: String,
    @SerializedName("DEFAULT OPEN AFTER CUSTOMER SELECT")
    private val _defaultOpenAfterCustomerSelect: String? = "NO"
) {

    val defaultOpenAfterCustomerSelect
        get() = _defaultOpenAfterCustomerSelect ?: "NO"
}