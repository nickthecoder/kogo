package uk.co.nickthecoder.goko.gui

import javafx.scene.input.KeyCode

object GoKoActions {

    private val nameToActionMap = mutableMapOf<String, GoKoAction>()

    // MainWindow
    val CLOSE_TAB = GoKoAction("tab.close", KeyCode.W, control = true, tooltip = "Close Tab")

    // Various views
    val PREFERENCES = GoKoAction("preferences", KeyCode.P, control = true, tooltip = "Preferences")
    val PASS = GoKoAction("pass", KeyCode.P, alt = true, label = "Pass")
    val EDIT = GoKoAction("edit", KeyCode.E, control = true, label = "Edit", tooltip = "Edit Game")
    val SAVE = GoKoAction("save", KeyCode.S, control = true, label = "Save", tooltip = "Save Game")
    val RESIGN = GoKoAction("resign", null, label = "Resign", tooltip = "Resign Game")
    val HINT = GoKoAction("hint", null, label = "Hint")
    val ESTIMATE_SCORE = GoKoAction("estimateScore", null, label = "Score")
    val UNDO = GoKoAction("undo", KeyCode.Z, control = true, tooltip = "Undo")

    // EditGameView
    val GO_FIRST = GoKoAction("go-first", KeyCode.HOME, alt = null, tooltip = "Rewind to the beginning")
    val GO_REWIND = GoKoAction("go-rewind", KeyCode.PAGE_UP, alt = null, tooltip = "Go back 10 moves")
    val GO_BACK = GoKoAction("go-back", KeyCode.LEFT, alt = null, tooltip = "Go back 1 move")
    val GO_FORWARD = GoKoAction("go-forward", KeyCode.RIGHT, alt = null, tooltip = "Go forward 1 move")
    val GO_FAST_FORWARD = GoKoAction("go-fastForward", KeyCode.PAGE_DOWN, alt = null, tooltip = "Fast Forward 10 moves")
    val GO_END = GoKoAction("go-last", KeyCode.END, alt = null, tooltip = "Fast forward to the end")
    val GO_MAIN_LINE = GoKoAction("go-mainLine", KeyCode.M, alt = null, label = "Main Line", tooltip = "Return to the main line of play")

    val MODE_MOVE = GoKoAction("mode-move", KeyCode.DIGIT1, control = null, tooltip = "Mode : Make moves")
    val MODE_BLACK = GoKoAction("mode-black", KeyCode.DIGIT2, control = null, tooltip = "Mode : Place black set-up stones")
    val MODE_WHITE = GoKoAction("mode-white", KeyCode.DIGIT3, control = null, tooltip = "Mode : Place white set-up stones")
    val MODE_REMOVE_STONE = GoKoAction("mode-remove-stone", KeyCode.DIGIT4, control = null, tooltip = "Mode : Remove stones")
    val MODE_SQUARE = GoKoAction("mode-square", KeyCode.DIGIT5, label = "□", control = null, tooltip = "Mode : Add square marks")
    val MODE_CIRCLE = GoKoAction("mode-square", KeyCode.DIGIT6, label = "○", control = null, tooltip = "Mode : Add circle marks")
    val MODE_TRIANGLE = GoKoAction("mode-triangle", KeyCode.DIGIT7, label = "△", control = null, tooltip = "Mode : Add triangle marks")
    val MODE_NUMBERS = GoKoAction("mode-numbers", KeyCode.DIGIT8, label = "1", control = null, tooltip = "Mode : Add number marks")
    val MODE_LETTERS = GoKoAction("mode-letters", KeyCode.DIGIT9, label = "A", control = null, tooltip = "Mode : Add letter marks")
    val MODE_REMOVE_MARK = GoKoAction("mode-remove-mark", KeyCode.DIGIT0, control = null, tooltip = "Mode : Remove marks")

    val EDIT_GAME_INFO = GoKoAction("game-info", keyCode = null, label = "Game Info")
    val DELETE_BRANCH = GoKoAction("delete-branch", keyCode = null, label = "Delete Branch")

    // ProblemView

    val PROBLEM_RESTART = GoKoAction("problem-reload", KeyCode.F5, control = true, tooltip = "Restart Problem")
    val PROBLEM_GIVE_UP = GoKoAction("problem-give-up", null, label = "Give Up", tooltip = "Give Up and show the solution")
    val PROBLEM_NEXT = GoKoAction("problem-next", KeyCode.RIGHT, alt = true, tooltip = "Next problem")


    fun add(action: GoKoAction) {
        GoKoActions.nameToActionMap.put(action.name, action)
    }
}