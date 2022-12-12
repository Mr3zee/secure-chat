package com.example.secure.chat.core

import com.example.secure.chat.core.registry.NewInvitesRegistry
import com.example.secure.chat.core.registry.NewMessagesRegistry
import com.example.secure.chat.core.registry.Registry
import com.example.secure.chat.core.registry.impl.NewInvitesRegistryImpl
import com.example.secure.chat.core.registry.impl.NewMessagesRegistryImpl
import com.example.secure.chat.core.service.ChatService
import com.example.secure.chat.core.service.MessageService
import com.example.secure.chat.core.service.UserService
import com.example.secure.chat.core.service.impl.ChatServiceImpl
import com.example.secure.chat.core.service.impl.MessageServiceImpl
import com.example.secure.chat.core.service.impl.UserServiceImpl
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

private val serviceModule = module {
    single { ChatServiceImpl }.bind<ChatService>()
    single { MessageServiceImpl }.bind<MessageService>()
    single { UserServiceImpl }.bind<UserService>()
}

private val registryModule = module {
    single { NewMessagesRegistryImpl }.binds(
        arrayOf(
            NewMessagesRegistry::class,
            Registry::class,
        )
    )
    single { NewInvitesRegistryImpl }.binds(
        arrayOf(
            NewInvitesRegistry::class,
            Registry::class,
        )
    )
}

val coreModule = listOf(
    serviceModule,
    registryModule,
)
