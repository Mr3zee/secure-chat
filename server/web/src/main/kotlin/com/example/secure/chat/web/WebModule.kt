package com.example.secure.chat.web

import com.example.secure.chat.web.controller.ChatController
import com.example.secure.chat.web.controller.MessageController
import com.example.secure.chat.web.controller.UserController
import com.example.secure.chat.web.controller.impl.ChatControllerImpl
import com.example.secure.chat.web.controller.impl.MessageControllerImpl
import com.example.secure.chat.web.controller.impl.UserControllerImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val webModule = module {
    single { ChatControllerImpl }.bind<ChatController>()
    single { MessageControllerImpl }.bind<MessageController>()
    single { UserControllerImpl }.bind<UserController>()
}
