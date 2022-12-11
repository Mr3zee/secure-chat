package com.example.secure.chat.domain

import com.example.secure.chat.domain.db.connection.TableHolder
import com.example.secure.chat.domain.db.tables.ChatTables
import com.example.secure.chat.domain.db.tables.JoinTables
import com.example.secure.chat.domain.db.tables.MessageTables
import com.example.secure.chat.domain.db.tables.UserTables
import com.example.secure.chat.domain.repository.ChatRepository
import com.example.secure.chat.domain.repository.InviteRepository
import com.example.secure.chat.domain.repository.MessageRepository
import com.example.secure.chat.domain.repository.UserRepository
import com.example.secure.chat.domain.repository.impl.ChatRepositoryImpl
import com.example.secure.chat.domain.repository.impl.InviteRepositoryImpl
import com.example.secure.chat.domain.repository.impl.MessageRepositoryImpl
import com.example.secure.chat.domain.repository.impl.UserRepositoryImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val tablesModule = module {
    single { ChatTables }.bind<TableHolder>()
    single { JoinTables }.bind<TableHolder>()
    single { MessageTables }.bind<TableHolder>()
    single { UserTables }.bind<TableHolder>()
}

val repositoryModule = module {
    single { ChatRepositoryImpl }.bind<ChatRepository>()
    single { InviteRepositoryImpl }.bind<InviteRepository>()
    single { MessageRepositoryImpl }.bind<MessageRepository>()
    single { UserRepositoryImpl }.bind<UserRepository>()
}

val dbModules = listOf(
    tablesModule,
    repositoryModule,
)
