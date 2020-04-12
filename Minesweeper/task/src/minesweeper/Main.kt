package minesweeper

import java.util.*

fun main() {
    Scanner(System.`in`).use { scanner ->
        print("How many mines do you want on the field? ")
        val numMines = scanner.nextInt()
        val minesweeper = Minesweeper(numMines)

        minesweeper.displayField()
        var steppedOnMine: Boolean
        do {
            print("Set/unset mines marks or claim a cell as free: ")
            println()
            val x = scanner.nextInt()
            val y = scanner.nextInt()
            val cmd = scanner.next()
            steppedOnMine = minesweeper.openCell(y - 1, x - 1, cmd.toLowerCase().let(::enumValueOf))
            minesweeper.displayField()
        } while (!steppedOnMine && !minesweeper.done())

        if (steppedOnMine) {
            println("You stepped on a mine and failed!")
        } else {
            println("Congratulations! You found all mines!")
        }
    }
}