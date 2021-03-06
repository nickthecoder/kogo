/*
GoKo a Go Client
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.goko.gui

import javafx.event.ActionEvent
import javafx.geometry.Orientation
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.goko.GnuGoPlayer
import uk.co.nickthecoder.goko.GoKo
import uk.co.nickthecoder.goko.LocalPlayer
import uk.co.nickthecoder.goko.model.*
import uk.co.nickthecoder.goko.preferences.Preferences
import uk.co.nickthecoder.goko.preferences.PreferencesListener
import uk.co.nickthecoder.goko.preferences.PreferencesView
import uk.co.nickthecoder.paratask.gui.CompoundButtons
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.gui.TaskPrompter

class EditGameView(mainWindow: MainWindow, game: Game) : AbstractGoView(mainWindow, game), PreferencesListener {

    override val title = "Edit"

    private val split = SplitPane()

    private val rightBorder = BorderPane()

    private val centerBorder = BorderPane()

    private val gameInfoView = GameInfoView(game, false)

    private val commentView = CommentsView(game, false, Preferences.editGamePreferences)

    private val modesToolBar = ToolBar()

    private val branchesButton = MenuButton()

    private val shortcuts = ShortcutHelper("EditGameView", node)

    override fun build() {
        super.build()
        gameInfoView.build()
        boardView.build()
        commentView.build()
        whole.top = toolBar
        whole.center = split

        with(split) {
            items.addAll(centerBorder, rightBorder)
            dividers[0].position = 0.7
        }

        rightBorder.center = commentView.node
        rightBorder.top = gameInfoView.node

        centerBorder.center = boardView.node
        centerBorder.left = modesToolBar

        val preferencesB = GoKoActions.PREFERENCES.createButton(shortcuts) { onPreferences() }

        val navigation = CompoundButtons()
        navigation.children.addAll(restartB, rewindB, backB, forwardB, fastForwardB, endB)

        boardView.clickBoardView.onClickedPoint = { point -> clickToMove(point) }
        val moveModeB = GoKoActions.MODE_MOVE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> clickToMove(point) }
            node.requestFocus()
            boardView.playing()
        }
        moveModeB.isSelected = true

        val blackModeB = GoKoActions.MODE_BLACK.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> addSetupStone(point, StoneColor.BLACK) }
            node.requestFocus()
            boardView.placingStone(StoneColor.BLACK)
        }

        val whiteModeB = GoKoActions.MODE_WHITE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> addSetupStone(point, StoneColor.WHITE) }
            node.requestFocus()
            boardView.placingStone(StoneColor.WHITE)
        }

        val removeStoneModeB = GoKoActions.MODE_REMOVE_STONE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> removeStone(point) }
            node.requestFocus()
            boardView.removingStone()
        }


        val squareModeB = GoKoActions.MODE_SQUARE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> game.addMark(SquareMark(point)) }
            node.requestFocus()
            boardView.placingMark()
        }

        val circleModeB = GoKoActions.MODE_CIRCLE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> game.addMark(CircleMark(point)) }
            node.requestFocus()
            boardView.placingMark()
        }

        val triangleModeB = GoKoActions.MODE_TRIANGLE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> game.addMark(TriangleMark(point)) }
            node.requestFocus()
            boardView.placingMark()
        }

        val numberModeB = GoKoActions.MODE_NUMBERS.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> addNumber(point) }
            node.requestFocus()
            boardView.placingMark()
        }

        val letterModeB = GoKoActions.MODE_LETTERS.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> addLetter(point) }
            node.requestFocus()
            boardView.placingMark()
        }

        val removeMarkModeB = GoKoActions.MODE_REMOVE_MARK.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> game.removeMark(point) }
            node.requestFocus()
            boardView.removingMark()
        }

        val dimModeB = GoKoActions.MODE_DIM.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> toggleDimStone(point) }
            node.requestFocus()
            boardView.placingMark()
        }

        val editGameInfoB = GoKoActions.EDIT_GAME_INFO.createButton(shortcuts) { onEditGameInfo() }
        val deleteBranchB = GoKoActions.DELETE_BRANCH.createButton(shortcuts) { onDeleteBranch() }
        val gnuGoB = GoKoActions.GNU_GO_TO_PLAY.createButton(shortcuts) { onGnuGoToPlay() }

        val rewindToMainLineB = GoKoActions.REWIND_TO_MAIN_LINE.createButton(shortcuts) { history.rewindToMainLine() }
        val rewindToBranchPointB = GoKoActions.REWIND_TO_BRANCH_POINT.createButton(shortcuts) { game.rewindToBranchPoint() }
        val forwardToBranchPointB = GoKoActions.FORWARD_TO_BRANCH_POINT.createButton(shortcuts) { history.forwardToBranchPoint() }

        boardView.showBranches = Preferences.editGamePreferences.showBranchesP.value!!

        toolBar.items.addAll(saveB, preferencesB, editGameInfoB, estimateScoreB, hotspotsB, hintB, passB, navigation)
        toolBar.items.addAll(rewindToMainLineB, rewindToBranchPointB, forwardToBranchPointB, branchesButton, deleteBranchB, gnuGoB)

        val modesToggleGroup = ToggleGroup()
        with(modesToolBar) {
            items.addAll(moveModeB, blackModeB, whiteModeB, removeStoneModeB, squareModeB, circleModeB, triangleModeB, numberModeB, letterModeB, removeMarkModeB, dimModeB)
            styleClass.add("modes")
            orientation = Orientation.VERTICAL
            items.forEach {
                it as ToggleButton
                it.minWidth = 40.0
                it.minHeight = 40.0
                modesToggleGroup.toggles.add(it)
            }
        }

        preferencesChanged()
        buildBranchesMenu()
        Preferences.listeners.add(this)

        // Listen for number key presses, and go down that branch.
        whole.addEventHandler(KeyEvent.KEY_PRESSED) { event ->
            val number = event.code.ordinal - KeyCode.DIGIT1.ordinal + 1
            if (number in 1..9) {
                if (game.currentNode.children.size >= number) {
                    game.apply(game.currentNode.children[number - 1])
                }
            }
        }
    }

    override fun tidyUp() {
        super.tidyUp()
        game.tidyUp()
        gameInfoView.tidyUp()
        boardView.tidyUp()
        commentView.tidyUp()
        Preferences.listeners.remove(this)
    }

    fun clickToMove(point: Point) {
        val player = game.playerToMove

        if (player.canClickToPlay() && game.canPlayAt(point)) {
            player.makeMove(point, onMainLine = false)
            GoKo.stoneSound()
        }
    }

    fun addSetupStone(point: Point, color: StoneColor) {
        var node = game.currentNode
        if (node !is SetupNode || node.children.isNotEmpty()) {
            node = SetupNode(game.playerToMove.color)
            game.addNode(node, false)
            game.apply(node)
        }
        node.addStone(board, point, color)
        GoKo.stoneSound()
    }

    fun removeStone(point: Point) {
        if (board.getStoneAt(point).isStone()) {
            var node = game.currentNode
            if (node !is SetupNode || node.children.isNotEmpty()) {
                node = SetupNode(game.playerToMove.color)
                game.addNode(node, false)
                game.apply(node)
            }
            node.removeStone(board, point)
        }
    }

    fun addNumber(point: Point) {
        val node = game.currentNode
        for (i in 1..99) {
            val text = i.toString()
            if (!node.hasLabelMark(text)) {
                game.addMark(LabelMark(point, text))
                return
            }
        }
    }

    fun addLetter(point: Point) {
        val node = game.currentNode
        for (c in 'A'..'Z') {
            val text = c.toString()
            if (!node.hasLabelMark(text)) {
                game.addMark(LabelMark(point, text))
                return
            }
        }
    }

    fun toggleDimStone(point: Point) {
        val oldMark = game.removeMark(point)
        if (oldMark !is DimmedMark) {
            game.addMark(DimmedMark(point))
        }
    }

    fun buildBranchesMenu() {
        with(branchesButton) {
            items.clear()
            graphic = ImageView(GoKo.imageResource("buttons/branches.png"))

            game.currentNode.children.forEach { child ->
                val text = when (child) {
                    is PassNode -> "Pass"
                    is MoveNode -> child.point.toString()
                    is SetupNode -> "Setup Node"
                    else -> null
                }
                if (text != null) {
                    val menuItem = MenuItem(text)
                    menuItem.addEventHandler(ActionEvent.ACTION) {
                        game.apply(child)
                    }
                    branchesButton.items.add(menuItem)
                }
            }

            if (branchesButton.items.isEmpty()) {
                val menuItem = MenuItem("None")
                menuItem.isDisable = true
                branchesButton.items.add(menuItem)
            }

        }

    }

    override fun madeMove(gameNode: GameNode) {
        super.madeMove(gameNode)
        buildBranchesMenu()
    }

    override fun undoneMove(gameNode: GameNode) {
        super.undoneMove(gameNode)
        buildBranchesMenu()
    }

    override fun preferencesChanged() {
        boardView.showMoveNumbers = Preferences.editGameShowMoveNumber!!
        boardView.showBranches = Preferences.editGamePreferences.showBranchesP.value!!
    }

    fun onPreferences() {
        val view = PreferencesView(mainWindow, Preferences.editGamePreferences)
        view.build()
        mainWindow.addView(view)
    }

    fun onEditGameInfo() {
        val prompter = TaskPrompter(game.metaData)
        prompter.placeOnStage(Stage())
    }

    fun onDeleteBranch() {
        val prompter = TaskPrompter(DeleteBranchTask(game))
        prompter.placeOnStage(Stage())
    }

    fun onGnuGoToPlay() {
        val newGame = game.copy(sync = true)
        val view = PlayingView(mainWindow, newGame)
        val gnuGo = GnuGoPlayer(newGame, newGame.playerToMove.color)
        val human = LocalPlayer(newGame, newGame.playerToMove.color.opposite())
        newGame.addPlayer(gnuGo)
        newGame.addPlayer(human)
        view.build()
        mainWindow.addView(view)
        gnuGo.start()
        gnuGo.yourTurn()
    }

    override fun gameMessage(message: String) {
        gameInfoView.message(message)
    }
}
