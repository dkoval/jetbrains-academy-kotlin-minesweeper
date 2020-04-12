package minesweeper

import kotlin.random.Random

class Minesweeper(private val numMines: Int = 10) {
    private val numRows: Int = NUM_ROWS
    private val numCols: Int = NUM_COLS
    private var field: Array<Array<Cell>> = generateRandomField(numRows, numCols)

    init {
        analyseField()
    }

    companion object {
        const val NUM_ROWS = 9
        const val NUM_COLS = 9
    }

    enum class Command {
        FREE, MINE
    }

    private sealed class Cell(var status: CellStatus = CellStatus.Unexplored()) {
        class Empty : Cell()
        class WithMinesAround(val numMines: Int) : Cell()
        class Mine : Cell()

        companion object {
            const val UNEXPLORED_CELL_SYMBOL = '.'
            const val UNEXPLORED_MARKED_CELL_SYMBOL = '*'
            const val EXPLORED_CELL_SYMBOL = '/'
            const val MINED_CELL_SYMBOL = 'X'
        }

        val marked: Boolean get() = status is CellStatus.Unexplored && (status as CellStatus.Unexplored).marked

        fun displaySymbol(showMine: Boolean = false): Char =
                if (showMine && this is Mine) {
                    MINED_CELL_SYMBOL
                } else when (status) {
                    is CellStatus.Unexplored -> {
                        val unexplored = status as CellStatus.Unexplored
                        if (unexplored.marked) UNEXPLORED_MARKED_CELL_SYMBOL else UNEXPLORED_CELL_SYMBOL
                    }
                    is CellStatus.Explored -> {
                        when (this) {
                            is Empty -> EXPLORED_CELL_SYMBOL
                            is WithMinesAround -> '0' + numMines
                            is Mine -> MINED_CELL_SYMBOL
                        }
                    }
                }
    }

    private sealed class CellStatus {
        class Unexplored(var marked: Boolean = false) : CellStatus()
        object Explored : CellStatus()
    }

    fun displayField(showMines: Boolean = false) {
        println(" │123456789│\n" +
                "—│—————————│")
        field.forEachIndexed { i, row ->
            print("${i + 1}│")
            row.forEach { cell -> print(cell.displaySymbol(showMines)) }
            println("│")
        }
        println("—│—————————│")
    }

    fun openCell(row: Int, col: Int, command: Command): Boolean {
        val cell = field[row][col]
        return if (cell.status is CellStatus.Unexplored) {
            when (command) {
                Command.FREE -> {
                    cell.status = CellStatus.Explored
                    cell !is Cell.Mine
                }
                Command.MINE -> {
                    val unexplored = cell.status as CellStatus.Unexplored
                    unexplored.marked = !unexplored.marked
                    true
                }
            }
        } else true
    }

    fun done(): Boolean {
        val numMinesMarked = field.fold(0) { acc, row ->
            acc + row.count { cell -> cell is Cell.Mine && cell.marked }
        }
        return numMinesMarked == numMines
    }

    private fun generateRandomField(numRows: Int, numCols: Int): Array<Array<Cell>> {
        val fieldToReturn = init2DArray<Cell>(numRows, numCols) { _, _ -> Cell.Empty() }
        var numMinesSoFar = 0
        // randomly put `numMines` to the field
        while (numMinesSoFar < numMines) {
            val randomNum = Random.nextInt(0, numRows * numCols)
            val row = randomNum / numRows
            val col = randomNum % numCols
            if (fieldToReturn[row][col] is Cell.Empty) {
                fieldToReturn[row][col] = Cell.Mine()
                numMinesSoFar++
            }
        }
        return fieldToReturn
    }

    private fun analyseField() {
        field.forEachIndexed { i, row ->
            row.forEachIndexed { j, cell ->
                if (cell is Cell.Empty) {
                    val numMinesAround = numMinesAround(i, j)
                    if (numMinesAround in 1..8) {
                        field[i][j] = Cell.WithMinesAround(numMinesAround)
                    }
                }
            }
        }
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

        // cell in the middle of the field
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

    private fun isMine(row: Int, col: Int): Boolean = field[row][col] is Cell.Mine
}