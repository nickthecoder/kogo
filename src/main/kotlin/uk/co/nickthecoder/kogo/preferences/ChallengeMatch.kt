package uk.co.nickthecoder.kogo.preferences

import javafx.application.Platform
import uk.co.nickthecoder.kogo.GnuGoPlayer
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.Player
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.GameListener
import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter

open class ChallengeMatch : AbstractGamePreferences(), GameListener {

    final override val taskD = TaskDescription("challengeMatch", description =
    """Play aginst the Gnu Go robot.
Each time you play, the the handicap will change based on your previous results.
""")

    val computerLevelP = IntParameter("computerLevel", range = 1..20, value = 10)

    val computerPlaysP = ChoiceParameter("computerPlays", value = StoneColor.BLACK)
            .choice("BLACK", StoneColor.BLACK, "Black")
            .choice("WHITE", StoneColor.WHITE, " White")

    val promotionThresholdP = IntParameter(name = "promotionThreshold", value = 3,
            description = "The number of consecutive wins to rank up")

    val demotionThresholdP = IntParameter(name = "demotionThreshold", value = 3,
            description = "The number of consecutive loses for a demotion")

    val winsP = IntParameter("wins", value = 0)

    val losesP = IntParameter("loses", value = 0)

    init {
        taskD.addParameters(boardSizeP, computerLevelP, computerPlaysP, handicapP, fixedHandicapPointsP, komiP, timeLimitP,
                promotionThresholdP, demotionThresholdP, winsP, losesP
        )
    }

    override fun run() {
        Preferences.save()
    }

    override fun changePlayers(game: Game) {
        val human = LocalPlayer(game, StoneColor.opposite(computerPlaysP.value!!), Preferences.yourName, Preferences.yourRank)
        human.timeRemaining = game.metaData.timeLimit.copy()

        val gnuGo = GnuGoPlayer(game, computerPlaysP.value!!)
        gnuGo.start()

        game.addPlayer(gnuGo)
        game.addPlayer(human)

        game.file = Preferences.gameFile("Challenge")
        // Listens for the end of the game to update number of wins/loses.
        val challengeMatch = (this as ChallengeMatchLauncher).parent
        game.listeners.add(challengeMatch)
    }

    override fun gameEnded(winner: Player?) {
        if (winner != null) {
            Platform.runLater {
                updateStats(winner is LocalPlayer)
            }
        }
    }

    fun updateStats(youWon: Boolean) {
        if (youWon) {
            winsP.value = winsP.value!! + 1
            losesP.value = 0
        } else if (!youWon) {
            losesP.value = losesP.value!! + 1
            winsP.value = 0
        }

        if (winsP.value!! >= promotionThresholdP.value!!) {
            promote()
        }
        if (losesP.value!! >= demotionThresholdP.value!!) {
            demote()
        }

        Preferences.save()
    }

    fun promote() {
        winsP.value = 0

        if (computerPlaysP.value == StoneColor.BLACK) {
            handicapP.value = handicapP.value!! + 1
        } else {
            if (handicapP.value == 0) {
                computerPlaysP.value = StoneColor.BLACK
            } else {
                handicapP.value = handicapP.value!! - 1
            }
        }
    }

    fun demote() {
        losesP.value = 0

        if (computerPlaysP.value == StoneColor.WHITE) {
            handicapP.value = handicapP.value!! + 1
        } else {
            if (handicapP.value == 0) {
                computerPlaysP.value = StoneColor.WHITE
            } else {
                handicapP.value = handicapP.value!! - 1
            }
        }
    }

    override fun createLauchTask(mainWindow: MainWindow): Task {
        return ChallengeMatchLauncher(mainWindow, this)
    }
}


class ChallengeMatchLauncher(val mainWindow: MainWindow, val parent: ChallengeMatch) : ChallengeMatch() {

    init {
        taskD.copyValuesFrom(parent.taskD)
    }

    override fun run() {
        mainWindow.changeView(createView(mainWindow))
    }

}