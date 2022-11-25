package com.ssinsirity.library.util

object UserMode {
    var mode: Mode = Mode.LIBRARIAN

    enum class Mode {
        ANONYMOUS,
        READER,
        LIBRARIAN
    }
}