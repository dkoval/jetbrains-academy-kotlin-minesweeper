package minesweeper

class Minesweeper(numMines: Int = 10) {
    private val board: Board = Board(NUM_ROWS, NUM_COLS, numMines)

    companion object {
        const val NUM_ROWS = 9
        const val NUM_COLS = 9
    }

    enum class Command {
        FREE, MINE
    }

    fun showBoard(showMines: Boolean = false) {
        board.show(showMines)
    }

    fun makeMove(row: Int, col: Int, command: Command): Boolean =
            if (!board.isExplored(row, col)) {
                when (command) {
                    Command.FREE -> board.explore(row, col)
                    Command.MINE -> {
                        board.mark(row, col)
                        true
                    }
                }
            } else true

    fun complete(): Boolean = board.isSolved()
}