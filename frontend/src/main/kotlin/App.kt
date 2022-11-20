import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffect
import react.useState
import utils.sendAsyncApiRequest

enum class State {
    PENDING, CONNECTED, ERROR
}

val App = FC<Props> {
    var state by useState(State.PENDING)

    useEffect {
        if (state == State.PENDING) {
            sendAsyncApiRequest<String>(
                path = "healthcheck"
            ) {
                state = if (it == "OK") {
                    State.CONNECTED
                } else {
                    State.ERROR
                }
            }
        }
    }

    div {
        +"State: $state"
    }
}
