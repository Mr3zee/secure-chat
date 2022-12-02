package com.example.secure.chat.web.models.chat

sealed interface Author {
    object Me : Author

    data class User(val username: String) : Author

    data class Bot(val botName: String) : Author
}
