package com.bunker.bunker.data.model

data class UserData(
    val isLogged: Boolean = false,
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val uid: String = ""
)