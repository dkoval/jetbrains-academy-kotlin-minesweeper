package minesweeper

import kotlin.random.Random

class Minesweeper(
        private val numMines: Int = 10,
        numRows: Int = DEFAULT_NUM_ROWS,
        numCols: Int = DEFAULT_NUM_COLS
) {

    private var field: Array<CharArray> = generateRandomField(numRows, numCols)

    companion object {
        const val DEFAULT_NUM_ROWS = 9
        const val DEFAULT_NUM_COLS = 9
        const val BASE_MARKER = '.'
        const val MINE_MARKER = 'X'

        private fun initField(numRows: Int, numCols: Int, init: (i: Int, j: Int) -> Char): Array<CharArray> =
                Array(numRows) { i ->
                    CharArray(numCols) { j ->
                        init(i, j)
                    }
                }
    }

    private fun generateRandomField(numRows: Int, numCols: Int): Array<CharArray> {
        val fieldToReturn = initField(numRows, numCols) { _, _ ->  BASE_MARKER }
        var numMinesSoFar = 0
        // randomly put `numMines` to the field
        while (numMinesSoFar < numMines) {
            val randomNum = Random.nextInt(0, numRows * numCols)
            val row = randomNum / numRows
            val col = randomNum % numCols
            if (fieldToReturn[row][col] == BASE_MARKER) {
                fieldToReturn[row][col] = MINE_MARKER
                numMinesSoFar++
            }
        }
        return fieldToReturn
    }

    fun displayField() {
        field.forEach { row ->
            row.forEach(::print)
            println()
        }
    }
}