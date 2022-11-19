package com.example.auth.server.modules

import org.koin.dsl.module
import com.example.auth.server.services.UserService

val servicesModule = module {
    single { UserService() }
}
