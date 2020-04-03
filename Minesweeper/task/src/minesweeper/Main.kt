package minesweeper

import java.util.*

fun main() {
    print("How many mines do you want on the field? ")
    val scanner = Scanner(System.`in`)
    val numMines = scanner.use(Scanner::nextInt)
    Minesweeper(numMines).displayField()
}
