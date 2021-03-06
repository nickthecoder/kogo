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
package uk.co.nickthecoder.goko.model

import uk.co.nickthecoder.goko.Player

class HiddenMoveGo(val game: Game, val hiddenMoveCountBlack: Int, val hiddenMoveCountWhite: Int) : GameVariation, GameListener {

    enum class State { HIDDEN_BLACK, HIDDEN_WHITE, NORMAL }

    override val allowHelp = false

    var state = State.HIDDEN_BLACK

    val board
        get() = game.board

    /**
     * The list of currently hidden moves. This list shrinks as hidden moves are revealed during the game.
     */
    val hiddenBlackMoves = mutableSetOf<Point>()
    val hiddenWhiteMoves = mutableSetOf<Point>()

    /**
     * The list of hidden moves. This list does NOT change over the course of the game as moves are revealed.
     */
    val allHiddenBlackMoves = mutableSetOf<Point>()
    val allHiddenWhiteMoves = mutableSetOf<Point>()

    override fun start() {
        game.playerToMove = game.players[StoneColor.BLACK]!!
        game.playerToMove.yourTurn()
        game.listeners.add(this)
        placeHiddenMovesMessage(StoneColor.BLACK)
    }

    override fun canPlayAt(point: Point?): Boolean {

        when (state) {
            State.HIDDEN_BLACK -> {
                if (point == null) {
                    return hiddenBlackMoves.size == hiddenMoveCountBlack
                }
                return true
            }
            State.HIDDEN_WHITE -> {
                if (point == null) {
                    return hiddenWhiteMoves.size == hiddenMoveCountWhite
                }
                return true
            }
            State.NORMAL -> {
                if (point == null) {
                    return true // Can always pass
                } else {
                    val color = board.getStoneAt(point)
                    if (color == StoneColor.HIDDEN_BLACK || color == StoneColor.HIDDEN_WHITE) {
                        reveal(color.realColor(), point)
                        game.message("That point is a hidden move. Try again.")
                        return false
                    }

                    val result = game.canPlayAt(point)

                    if (color == StoneColor.HIDDEN_BOTH) {
                        reveal(StoneColor.NONE, point)
                    }

                    game.message("")
                    return result
                }
            }
        }
    }

    /**
     * Make a move at point, or null for a pass.
     */
    override fun makeMove(point: Point?, color: StoneColor, onMainLine: Boolean) {

        if (state == State.NORMAL) {

            if (point == null) {
                game.pass(color, onMainLine)
            } else {
                game.move(point, color, onMainLine)
            }

        } else {

            if (point == null) {
                endOfOneColorSetup()
                return
            } else {
                val list = if (color == StoneColor.BLACK) hiddenBlackMoves else hiddenWhiteMoves

                if (game.getMarkAt(point) == null) {
                    list.add(point)
                    game.addMark(TerritoryMark(point, color))
                } else {
                    list.remove(point)
                    game.removeMark(point)
                }
                setupMessage(color)
            }

        }

    }

    private fun setupMessage(color: StoneColor) {
        val colorString = color.toString().toLowerCase().capitalize()
        val diff = if (color == StoneColor.BLACK) {
            hiddenMoveCountBlack - hiddenBlackMoves.size
        } else {
            hiddenMoveCountWhite - hiddenWhiteMoves.size
        }
        if (diff > 0) {
            game.message("$colorString to make $diff more hidden moves, and then pass.")
        } else if (diff < 0) {
            game.message("$colorString to remove ${-diff} hidden moves, and then pass.")
        } else {
            game.message("$colorString may now pass or rearrange the hidden moves.")
        }
    }

    private fun placeHiddenMovesMessage(color: StoneColor) {
        game.message("${color.humanString()} to make $hiddenMoveCountBlack hidden moves, and then pass.")
    }

    private fun endOfOneColorSetup() {

        if (state == State.HIDDEN_BLACK) {
            if (hiddenBlackMoves.size != hiddenMoveCountBlack) {
                setupMessage(StoneColor.BLACK)
                return
            }
            state = State.HIDDEN_WHITE
            game.playerToMove = game.players[StoneColor.WHITE]!!
            placeHiddenMovesMessage(StoneColor.WHITE)
            // This is a little bit of a bodge. It allows views to change mouse color.

        } else {
            if (hiddenWhiteMoves.size != hiddenMoveCountWhite) {
                setupMessage(StoneColor.WHITE)
                return
            }
            state = State.NORMAL
            game.playerToMove = game.players[StoneColor.BLACK]!!
        }

        game.clearMarks()

        if (state == State.NORMAL) {
            endSetup()
        }

        game.nodeDataChanged()
        game.playerToMove.yourTurn()

    }

    private fun endSetup() {
        val node = game.root

        allHiddenBlackMoves.addAll(hiddenBlackMoves)
        allHiddenWhiteMoves.addAll(hiddenWhiteMoves)

        // Place all the stones on the board. Some stones may have no liberties at after this step.
        hiddenBlackMoves.forEach { point ->
            if (hiddenWhiteMoves.contains(point)) {
                node.addStone(board, point, StoneColor.HIDDEN_BOTH)
            } else {
                node.addStone(board, point, StoneColor.HIDDEN_BLACK)
            }
        }
        hiddenWhiteMoves.forEach { point ->
            if (!hiddenBlackMoves.contains(point)) {
                node.addStone(board, point, StoneColor.HIDDEN_WHITE)
            }
        }

        // Now check all placed stones, and build a list of those that have no liberties
        val deadBlack = hiddenBlackMoves.filter {
            board.getStoneAt(it) != StoneColor.HIDDEN_BOTH
        }.filter {
            board.checkLiberties(it) != null
        }

        val deadWhite = hiddenWhiteMoves.filter {
            board.getStoneAt(it) != StoneColor.HIDDEN_BOTH
        }.filter {
            board.checkLiberties(it) != null
        }

        // Remove all the hidden moves that have no liberties
        deadBlack.forEach {
            board.setStoneAt(it, StoneColor.NONE)
            hiddenBlackMoves.remove(it)
        }
        deadWhite.forEach {
            board.setStoneAt(it, StoneColor.NONE)
            hiddenWhiteMoves.remove(it)
        }

        game.root.name = "Hidden Moves"
        game.message("Let the game begin!")
    }


    override fun displayColor(point: Point): StoneColor = board.getStoneAt(point)

    override fun capturedStones(colorCaptured: StoneColor, points: Set<Point>) {
        points.forEach { point ->


            val checkAttackers: Set<Point>
            val checkCaptured: Set<Point>

            if (colorCaptured == StoneColor.WHITE) {
                checkAttackers = hiddenBlackMoves
                checkCaptured = hiddenWhiteMoves
            } else {
                checkAttackers = hiddenWhiteMoves
                checkCaptured = hiddenBlackMoves
            }

            // If any of the stones doing the capturing were hidden, then they must be revealed.
            checkAttackers.filter { it.isTouching(point) }.forEach {
                reveal(colorCaptured.opposite(), it)
            }

            // If a hidden stone was captured as part of a larger group, then it should also be revealed.
            // This isn't perfect, because if TWO groups are captured, and one of the groups was a single
            // hidden stone, then this didn't need to be revealed
            if (points.size > 1) {
                checkCaptured.filter { points.contains(it) }.forEach {
                    reveal(colorCaptured, it)
                }
            }
            // Also, if a single hidden stone was captured in a ko situation, then it needs to be releaved
            // when the ko is attempted.
        }
    }

    /**
     * Reveal a point. Real color can by WHITE, BLACK or NONE.
     * Note that realColor is needed, because we cannot use the color of the stone on the board,
     * as it may have been taken (an may even be replaced by a different color).
     * This is used to reveal stones during the course of the game AND when after the game has ended.
     */
    fun reveal(realColor: StoneColor, point: Point) {
        board.setStoneAt(point, board.getStoneAt(point).realColor())

        if (realColor == StoneColor.BLACK) {
            game.addMark(CircleMark(point))
        } else if (realColor == StoneColor.WHITE) {
            game.addMark(TriangleMark(point))
        } else {
            game.addMark(SquareMark(point))
        }

        hiddenWhiteMoves.remove(point)
        hiddenBlackMoves.remove(point)

        game.root.removeStone(game.board, point)
        game.root.addStone(game.board, point, realColor)
    }

    override fun gameEnded(winner: Player?) {
        allHiddenBlackMoves.toList().forEach { reveal(StoneColor.BLACK, it) }
        allHiddenWhiteMoves.toList().forEach { reveal(StoneColor.WHITE, it) }
        allHiddenBlackMoves.filter { allHiddenWhiteMoves.contains(it) }.forEach { reveal(StoneColor.NONE, it) }
    }

}
