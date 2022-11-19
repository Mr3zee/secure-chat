import components.Welcome
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffect
import react.useState
import utils.sendAsyncApiRequest

val App = FC<Props> {
    var allUsers by useState<List<User>?>(null)

    useEffect {
        if (allUsers == null) {
            sendAsyncApiRequest<List<User>>("users") {
                allUsers = it
            }
        }
    }

    allUsers?.also {
        Welcome {
            this.allUsers = it
        }
    } ?: run {
        div {
            +"Loading"
        }
    }
}
