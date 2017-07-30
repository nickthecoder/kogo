package uk.co.nickthecoder.kogo.gui

import javafx.scene.input.KeyCode

object KoGoActions {

    private val nameToActionMap = mutableMapOf<String, KoGoAction>()

    // MainWindow
    val CLOSE_TAB = KoGoAction("tab.close", KeyCode.W, control = true, tooltip = "Close Tab")

    // Various views
    val PREFERENCES = KoGoAction("preferences", KeyCode.P, control = true, tooltip = "Preferences")
    val PASS = KoGoAction("pass", KeyCode.P, alt = true, label = "Pass")
    val EDIT = KoGoAction("edit", KeyCode.E, control = true, label = "Edit", tooltip = "Edit Game")
    val SAVE = KoGoAction("save", KeyCode.S, control = true, label = "Save", tooltip = "Save Game")
    val RESIGN = KoGoAction("resign", null, label = "Resign", tooltip = "Resign Game")
    val HINT = KoGoAction("hint", null, label = "Hint")
    val ESTIMATE_SCORE = KoGoAction("estimateScore", null, label = "Score")
    val UNDO = KoGoAction("undo", KeyCode.Z, control = true, tooltip = "Undo")

    // EditGameView
    val GO_FIRST = KoGoAction("go-first", KeyCode.HOME, alt = true, tooltip = "Rewind to the beginning")
    val GO_REWIND = KoGoAction("go-rewind", KeyCode.PAGE_UP, alt = true, tooltip = "Go back 10 moves")
    val GO_BACK = KoGoAction("go-back", KeyCode.LEFT, alt = true, tooltip = "Go back 1 move")
    val GO_FORWARD = KoGoAction("go-forward", KeyCode.RIGHT, alt = true, tooltip = "Go forward 1 move")
    val GO_FAST_FORWARD = KoGoAction("go-fastForward", KeyCode.PAGE_DOWN, alt = true, tooltip = "Fast Forward 10 moves")
    val GO_END = KoGoAction("go-last", KeyCode.END, alt = true, tooltip = "Fast forward to the end")
    val GO_MAIN_LINE = KoGoAction("go-mainLine", KeyCode.M, alt = true, label = "Main Line", tooltip = "Return to the main line of play")

    val MODE_MOVE = KoGoAction("mode-move", KeyCode.DIGIT1, control = true, tooltip = "Mode : Make moves")
    val MODE_BLACK = KoGoAction("mode-black", KeyCode.DIGIT2, control = true, tooltip = "Mode : Place black set-up stones")
    val MODE_WHITE = KoGoAction("mode-white", KeyCode.DIGIT3, control = true, tooltip = "Mode : Place white set-up stones")
    val MODE_REMOVE_STONE = KoGoAction("mode-remove-stone", KeyCode.DIGIT4, control = true, tooltip = "Mode : Remove stones")
    val MODE_SQUARE = KoGoAction("mode-square", KeyCode.DIGIT5, label = "□", control = true, tooltip = "Mode : Add square marks")
    val MODE_CIRCLE = KoGoAction("mode-square", KeyCode.DIGIT6, label = "○", control = true, tooltip = "Mode : Add circle marks")
    val MODE_TRIANGLE = KoGoAction("mode-triangle", KeyCode.DIGIT7, label = "△", control = true, tooltip = "Mode : Add triangle marks")
    val MODE_NUMBERS = KoGoAction("mode-numbers", KeyCode.DIGIT8, label = "1", control = true, tooltip = "Mode : Add number marks")
    val MODE_LETTERS = KoGoAction("mode-letters", KeyCode.DIGIT9, label = "A", control = true, tooltip = "Mode : Add letter marks")
    val MODE_REMOVE_MARK = KoGoAction("mode-remove-mark", KeyCode.DIGIT0, control = true, tooltip = "Mode : Remove marks")

    val EDIT_GAME_INFO = KoGoAction("game-info", keyCode = null, label = "Game Info")
    val DELETE_BRANCH = KoGoAction("delete-branch", keyCode = null, label = "Delete Branch")

    // ProblemView

    val PROBLEM_RESTART = KoGoAction("problem-reload", KeyCode.F5, control = true, tooltip = "Restart Problem")
    val PROBLEM_GIVE_UP = KoGoAction("problem-give-up", null, label = "Give Up", tooltip = "Give Up and show the solution")
    val PROBLEM_NEXT = KoGoAction("problem-next", KeyCode.RIGHT, alt = true, tooltip = "Next problem")


    fun add(action: KoGoAction) {
        KoGoActions.nameToActionMap.put(action.name, action)
    }
}
