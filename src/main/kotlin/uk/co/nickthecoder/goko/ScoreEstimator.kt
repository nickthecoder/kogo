package uk.co.nickthecoder.goko

import javafx.application.Platform
import uk.co.nickthecoder.goko.model.Game

class ScoreEstimator(val game: Game) : GnuGoClient {

    private lateinit var callback: (String) -> Unit

    fun score(callback: (String) -> Unit) {
        // Run later to ensure that GnuGo has been updated first
        Platform.runLater {
            this.callback = callback
            game.createGnuGo().estimateScore(this)
        }
    }

    override fun estimateScoreResults(score: String) {
        Platform.runLater {
            callback(score.split(" ").firstOrNull() ?: "Unknown")
        }
    }

}
