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

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.goko.model.TimedLimit
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.TaskParameter
import uk.co.nickthecoder.paratask.util.JsonHelper
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object Preferences {

    val basicPreferences = BasicPreferences()
    val yourName by basicPreferences.yourNameP
    val yourRank by basicPreferences.yourRankP
    val gamesDirectory by basicPreferences.gamesDirectoryP

    val timeLimitPreferences = TimeLimitPreferences()

    val gamesPreferences = GamesPreferences()

    val problemsPreferences = ProblemsPreferences()
    val problemsDirectory by problemsPreferences.directoryP
    val problemsShowBranches by problemsPreferences.showBranchesP
    val problemsAutomaticOpponent by problemsPreferences.automaticOpponentP

    val josekiPreferences = JosekiPreferences()
    val josekiDirectionary by josekiPreferences.josekiDictionaryP

    val editGamePreferences = EditGamePreferences()
    val editGameShowMoveNumber by editGamePreferences.showMoveNumbersP

    val advancedPreferences = AdvancedPreferences()

    val preferencesFile = homeDirectory.child(".config", "goko", "preferences.json")

    val problemResultsDirectory = homeDirectory.child(".config", "goko", "problems")


    val preferenceTasksMap = mutableMapOf<String, Task>()


    val listeners = mutableListOf<PreferencesListener>()

    private fun addPreferenceTask(task: Task) {
        preferenceTasksMap.put(task.taskD.name, task)
    }

    init {
        // NOTE. Time limits must come before any preferences that use time limits (such as quickGamePreferences).
        addPreferenceTask(basicPreferences)
        addPreferenceTask(timeLimitPreferences)
        addPreferenceTask(gamesPreferences)
        addPreferenceTask(problemsPreferences)
        addPreferenceTask(josekiPreferences)
        addPreferenceTask(editGamePreferences)
        addPreferenceTask(advancedPreferences)

        if (preferencesFile.exists()) {
            load()
        }

    }

    /**
     * Creates a file path for a sgf file, based on the type of game and the current date and time.
     */
    fun gameFile(gameType: String): File {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val name = "$gameType ${format.format(Date().time)}.sgf"
        return File(gamesDirectory, name)
    }

    /**
     * Example json file :
     *
     * {
     *      preferences=[
     *          name="quickGamePreferences",
     *          parameters=[
     *              { name="foo", value="bar" },
     *              { name="baz", value=3 }
     *      ],
     *      [
     *          name="basicPreferences",
     *          parameters=[
     *              { name="foo", value="bar" },
     *              { name="baz", value=3 }
     *      ]
     * }
     */
    fun load() {

        val jroot = Json.parse(InputStreamReader(FileInputStream(preferencesFile))).asObject()
        val jpreferences = jroot.get("preferences").asArray()

        for (jtask1 in jpreferences) {
            val jtask = jtask1.asObject()
            val taskName = jtask.getString("name", "")

            val task = preferenceTasksMap[taskName]
            if (task != null) {
                val jparams = jtask.get("parameters").asArray()
                JsonHelper.read(jparams, task)
            }
            if (taskName == "timeLimits") {
                updateTimeLimits()
            }
        }

    }

    fun save() {

        preferencesFile.parentFile.mkdirs()

        val jroot = JsonObject()
        val jpreferences = JsonArray()
        jroot.add("preferences", jpreferences)

        preferenceTasksMap.values.forEach { task ->
            val jtask = JsonObject()
            jtask.add("name", task.taskD.name)
            val jparameters = JsonHelper.parametersAsJsonArray(task)
            jtask.add("parameters", jparameters)
            jpreferences.add(jtask)
        }

        BufferedWriter(OutputStreamWriter(FileOutputStream(preferencesFile))).use {
            jroot.writeTo(it, PrettyPrint.indentWithSpaces(4))
        }

        for (listener in listeners) {
            listener.preferencesChanged()
        }

        updateTimeLimits()
    }

    private fun updateTimeLimits() {

        timeLimitPreferences.addTimeLimit(TimedLimit("30 minutes, plus 10 minutes byo-yomi per 25 moves", 30.0, 60.0, byoYomiPeriod = 10.0, byoYomiScale = 60.0, byoYomiMoves = 25))
        timeLimitPreferences.addTimeLimit(TimedLimit("10 minutes, plus 30 seconds byo-yomi, 3 overtimes", 10.0, 60.0, byoYomiPeriod = 30.0, byoYomiScale = 1.0, overtimePeriod = 30.0, overtimePeriods = 3))
        timeLimitPreferences.addTimeLimit(TimedLimit("10 minutes, plus 30 seconds byo-yomi, no overtime", 10.0, 60.0, byoYomiPeriod = 30.0, byoYomiScale = 1.0))

        gamesPreferences.gamesP.value.forEach { compound ->
            val taskParameter = compound.find("type") as TaskParameter
            val task = taskParameter.value!! as AbstractGamePreferences
            timeLimitPreferences.updateTimeLimitChoice(task.timeLimitP)
        }
    }
}

interface PreferencesListener {

    fun preferencesChanged()
}
