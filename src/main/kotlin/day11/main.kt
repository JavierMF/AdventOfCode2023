package day11

// https://adventofcode.com/2023/day/11

import Cell
import Coords
import DayChallenge
import getNonBlankFileLines
import toCells

fun main() = Day11Challenge.run()

object Day11Challenge: DayChallenge(
    day = "11",
    part1SampleResult = 374,
    part2SampleResult = 82000210
) {

    override fun runPart1(filePath: String): Long =
            Universe(getNonBlankFileLines(filePath).toCells()).part1()

    override fun runPart2(filePath: String): Long =
            Universe(getNonBlankFileLines(filePath).toCells()).part2()
}

data class Universe(private val cells: List<Cell>) {
    private val maxRow = cells.maxOf { it.coords.y }
    private val maxCol = cells.maxOf { it.coords.x }
    private val emptyRows = (0..maxRow).toSet().subtract(cells.map { it.coords.y }.toSet())
    private val emptyCols = (0..maxCol).toSet().subtract(cells.map { it.coords.x }.toSet())

    private fun solveWithExpansionFactor(expansionFactor: Int): Long {
        val expandedCells = cells.map { it.includeExpansion(expansionFactor) }
        return expandedCells.flatMapIndexed { index: Int, cell: Cell ->
            expandedCells.drop(index + 1).map { cell.coords.distanceTaxicabTo(it.coords).toLong() }
        }.sum()
    }

    fun part1(): Long = solveWithExpansionFactor(2)
    fun part2(): Long = solveWithExpansionFactor(1000000)

    private fun Cell.includeExpansion(expansionFactor: Int): Cell {
        val emptyColsBefore = emptyCols.count { it < this.coords.x }
        val emptyRowsBefore = emptyRows.count { it < this.coords.y }
        val newX = this.coords.x + (expansionFactor * emptyColsBefore) - emptyColsBefore
        val newY = this.coords.y + (expansionFactor * emptyRowsBefore) - emptyRowsBefore
        return this.copy(coords = Coords(x = newX, y = newY))
    }
}
