package com.example.dtos

import com.google.gson.annotations.SerializedName

data class UserDTO (
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    val email: String?,
    @SerializedName("user_id")
    var userID: Int?
)