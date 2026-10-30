package com.woovi.app.ui.navigation

sealed class Route(val path: String) {
    data object Welcome : Route("welcome")
    data object Login : Route("login")
    data object Register : Route("register")
    data object Home : Route("home")
    data object Library : Route("library")
    data object AddItem : Route("add")
    data object Profile : Route("profile")
    data object Detail : Route("detail/{id}") {
        fun create(id: String) = "detail/$id"
    }
    data object Edit : Route("edit/{id}") {
        fun create(id: String) = "edit/$id"
    }
}