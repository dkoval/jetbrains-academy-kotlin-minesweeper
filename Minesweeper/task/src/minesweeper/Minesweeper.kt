package minesweeper

import kotlin.random.Random

class Minesweeper(private val numMines: Int = 10) {
    private val numRows: Int = NUM_ROWS
    private val numCols: Int = NUM_COLS
    private var board: Array<Array<Cell>> = generateRandomBoard(numRows, numCols)
    private var firstMove: Boolean = true

    companion object {
        const val NUM_ROWS = 9
        const val NUM_COLS = 9
    }

    enum class Command {
        FREE, MINE
    }

    private sealed class Cell {
        class Safe(var numMinesAround: Int = -1) : Cell()
        class Mine : Cell()

        var explored: Boolean = false
            set(value) {
                if (field && !value) {
                    throw IllegalStateException("Explored cell cannot be made unexplored")
                }
                if (!field && value) {
                    marked = false
                    field = value
                }
            }

        var marked: Boolean = false
            set(value) {
                if (explored) {
                    throw IllegalStateException("Explored cell cannot be [un]marked")
                }
                field = value
            }

        companion object {
            const val UNEXPLORED_CELL_SYMBOL = '.'
            const val UNEXPLORED_MARKED_CELL_SYMBOL = '*'
            const val EXPLORED_CELL_SYMBOL = '/'
            const val MINED_CELL_SYMBOL = 'X'
        }

        fun displaySymbol(showMine: Boolean = false): Char =
                if (showMine && this is Mine) {
                    MINED_CELL_SYMBOL
                } else if (explored) {
                    if (this !is Safe) {
                        throw IllegalStateException("Mined cell cannot be explored")
                    }
                    if (numMinesAround == 0) EXPLORED_CELL_SYMBOL
                    else '0' + numMinesAround
                } else {
                    if (marked) UNEXPLORED_MARKED_CELL_SYMBOL
                    else UNEXPLORED_CELL_SYMBOL
                }
    }

    fun showBoard(showMines: Boolean = false) {
        println(" │123456789│\n" +
                "—│—————————│")
        board.forEachIndexed { i, row ->
            print("${i + 1}│")
            row.forEach { cell -> print(cell.displaySymbol(showMine = showMines)) }
            println("│")
        }
        println("—│—————————│")
    }

    fun exploreCell(row: Int, col: Int, command: Command): Boolean {
        var cell = board[row][col]
        return if (cell.explored) true else when (command) {
            Command.FREE -> {
                cell = ensureFirstMoveIsSafe(row, col)
                val ok = cell !is Cell.Mine
                if (ok) {
                    exploreCellsAround(row, col)
                }
                ok
            }
            Command.MINE -> {
                cell.marked = !cell.marked
                true
            }
        }
    }

    private fun ensureFirstMoveIsSafe(row: Int, col: Int): Cell {
        if (!firstMove) {
            return board[row][col]
        }
        firstMove = false
        val cell1 = board[row][col]
        if (cell1 !is Cell.Mine) {
            return cell1
        }
        // swap mine with the very first safe cell
        for (i in 0 until numRows) {
            for (j in 0 until numCols) {
                val cell2 = board[i][j]
                if (cell2 !is Cell.Mine) {
                    board[row][col] = cell2
                    board[i][j] = cell1
                    return cell2
                }
            }
        }
        throw IllegalStateException("No mined cells found on the board")
    }

    private fun exploreCellsAround(row: Int, col: Int) {
        val cell = board[row][col]
        if (cell.explored) {
            return
        }
        if (cell !is Cell.Safe) {
            return
        }
        cell.explored = true
        cell.numMinesAround = numMinesAround(row, col)
        if (cell.numMinesAround == 0) {
            doExploreCellsAround(row - 1, col - 1)
            doExploreCellsAround(row - 1, col)
            doExploreCellsAround(row - 1, col + 1)
            doExploreCellsAround(row, col - 1)
            doExploreCellsAround(row, col + 1)
            doExploreCellsAround(row + 1, col - 1)
            doExploreCellsAround(row + 1, col)
            doExploreCellsAround(row + 1, col + 1)
        }
    }

    private fun doExploreCellsAround(row: Int, col: Int) {
        if (isWithinBoard(row, col)) {
            exploreCellsAround(row, col)
        }
    }

    fun complete(): Boolean {
        var numMarkedCells = 0
        var numMarkedMines = 0
        var numExploredCells = 0
        board.forEach { row ->
            row.forEach { cell ->
                if (cell.explored) {
                    numExploredCells++
                } else {
                    if (cell.marked) {
                        numMarkedCells++
                        if (cell is Cell.Mine) {
                            numMarkedMines++
                        }
                    }
                }
            }
        }
        // user wins by marking all mines correctly or by exploring all safe cells
        return numMarkedCells == numMines && numMarkedMines == numMines
                || numExploredCells == numRows * numCols - numMines
    }

    private fun generateRandomBoard(numRows: Int, numCols: Int): Array<Array<Cell>> {
        val boardToReturn = init2DArray<Cell>(numRows, numCols) { _, _ -> Cell.Safe() }
        var numMinesSoFar = 0
        // put random numMines on the board
        while (numMinesSoFar < numMines) {
            val randomNum = Random.nextInt(0, numRows * numCols)
            val row = randomNum / numRows
            val col = randomNum % numCols
            if (boardToReturn[row][col] is Cell.Safe) {
                boardToReturn[row][col] = Cell.Mine()
                numMinesSoFar++
            }
        }
        return boardToReturn
    }

    private fun numMinesAround(row: Int, col: Int): Int = when {
        // cell is in top-level corner
        row == 0 && col == 0 ->
            countMines(
                    0 to 1,
                    1 to 0,
                    1 to 1)

        // cell is in top-right corner
        row == 0 && col == numCols - 1 ->
            countMines(
                    0 to numCols - 2,
                    1 to numCols - 2,
                    1 to numCols - 1)

        // cell is in bottom-left corner
        row == numRows - 1 && col == 0 ->
            countMines(
                    numRows - 2 to 0,
                    numRows - 2 to 1,
                    numRows - 1 to 1)

        // cell is in bottom-right corner
        row == numRows - 1 && col == numCols - 1 ->
            countMines(
                    numRows - 2 to numCols - 2,
                    numRows - 2 to numCols - 1,
                    numRows - 1 to numCols - 2)

        // cell is on the top side
        row == 0 ->
            countMines(
                    0 to col - 1,
                    0 to col + 1,
                    1 to col - 1,
                    1 to col,
                    1 to col + 1)

        // cell is on the left side
        col == 0 ->
            countMines(
                    row - 1 to 0,
                    row - 1 to 1,
                    row to 1,
                    row + 1 to 0,
                    row + 1 to 1)

        // cell is on the right side
        col == numCols - 1 ->
            countMines(
                    row - 1 to numCols - 2,
                    row - 1 to numCols - 1,
                    row to numCols - 2,
                    row + 1 to numCols - 2,
                    row + 1 to numCols - 1)

        // cell is on the bottom side
        row == numRows - 1 ->
            countMines(
                    numRows - 2 to col - 1,
                    numRows - 2 to col,
                    numRows - 2 to col + 1,
                    numRows - 1 to col - 1,
                    numRows - 1 to col + 1)

        // cell is in the middle
        else ->
            countMines(
                    row - 1 to col - 1,
                    row - 1 to col,
                    row - 1 to col + 1,
                    row to col - 1,
                    row to col + 1,
                    row + 1 to col - 1,
                    row + 1 to col,
                    row + 1 to col + 1)
    }

    private fun countMines(vararg cells: Pair<Int, Int>): Int = cells.count { (row, col) -> isMine(row, col) }

    private fun isWithinBoard(row: Int, col: Int): Boolean = row in 0 until numRows && col in 0 until numRows

    private fun isMine(row: Int, col: Int): Boolean = board[row][col] is Cell.Mine
}