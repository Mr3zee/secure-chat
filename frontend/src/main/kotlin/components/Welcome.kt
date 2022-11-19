package components

import ServerGreeting
import User
import csstype.Display
import csstype.FlexDirection
import csstype.NamedColor
import csstype.px
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.InputType
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.p
import react.useState
import utils.sendAsyncApiPostRequest


external interface WelcomeProps : Props {
    var allUsers: List<User>
}

val Welcome = FC<WelcomeProps> { props ->
    var allUsers by useState(props.allUsers)
    var name by useState("")
    var serverGreeting by useState("")

    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
            gap = 4.px
        }

        img {
            src = "/images/KotlinLogo.png"
            alt = "Kotlin logo"
            width = 32.0
            height = 32.0
        }

        div {
            css {
                fontSize = 32.px
                lineHeight = 32.px
            }

            +"Kotlin Client-Server Template"
        }
    }

    div {
        css {
            padding = 5.px
            backgroundColor = NamedColor.orange

            marginTop = 16.px
        }

        p {
            +"All registered users:"
        }
        p {
            +allUsers.joinToString(", ") { it.name }
        }
    }

    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
            gap = 4.px
        }

        input {
            css {
                marginTop = 5.px
                marginBottom = 5.px
                fontSize = 14.px
            }
            type = InputType.text
            value = name
            placeholder = "username"
            onChange = { event ->
                name = event.target.value
            }
        }

        button {
            +"Add new user"

            onClick = {
                sendAsyncApiPostRequest(
                    path = "users",
                    body = User(name)
                ) { (users, greeting): Pair<List<User>, ServerGreeting> ->
                    serverGreeting = greeting.greeting
                    allUsers = users
                }

                name = ""
            }
        }
    }

    if (serverGreeting.isNotBlank()) {
        div {
            css {
                padding = 5.px
                backgroundColor = NamedColor.aqua
            }
            p {
                +"Greetings from server:"
            }
            p {
                +serverGreeting
            }
        }
    }
}
