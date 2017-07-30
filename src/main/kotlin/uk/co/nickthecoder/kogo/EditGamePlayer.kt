package uk.co.nickthecoder.kogo

import uk.co.nickthecoder.kogo.model.*

class EditGamePlayer(val game: Game, override val color: StoneColor, override val rank: String = "")
    : Player {

    override var timeRemaining: TimeLimit = NoTimeLimit()

    override var label = color.toString().toLowerCase().capitalize()

    override fun makeMove(point: Point) {
        game.move(point, color, onMainLine = false)
    }

    override fun pass() {
        game.pass()
    }

    override fun canClickToPlay() = true
}
