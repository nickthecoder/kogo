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
package uk.co.nickthecoder.goko.gui

import javafx.scene.control.Label
import uk.co.nickthecoder.goko.model.LabelMark
import uk.co.nickthecoder.goko.model.Point

/**
 * A single mark on the board (or on top of stones). A child of MarksView.
 */
open class MarkView : Label {

    internal var marksView: MarksView? = null

    private var markStyle: String? = null

    var point: Point
        set(v) {
            field = v
            marksView?.node?.requestLayout()
        }

    constructor(point: Point, style: String? = null, text: String = "") : super(text) {
        this.point = point
        markStyle = style
        style?.let { styleClass.add(it) }
    }

    constructor(mark: LabelMark) : super(mark.text) {
        this.point = mark.point
        styleClass.add(mark.style)
    }

    init {
        styleClass.add("mark")
    }

    fun style(style: String) {
        styleClass.remove(markStyle)
        markStyle = style
        styleClass.add(style)
    }

    fun colorWhite(white: Boolean) {
        styleClass.removeAll("black", "white")
        styleClass.add(if (white) "white" else "black")
    }

}
