package modules

import org.koin.dsl.module
import services.UserService

val servicesModule = module {
    single { UserService() }
}