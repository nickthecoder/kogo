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

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.gui.TopLevelView
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm

open class PromptTaskView(val task: Task, mainWindow: MainWindow) : TopLevelView(mainWindow) {

    override val title = task.taskD.label

    val borderPane = BorderPane()

    override val node: Node = borderPane

    val taskForm = TaskForm(task)

    private val buttons = FlowPane()

    val okButton = Button("Ok")

    val cancelButton = Button("Cancel")

    override fun build() {
        with(borderPane) {
            styleClass.add("prompt")
            center = taskForm.scrollPane
            bottom = buttons
        }

        with(cancelButton) {
            cancelButton.onAction = EventHandler { onCancel() }
            cancelButton.isCancelButton = true
        }

        with(okButton) {
            okButton.onAction = EventHandler { onOk() }
            okButton.isDefaultButton = true
        }


        with(buttons) {
            children.addAll(okButton, cancelButton)
            styleClass.add("buttons")
        }
    }

    private fun onCancel() {
        mainWindow.remove(this)
    }

    protected open fun onOk() {
        taskForm.check()
        task.run()
    }
}
