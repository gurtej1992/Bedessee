package com.bedessee.salesca.modal

import com.google.gson.annotations.SerializedName

data class SalesPerson(@SerializedName("email"   ) var email   : String?          = null,
                       @SerializedName("APP_DISPLAY_NAME"    ) var name    : String?          = null,
                       @SerializedName("LINK_4_DATAZIP"    ) var link    : String?          = null)
