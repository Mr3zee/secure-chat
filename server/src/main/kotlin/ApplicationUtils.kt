import io.ktor.server.application.*
import org.slf4j.event.Level


@Suppress("unused")
fun Application.isDevEnv(): Boolean {
    val env = propertyOrNull("ktor.environment")?.getString()
    return env == "development"
}

fun Application.propertyOrNull(path: String) = environment.config.propertyOrNull(path)

fun Application.property(path: String) = propertyOrNull(path) ?: error("Expected Application property $path")

val Application.loggerLevel: Level
    get() = propertyOrNull("ktor.logger.level")?.let {
        Level.valueOf(it.getString())
    } ?: Level.INFO

fun Level.koinLevel() = org.koin.core.logger.Level.valueOf(name)
