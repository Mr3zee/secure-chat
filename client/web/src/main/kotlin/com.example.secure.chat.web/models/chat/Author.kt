package com.example.secure.chat.web.models.chat

sealed interface Author {
    val name: String

    object Me : Author {
        override val name: String = "You"
    }

    data class User(override val name: String) : Author

    data class Bot(override val name: String) : Author
}
