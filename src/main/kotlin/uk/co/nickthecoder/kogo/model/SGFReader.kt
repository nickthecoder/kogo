package uk.co.nickthecoder.kogo.model

import java.io.File
import java.io.IOException
import java.io.Reader

/**
 * Reads a .sgf file.
 * See http://www.red-bean.com/sgf/sgf4.html
 */
class SGFReader(var file: File) {

    lateinit var reader: Reader

    val buffer = charArrayOf(' ')

    var unread: Char? = null

    var size = 19

    /**
     * Reads an sgf file, returning a list of games, each game is represented as a tree of SGFNodes
     */
    fun readMultipleGames(): List<Game> {
        val result = mutableListOf<Game>()
        reader = file.bufferedReader()

        while (true) {
            val sgfRoot = readTree()
            if (sgfRoot == null) {
                break
            }
            val game = Game(size, size)
            updateRootNode(game, sgfRoot)
            addChildren(game, sgfRoot)

            //game.dumpTree()
            game.rewindTo(game.root)
            result.add(game)
        }
        reader.close()
        return result
    }

    /**
     * Reads an sgf file, returning a single Game (if the sgf file contains more than one game, then the first is
     * loaded, and the rest are silently ignored.
     */
    fun read(): Game {

        reader = file.bufferedReader()

        val sgfRoot = readTree()
        if (sgfRoot == null) {
            throw IOException("No game data found")
        }
        reader.close()

        // dumpTree(sgfRoot)

        val game = Game(size, size)
        updateRootNode(game, sgfRoot)
        addChildren(game, sgfRoot)

        game.dumpTree()
        game.file = file
        game.rewindTo(game.root)

        return game
    }

    fun updateRootNode(game: Game, sgfNode: SGFNode) {

        // TODO Add meta data such as play names, ranks etc

        updateNode(game, sgfNode)
    }

    fun updateNode(game: Game, sgfNode: SGFNode) {
        val currentNode = game.currentNode

        // I've seen PL properties in non-root nodes, so let's put it here, rather than updateRootNode. Grr.
        val toPlay = toStoneColor(sgfNode.getPropertyValue("PL"))
        if (toPlay != null) {
            game.currentNode.colorToPlay = toPlay
            game.playerToMove = game.players.get(toPlay)!!
        }

        val whites = sgfNode.getPropertyValues("AW")
        if (whites != null) {
            whites.forEach { str ->
                val point = toPoint(game.board, str)
                currentNode.addStoneOnly(game.board, point, StoneColor.WHITE)
            }
        }
        val blacks = sgfNode.getPropertyValues("AB")
        if (blacks != null) {
            blacks.forEach { str ->
                val point = toPoint(game.board, str)
                currentNode.addStoneOnly(game.board, point, StoneColor.BLACK)
            }
        }

        val removed = sgfNode.getPropertyValues("AE")
        if (removed != null) {
            removed.forEach { str ->
                val point = toPoint(game.board, str)
                currentNode.removeStoneOnly(game.board, point)
            }
        }

        sgfNode.getPropertyValue("C")?.let {
            currentNode.comment = it
        }
        sgfNode.getPropertyValue("N")?.let {
            currentNode.name = it
        }

        if (sgfNode.hasProperty("GW")) {
            currentNode.statuses.add(NodeStatus.GOOD_FOR_WHITE)
        }
        if (sgfNode.hasProperty("GB")) {
            currentNode.statuses.add(NodeStatus.GOOD_FOR_BLACK)
        }
        if (sgfNode.hasProperty("DM")) {
            currentNode.statuses.add(NodeStatus.EVEN)
        }
        if (sgfNode.hasProperty("HO")) {
            currentNode.statuses.add(NodeStatus.HOT_SPOT)
        }
        if (sgfNode.hasProperty("UC")) {
            currentNode.statuses.add(NodeStatus.UNCLEAR)
        }


        val labels = sgfNode.getPropertyValues("LB")
        labels?.forEach { str ->
            val mark = LabelMark(toPoint(game.board, str.substring(0, 2)), str.substring(3))
            currentNode.addMark(mark)
        }
        val circles = sgfNode.getPropertyValues("CR")
        circles?.forEach { str ->
            val mark = CircleMark(toPoint(game.board, str))
            currentNode.addMark(mark)
        }
        val crosses = sgfNode.getPropertyValues("MA")
        crosses?.forEach { str ->
            val mark = CrossMark(toPoint(game.board, str))
            currentNode.addMark(mark)
        }
        val squares = sgfNode.getPropertyValues("SQ")
        squares?.forEach { str ->
            val mark = CircleMark(toPoint(game.board, str))
            currentNode.addMark(mark)
        }
        val triangles = sgfNode.getPropertyValues("TR")
        triangles?.forEach { str ->
            val mark = TriangleMark(toPoint(game.board, str))
            currentNode.addMark(mark)
        }
        // TODO "DD" to dim out the point
        // TODO "LN" for lines
        // TODO Update other node data.
    }

    fun addChildren(game: Game, sgfParent: SGFNode) {
        val fromNode = game.currentNode
        var passNode: PassNode? = null

        for (sgfChild in sgfParent.chldren) {
            game.rewindTo(fromNode) // Will do nothing for the first child in the list

            var gameNode = createGameNode(game, sgfChild)
            if (gameNode is MoveNode && fromNode is MoveNode && gameNode.color == fromNode.color) {
                // Add an extra Pass node (SGF does not have a concept of a pass node!
                // But only add ONE pass node, if there are many variations after the pass.
                if (passNode == null) {
                    passNode = PassNode(game.playerToMove.color.opposite())
                    game.addNode(passNode)
                }
                passNode.apply(game, null)
            }
            if (gameNode is SetupNode && game.currentNode is SetupNode) {
                // There are two setup nodes in a row, which seems pointless, so lets merge them into one node.
                gameNode = game.currentNode
                updateNode(game, sgfChild)
                game.moveBack()
                game.moveForward()
            } else {
                game.addNode(gameNode)
                gameNode.apply(game, null)
                updateNode(game, sgfChild)
            }
            addChildren(game, sgfChild)
        }
    }

    fun createGameNode(game: Game, sgfNode: SGFNode): GameNode {
        val white = sgfNode.getPropertyValue("W")
        if (white != null) {
            return MoveNode(toPoint(game.board, white), StoneColor.WHITE)
        }
        val black = sgfNode.getPropertyValue("B")
        if (black != null) {
            return MoveNode(toPoint(game.board, black), StoneColor.BLACK)
        }
        return SetupNode(game.playerToMove.color)
    }

    fun toStoneColor(str: String?): StoneColor? {
        // The spec says that only B and W are allowed, but I've seen 1 and 2 used. Grr.
        if (str == "B" || str == "1") {
            return StoneColor.BLACK
        } else if (str == "W" || str == "2") {
            return StoneColor.WHITE
        }
        return null
    }

    fun toPoint(board: Board, str: String): Point {
        val x: Int = str[0].toLowerCase() - 'a'
        val y: Int = board.sizeY - (str[1].toLowerCase() - 'a') - 1
        return Point(x, y)
    }

    fun readTree(): SGFNode? {

        // Skip ahead till the first "(;" is found. Some sgf files contain comments at the top, which is NOT
        // in the spec, but hey, what can you do!
        var c = readCharSkippingWhiteSpace()
        while (c != null) {
            if (c == '(') {
                c = readChar()
                if (c == ';') {
                    unreadChar(c)
                    val branch = SGFNode()
                    readBranch(branch)
                    return branch
                }
            }
            c = readCharSkippingWhiteSpace()
        }
        return null
    }

    fun readBranch(branch: SGFNode) {

        var first = true
        var node = branch

        var c = readCharSkippingWhiteSpace()
        while (true) {
            if (c == ';') {
                if (first) {
                    first = false
                } else {
                    val newNode = SGFNode()
                    node.chldren.add(newNode)
                    node = newNode
                }
                readProperties(node)
            } else if (c == ')') {
                return
            } else if (c == '(') {
                readBranches(node)
            }
            c = readCharSkippingWhiteSpace()
        }
    }

    fun readBranches(parent: SGFNode) {
        while (true) {
            val newNode = SGFNode()
            parent.chldren.add(newNode)
            readBranch(newNode)
            val c = readCharSkippingWhiteSpace()
            if (c != '(') {
                unreadChar(c)
                return
            }
        }
    }

    fun readProperties(node: SGFNode) {

        while (true) {
            var ident = ""
            var c = readCharSkippingWhiteSpace()
            if (c?.isUpperCase() != true) {
                unreadChar(c)
                return
            }

            while (c?.isUpperCase() == true) {
                ident += c
                c = readChar()
            }
            while (c == '[') {
                val str = readPropertyValue()
                if (ident == "SZ") {
                    size = Integer.parseInt(str)
                }
                node.addProperty(ident, str)

                c = readCharSkippingWhiteSpace()
            }

            unreadChar(c)
            if (c?.isUpperCase() != true) {
                return
            }
        }
    }

    fun readPropertyValue(): String {
        var str = ""
        var escaped = false

        while (true) {
            var c = readChar()
            if (c == null) {
                throw IOException("End of file while reading property value")
            }
            if (escaped) {
                str += c
                escaped = false
            } else {
                if (c == '\\') {
                    escaped = true
                } else if (c == ']') {
                    return str
                } else {
                    str += c
                }
            }
        }
    }

    fun readCharSkippingWhiteSpace(): Char? {
        var symbol = readChar()
        while (symbol?.isWhitespace() == true) {
            symbol = readChar()
        }
        return symbol
    }

    fun unreadChar(c: Char?) {
        unread = c
    }

    fun readChar(): Char? {
        if (unread != null) {
            val tmp = unread
            unread = null
            return tmp
        }

        val read = reader.read(buffer)
        if (read < 0) {
            return null
        } else {
            return buffer[0]
        }
    }


    fun dumpTree(sgfNode: SGFNode) {

        fun dumpTree(indent: Int, sgfNode2: SGFNode) {
            print(" ".repeat(indent * 4))
            for (child in sgfNode2.chldren) {
                dumpTree(indent + 1, child)
            }
        }
        dumpTree(0, sgfNode)
    }
}

class SGFNode() {

    private val listProperties = mutableMapOf<String, MutableList<String>>()

    val chldren = mutableListOf<SGFNode>()

    fun addProperty(propertyName: String, value: String) {
        var list = listProperties.get(propertyName)
        if (list == null) {
            list = mutableListOf(value)
            listProperties.put(propertyName, list)
        } else {
            list.add(value)
        }
    }

    fun hasProperty(propertyName: String): Boolean {
        return listProperties.get(propertyName) != null
    }

    fun getPropertyValue(propertyName: String): String? {
        val list = listProperties.get(propertyName)
        if (list == null) {
            return null
        } else {
            return list[0]
        }
    }

    fun getPropertyValues(propertyName: String): List<String>? {
        return listProperties.get(propertyName)
    }

    override fun toString(): String {
        val builder = StringBuilder()

        builder.append("Node ")

        val black = getPropertyValue("B")
        val white = getPropertyValue("W")

        if (black != null) {
            builder.append("B @ $black ")
        } else if (white != null) {
            builder.append("W @ $white ")
        } else {
            builder.append("Setup Node $listProperties")
        }
        return builder.toString()
    }
}
