package minesweeper

inline fun <reified T> init2DArray(numRows: Int, numCols: Int, init: (row: Int, col: Int) -> T): Array<Array<T>> =
        Array(numRows) { row ->
            Array(numCols) { col ->
                init(row, col)
            }
        }