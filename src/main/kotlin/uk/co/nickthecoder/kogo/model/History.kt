package uk.co.nickthecoder.kogo.model

/**
 * Keeps track of the nodes traversed, so that when moving backwards and then forwards again, you move through the
 * the same set of nodes. Moving forwards without moving backwards will pick the first child node.
 */
class History(val game: Game) : GameListener {

    private val history = mutableListOf<GameNode>()

    init {
        game.gameListeners.add(this)
    }

    fun forward(n: Int = 1) {
        for ( foo in 1..n ) {
            val i = history.indexOf(game.currentNode)
            if (i >= 0 && i < history.size - 1) {
                val node = history[i + 1]
                node.apply(game, null)
            } else {
                game.moveForward()
            }
        }
    }

    /**
     * Rewind until we are on the main line (i.e. first child nodes only), and then clear the future nodes, so that
     * forward() will continue along the main line.
     */
    fun mainLine() {
        for (node in history) {
            val parent = node.parent
            if (parent != null && parent.children[0] != node) {
                game.rewindTo(parent)
                val i = history.indexOf(game.currentNode)
                if (i >= 0) {
                    while (history.size > i) {
                        history.removeAt(history.size - 1)
                    }
                } else {
                    println("Hmm, something went wrong in History.mainLine")
                    println("Main history = $history")
                }
                println("Main history = $history")
                return
            }
        }
    }

    override fun moved() {

        val currentNode = game.currentNode

        val c = history.indexOf(currentNode)
        if (c < 0) {
            val p = history.indexOf(currentNode.parent)
            if (p >= 0) {
                while (history.size > p + 1) {
                    history.removeAt(history.size - 1)
                }
                history.add(currentNode)
            } else {
                history.clear()
                var node: GameNode? = currentNode
                while (node != null) {
                    history.add(0, node)
                    node = node.parent
                }
            }
        }
    }
}