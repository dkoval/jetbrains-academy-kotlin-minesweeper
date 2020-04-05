package minesweeper

import kotlin.random.Random

class Minesweeper(private val numMines: Int = 10) {
    private val numRows: Int = DEFAULT_NUM_ROWS
    private val numCols: Int = DEFAULT_NUM_COLS
    private var field: Array<CharArray> = generateRandomField(numRows, numCols)

    init {
        analyseField()
    }

    companion object {
        const val DEFAULT_NUM_ROWS = 9
        const val DEFAULT_NUM_COLS = 9
        const val EMPTY_CELL_MARKER = '.'
        const val MINED_CELL_MARKER = 'X'

        private fun initField(numRows: Int, numCols: Int, init: (i: Int, j: Int) -> Char): Array<CharArray> =
                Array(numRows) { i ->
                    CharArray(numCols) { j ->
                        init(i, j)
                    }
                }
    }

    private fun generateRandomField(numRows: Int, numCols: Int): Array<CharArray> {
        val fieldToReturn = initField(numRows, numCols) { _, _ -> EMPTY_CELL_MARKER }
        var numMinesSoFar = 0
        // randomly put `numMines` to the field
        while (numMinesSoFar < numMines) {
            val randomNum = Random.nextInt(0, numRows * numCols)
            val row = randomNum / numRows
            val col = randomNum % numCols
            if (fieldToReturn[row][col] == EMPTY_CELL_MARKER) {
                fieldToReturn[row][col] = MINED_CELL_MARKER
                numMinesSoFar++
            }
        }
        return fieldToReturn
    }

    private fun analyseField() {
        field.forEachIndexed { i, row ->
            row.forEachIndexed { j, cell ->
                if (cell == EMPTY_CELL_MARKER) {
                    val numMinesAround = numMinesAround(i, j)
                    if (numMinesAround in 1..8) {
                        field[i][j] = '0' + numMinesAround
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

    private fun isMine(row: Int, col: Int): Boolean = field[row][col] == MINED_CELL_MARKER

    fun displayField() {
        field.forEach { row ->
            row.forEach(::print)
            println()
        }
    }
}