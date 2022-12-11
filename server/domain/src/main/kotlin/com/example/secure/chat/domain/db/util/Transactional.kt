package com.example.secure.chat.domain.db.util

import org.jetbrains.exposed.sql.Transaction

open class Transactional(internal open val transaction: Transaction)
