package com.bedessee.sales.store

import com.google.gson.annotations.SerializedName

data class StoreCustomAttributes(
    @SerializedName("contactName") val contactName:String,
    @SerializedName("companyName") val companyName:String,
    @SerializedName("address") val address:String,
    @SerializedName("city") val city:String,
    @SerializedName("province") val province:String,
    @SerializedName("country") val country:String,
    @SerializedName("postalCode") val postalCode:String,
    @SerializedName("telephone") val telephone:String,
    @SerializedName("email") val email:String)