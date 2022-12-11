package com.example.secure.chat.core

import com.example.secure.chat.core.service.ChatService
import com.example.secure.chat.core.service.MessageService
import com.example.secure.chat.core.service.UserService
import com.example.secure.chat.core.service.impl.ChatServiceImpl
import com.example.secure.chat.core.service.impl.MessageServiceImpl
import com.example.secure.chat.core.service.impl.UserServiceImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    single { ChatServiceImpl }.bind<ChatService>()
    single { MessageServiceImpl }.bind<MessageService>()
    single { UserServiceImpl }.bind<UserService>()
}
