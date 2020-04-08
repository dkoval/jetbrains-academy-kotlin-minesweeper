package minesweeper

import java.util.*

fun main() {
    Scanner(System.`in`).use { scanner ->
        print("How many mines do you want on the field? ")
        val numMines = scanner.nextInt()
        val minesweeper = Minesweeper(numMines)
        do {
            println()
            minesweeper.displayField()
            do {
                print("Set/delete mines marks (x and y coordinates): ")
                val x = scanner.nextInt()
                val y = scanner.nextInt()
                val ok = minesweeper.markCell(y - 1, x - 1)
                if (!ok) {
                    println("There is a number here!")
                }
            } while (!ok)
            val done = minesweeper.done()
            if (done) {
                println("Congratulations! You found all mines!")
            }
        } while (!done)
    }
}