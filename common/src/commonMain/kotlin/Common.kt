import kotlinx.serialization.Serializable

@Serializable
data class User(val name: String)

@Serializable
data class ServerGreeting(val greeting: String)
