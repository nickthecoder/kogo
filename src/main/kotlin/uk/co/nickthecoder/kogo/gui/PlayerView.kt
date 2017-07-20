package uk.co.nickthecoder.kogo.gui

import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.model.Board
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.GameListener

class PlayerView(val boardView: BoardView) : GameListener {

    private val stack = StackPane()

    val node: Node
        get() = stack

    val board: Board
        get() = boardView.board

    val game: Game
        get() = boardView.board.game

    init {
        with(stack) {
            styleClass.add("playing-area")
            prefWidth = boardView.board.sizeX * BoardView.pointSize
            prefHeight = boardView.board.sizeY * BoardView.pointSize
        }

        stack.addEventHandler(MouseEvent.MOUSE_MOVED) { onMouseMoved(it) }
        stack.addEventHandler(MouseEvent.MOUSE_CLICKED) { onMouseClicked(it) }
        stack.addEventHandler(MouseEvent.MOUSE_EXITED) { onMouseExited() }

        game.gameListeners.add(this)
    }

    fun onMouseMoved(event: MouseEvent) {
        val point = boardView.toBoardPoint(event.x, event.y)
        if (board.contains(point)) {
            boardView.mouseMark.point = point
        } else {
            boardView.mouseMark.point = boardView.OFF_SCREEN
        }
    }

    fun onMouseExited() {
        boardView.mouseMark.point = boardView.OFF_SCREEN
    }

    fun onMouseClicked(event: MouseEvent) {
        val point = boardView.toBoardPoint(event.x, event.y)
        val player = game.playerToMove

        if (player is LocalPlayer && game.canPlayAt(point)) {
            player.makeMove(point)
        }
    }

}