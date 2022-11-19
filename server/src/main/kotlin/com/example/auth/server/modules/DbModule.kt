package com.example.auth.server.modules

import com.example.auth.server.db.connection.TableHolder
import com.example.auth.server.db.tables.UserTables
import org.koin.dsl.bind
import org.koin.dsl.module

val dbModule = module {
    single { UserTables }.bind<TableHolder>()
}
