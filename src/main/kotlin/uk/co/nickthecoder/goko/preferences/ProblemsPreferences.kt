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
package uk.co.nickthecoder.goko.preferences

import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.ButtonParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter


class ProblemsPreferences : AbstractTask(), CommentsPreferences {

    override val taskD = TaskDescription("problems")

    val directoryP = FileParameter("directory", expectFile = false, value = null, required = false)
    val showBranchesP = BooleanParameter("showBranches", value = true)
    val automaticOpponentP = BooleanParameter("automaticOpponent", value = true, description = "The computer can play the 2nd player's moves")

    override val showNodeAnnotationsP = BooleanParameter(name = "showNodeAnnotations", value = true)
    override val showMoveAnnotationsP = BooleanParameter(name = "showMoveAnnotations", value = true)

    val splitP = BooleanParameter("splitIntoMultipleFiles", value = false,
            description = """Split sgf files containing many of problems into lots of files containing a single go problem.
This makes is quicker to load, and you can edit the problems much easier. It does take up more disk space though.
""")

    val downloadP = ButtonParameter("downloadAdditionalProblems", action = { onDownload() }, buttonText = "Download")

    init {
        taskD.addParameters(directoryP, showBranchesP, automaticOpponentP, showNodeAnnotationsP, showMoveAnnotationsP, splitP, downloadP)
    }

    override fun run() {
        Preferences.save()
    }

    fun onDownload() {
        val directory = directoryP.value
        if (directory == null) {
            val alert = Alert(AlertType.INFORMATION)
            alert.title = "Information"
            alert.headerText = null
            alert.contentText = "Please enter the directory first."
            alert.showAndWait()

        } else {
            TaskPrompter(DownloadProblems(directory)).placeOnStage(Stage())
        }
    }
}
