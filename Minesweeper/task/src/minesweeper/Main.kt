package minesweeper

import java.util.*

fun main() {
    Scanner(System.`in`).use { scanner ->
        print("How many mines do you want on the field? ")
        val numMines = scanner.nextInt()
        val game = Minesweeper(numMines)

        println()
        game.showBoard()
        var ok: Boolean
        do {
            print("Set/unset mines marks or claim a cell as free: ")
            val x = scanner.nextInt()
            val y = scanner.nextInt()
            val cmd = scanner.next()
            ok = game.makeMove(y - 1, x - 1, cmd.toUpperCase().let(::enumValueOf))
            println()
            game.showBoard(showMines = !ok)
        } while (ok && !game.complete())

        if (ok) {
            println("Congratulations! You found all mines!")
        } else {
            println("You stepped on a mine and failed!")
        }
    }
}