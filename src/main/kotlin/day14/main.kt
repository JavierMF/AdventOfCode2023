package day14

// https://adventofcode.com/2023/day/14

import Cell
import Coords
import DayChallenge
import Direction
import getNonBlankFileLines
import toCells

fun main() = Day14Challenge.run()

object Day14Challenge: DayChallenge(
    day = "14",
    part1SampleResult = 136,
    part2SampleResult = 64
) {

    override fun runPart1(filePath: String): Long =
        Desert(getNonBlankFileLines(filePath).toCells()).part1()

    override fun runPart2(filePath: String): Long =
        Desert(getNonBlankFileLines(filePath).toCells()).part2()
}

data class Desert(val rocks: List<Cell>) {
    private val rocksByCoords: Map<Coords, Cell> = rocks.associateBy { it.coords }

    fun part1(): Long = this.moveTo(Direction.UP).rocksWeigth()
    fun part2(): Long {
        var lastDesert = this
        repeat(1000000000) {
            lastDesert = this.moveTo(Direction.UP)
                    .moveTo(Direction.LEFT)
                    .moveTo(Direction.DOWN)
                    .moveTo(Direction.RIGHT)
        }
        return lastDesert.rocksWeigth()
    }

    private fun rocksWeigth(): Long {
        val rows = rocks.maxOf { it.coords.y } + 1
        return rocks.filter { it.isRoundRock() }.sumOf { rows - it.coords.y }.toLong()
    }

    private fun moveTo(dir: Direction): Desert {
        var oldDesert: Desert
        var newDesert = this
        do {
            oldDesert = newDesert
            newDesert = oldDesert.move1To(dir)
        }  while (oldDesert != newDesert)
        return newDesert
    }

    private fun move1To(dir:Direction): Desert =
        rocks.map { cell ->
            if (!cell.isRoundRock()) cell
            else {
                val targetCoords = cell.coords.moveTo(dir)
                if (rocksByCoords.containsKey(targetCoords) || targetCoords.x < 0 || targetCoords.y < 0) {
                    cell
                } else {
                    cell.copy(coords = targetCoords)
                }
            }
        }.let { Desert(it) }

    private fun Cell.isRoundRock(): Boolean = this.value == 'O'
}
