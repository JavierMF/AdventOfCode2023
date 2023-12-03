package day03

// https://adventofcode.com/2023/day/3

import Cell
import Coords
import DayChallenge
import Grid
import getNonBlankFileLines
import toCells

fun main() = Day03Challenge().run()

class Day03Challenge: DayChallenge("03") {

    override fun runPart1(filePath: String): Int =
        EngineSchematic(getNonBlankFileLines(filePath).toCells())
            .numbersWithNeighbourSymbol().sumOf { it.number }

    override fun runPart2(filePath: String): Int =
        EngineSchematic(getNonBlankFileLines(filePath).toCells())
            .gearRatios().sum()

    override fun part1SampleResult(): Int = 4361
    override fun part2SampleResult(): Int = 467835

}

data class EngineSchematic(private val cells: List<Cell>) {
    private val grid = Grid(cells)

    private val numbers: List<EngineNumber> = NumberFinder(grid).findAll()

    fun numbersWithNeighbourSymbol() = numbers.filter { it.anyNeighbourIsSymbol() }

    data class EngineNumber(val number:Int, val coords: Set<Coords>) {
        fun neighbours() = coords.flatMap { it.neighbours() }
    }

    private fun EngineNumber.anyNeighbourIsSymbol(): Boolean =
        neighbours().any { grid.cellInCoords(it)?.isSymbol == true }

    fun gearRatios(): List<Int> = cells
        .filter { it.value == '*' }
        .mapNotNull {cell ->
            val numberNeighbours = cell.coords.neighbours().mapNotNull { engineNeighbourCoords ->
                numbers.firstOrNull { it.coords.contains(engineNeighbourCoords) }
            }.toSet()
            if (numberNeighbours.size == 2) {
                numberNeighbours.first().number * numberNeighbours.last().number
            } else null
        }
}

class NumberFinder(private val grid: Grid) {
    private val usedCellsCoords = mutableSetOf<Coords>()

    fun findAll(): List<EngineSchematic.EngineNumber> =
        grid.cells.mapNotNull { cell ->
            if (cell.isNewNumberCell()) {
                findEngineNumber(cell).also { usedCellsCoords.addAll(it.coords) }
            } else null
        }

    private fun Cell.isNewNumberCell(): Boolean = !isSymbol && ! usedCellsCoords.contains(coords)

    private fun findEngineNumber(init: Cell): EngineSchematic.EngineNumber {
        val numberCoords = mutableListOf(init)
        var rightCell: Cell? = init.rightCell()
        while((rightCell!=null) && !rightCell.isSymbol) {
            numberCoords.add(rightCell)
            rightCell = rightCell.rightCell()
        }
        val value = numberCoords.map { it.value }.joinToString("").toInt()
        return EngineSchematic.EngineNumber(value, numberCoords.map { it.coords }.toSet())
    }

    private fun Cell.rightCell(): Cell? = grid.cellInCoords(coords.rightCoord())

}
