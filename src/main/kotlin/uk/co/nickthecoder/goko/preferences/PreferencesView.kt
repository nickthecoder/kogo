package uk.co.nickthecoder.goko.preferences

import javafx.geometry.Side
import javafx.scene.Node
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.gui.TopLevelView
import uk.co.nickthecoder.goko.gui.View
import uk.co.nickthecoder.goko.gui.ViewTab
import uk.co.nickthecoder.goko.shell.PromptTaskView
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.MyTabPane

class PreferencesView(mainWindow: MainWindow, val initialPage: Task? = null) : TopLevelView(mainWindow) {

    private val tabs = MyTabPane<ViewTab>()

    override val node: Node = tabs

    override val title = "Preferences"

    init {
        tabs.side = Side.BOTTOM
    }

    override fun build() {

        for (task in Preferences.preferenceTasksMap.values) {
            val view = object : PromptTaskView(task, mainWindow) {
                override fun onOk() {
                    super.onOk()
                    mainWindow.remove(this)
                }
            }
            view.build()
            val tab = ViewTab(view)
            tabs.add(tab)
            if (task === initialPage) {
                tabs.selectedTab = tab
            }
        }
    }

    override fun hasView(view: View): Boolean {
        return if (tabs.tabs.any { it.hasView(view) }) true else super.hasView(view)
    }
}