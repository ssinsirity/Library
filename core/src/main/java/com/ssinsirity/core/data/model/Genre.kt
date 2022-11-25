package com.ssinsirity.core.data.model

enum class Genre(
    val id: String
) {
    FICTION("1"),
    NON_FICTION("2"),
    NOVEL("3"),
    FANTASY("4"),
    ADVENTURE("5"),
    CLASSICS("6")
    ;

    companion object {
        fun findValue(name: String) = values().find { it.name == name }!!
    }
}