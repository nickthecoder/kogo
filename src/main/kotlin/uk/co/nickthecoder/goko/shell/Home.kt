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

import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.preferences.AbstractGamePreferences
import uk.co.nickthecoder.goko.preferences.Preferences
import uk.co.nickthecoder.goko.preferences.PreferencesListener
import uk.co.nickthecoder.goko.preferences.PreferencesView
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.parameters.TaskParameter

class Home(mainWindow: MainWindow) : GridView(mainWindow, 210.0), PreferencesListener {

    override val title = "Home"

    override val viewStyle = "home"

    init {
        Preferences.gamesPreferences.ensureGamesExist()

        Preferences.listeners.add(this)
    }

    override fun tidyUp() {
        super.tidyUp()
        Preferences.listeners.remove(this)
    }

    override fun preferencesChanged() {
        createButtons()
        buildButtons()
    }

    override fun createButtons() {
        super.createButtons()

        Preferences.gamesPreferences.gamesP.value.forEach { compound ->
            val taskParameter = compound.find("type") as TaskParameter
            val labelP = compound.find("label") as StringParameter
            val task = taskParameter.value
            if (task is AbstractGamePreferences) {
                buttons.add(createTaskButton(labelP.value, style = task.style) { task.createLaunchTask(mainWindow) })
            }
        }

        with(buttons) {

            add(createViewButton("Problems", "problems") {
                val problemsDir = Preferences.problemsDirectory
                if (problemsDir != null) {
                    ProblemsView(mainWindow)
                } else {
                    PreferencesView(mainWindow, Preferences.problemsPreferences)
                }
            })
            add(createActionButton("Joseki Dictionary", "joseki") {
                mainWindow.openJosekiDictionary()
            })
            add(createActionButton("Open SGF File", "open-file") { mainWindow.onOpenFile() })
            add(createViewButton("Preferences", "preferences") { PreferencesView(mainWindow) })
        }
    }

}
