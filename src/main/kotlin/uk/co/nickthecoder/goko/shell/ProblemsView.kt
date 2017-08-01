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
package uk.co.nickthecoder.goko.shell

import javafx.scene.control.Button
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.model.ProblemSet
import uk.co.nickthecoder.goko.model.Problems

class ProblemsView(mainWindow: MainWindow) : GridView(mainWindow, 130.0) {

    override val title = "Problems"

    override val viewStyle = "problems"

    override fun addButtons() {
        Problems.problemSets().forEach {
            buttons.add(createButton(it))
        }
    }

    fun createButton(problemSet: ProblemSet): Button {
        val button = createButton(problemSet.label, style = null) { ProblemSetView(mainWindow, problemSet) }
        return button
    }

}
