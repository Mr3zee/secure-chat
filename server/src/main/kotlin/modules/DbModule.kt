package modules

import db.connection.TableHolder
import db.tables.UserTables
import org.koin.dsl.bind
import org.koin.dsl.module

val dbModule = module {
    single { UserTables }.bind<TableHolder>()
}