package minesweeper

import kotlin.random.Random

class Board(
        private val numRows: Int,
        private val numCols: Int,
        private val numMines: Int
) {
    private val board: Array<Array<Cell>> = init2DArray(numRows, numCols) { _, _ -> Cell.Unexplored }
    private val mines: Array<Array<Boolean>> = generateRandomMines()
    private var numMarkedCells: Int = 0
    private var numExploredCells: Int = 0
    private var numMarkedMines: Int = 0
    private var firstCellExplored: Boolean = false

    private fun generateRandomMines(): Array<Array<Boolean>> {
        val result = init2DArray(numRows, numCols) { _, _ -> false }
        var numMinesSoFar = 0
        while (numMinesSoFar < numMines) {
            val rnd = Random.nextInt(0, numRows * numCols)
            val row = rnd / numRows
            val col = rnd % numCols
            if (!result[row][col]) {
                result[row][col] = true
                numMinesSoFar++
            }
        }
        return result
    }

    fun show(showMines: Boolean = false) {
        println(" │123456789│\n" +
                "—│—————————│")
        for (i in 0 until numRows) {
            print("${i + 1}│")
            for (j in 0 until numCols) {
                val displaySymbol =
                        if (showMines && mines[i][j]) Cell.MINED_CELL_SYMBOL
                        else board[i][j].displaySymbol
                print(displaySymbol)
            }
            println("│")
        }
        println("—│—————————│")
    }

    fun mark(row: Int, col: Int) {
        when (board[row][col]) {
            is Cell.Unexplored -> {
                board[row][col] = Cell.Marked
                numMarkedCells++
                if (mines[row][col]) {
                    numMarkedMines++
                }
            }
            is Cell.Marked -> {
                board[row][col] = Cell.Unexplored
                numMarkedCells--
                if (mines[row][col]) {
                    numMarkedMines--
                }
            }
            is Cell.Explored -> throw IllegalStateException("Explored cell cannot be [un]marked anymore")
        }
    }

    fun isExplored(row: Int, col: Int): Boolean = board[row][col] is Cell.Explored

    fun explore(row: Int, col: Int): Boolean {
        ensureFirstCellExploredIsNotMine(row, col)
        if (mines[row][col]) {
            return false
        }
        doExplore(row, col)
        return true
    }

    private fun doExplore(row: Int, col: Int) {
        if (!isWithinBoundaries(row, col)) {
            return
        }
        if (board[row][col] is Cell.Explored) {
            return
        }
        val numMinesAround = numMinesAround(row, col)
        board[row][col] = Cell.Explored(numMinesAround)
        numExploredCells++
        if (numMinesAround == 0) {
            // explore all the cells around (row, col)
            doExplore(row - 1, col - 1)
            doExplore(row - 1, col)
            doExplore(row - 1, col + 1)
            doExplore(row, col - 1)
            doExplore(row, col + 1)
            doExplore(row + 1, col - 1)
            doExplore(row + 1, col)
            doExplore(row + 1, col + 1)
        }
    }

    private fun numMinesAround(row: Int, col: Int): Int =
            countMines(
                    row - 1 to col - 1,
                    row - 1 to col,
                    row - 1 to col + 1,
                    row to col - 1,
                    row to col + 1,
                    row + 1 to col - 1,
                    row + 1 to col,
                    row + 1 to col + 1)

    private fun countMines(vararg cells: Pair<Int, Int>): Int =
            cells.count { (row, col) -> isWithinBoundaries(row, col) && mines[row][col] }

    private fun isWithinBoundaries(row: Int, col: Int): Boolean =
            row in 0 until numRows && col in 0 until numRows

    private fun ensureFirstCellExploredIsNotMine(row: Int, col: Int) {
        if (firstCellExplored) {
            return
        }
        firstCellExplored = true
        if (!mines[row][col]) {
            return
        }
        // swap mine with the very first safe cell
        for (i in 0 until numRows) {
            for (j in 0 until numCols) {
                if (!mines[i][j]) {
                    mines[i][j] = true
                    mines[row][col] = false
                    return
                }
            }
        }
    }

    fun isSolved(): Boolean {
        // user wins by marking all mines correctly or by exploring all safe cells
        return numMarkedCells == numMines && numMarkedMines == numMines
                || numExploredCells == numRows * numCols - numMines
    }
}

sealed class Cell {
    abstract val displaySymbol: Char

    object Unexplored : Cell() {
        override val displaySymbol: Char = UNEXPLORED_CELL_SYMBOL
    }

    object Marked : Cell() {
        override val displaySymbol: Char = MARKED_CELL_SYMBOL
    }

    class Explored(private val numMinesAround: Int) : Cell() {
        override val displaySymbol: Char = EXPLORED_CELL_SYMBOL
            get() = if (numMinesAround > 0) '0' + numMinesAround else field
    }

    companion object {
        const val UNEXPLORED_CELL_SYMBOL = '.'
        const val MARKED_CELL_SYMBOL = '*'
        const val EXPLORED_CELL_SYMBOL = '/'
        const val MINED_CELL_SYMBOL = 'X'
    }
}