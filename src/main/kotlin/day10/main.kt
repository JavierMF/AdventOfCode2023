package day10

// https://adventofcode.com/2023/day/10

import Coords
import DayChallenge
import Direction
import getNonBlankFileLines

fun main() = Day10Challenge.run()

object Day10Challenge: DayChallenge(
    day = "10",
    8,
    part2SampleResult = null
) {

    override fun runPart1(filePath: String): Long =
        PipeMap(getNonBlankFileLines(filePath).toCellContent()).part1()

    override fun runPart2(filePath: String): Long  {
        TODO("Not implemented")
    }
}

class PipeMap(cells: List<CellContent>) {
    private val cellsByCoords = cells.associateBy { it.coords }
    private val startingCoords = cells.first { it.startingPoint }.coords

    fun part1(): Long {
        val cycle = startingCoords.verticalNeighbours()
                .firstNotNullOf { findCycle(startingCoords, it) }
        return cycle.size/2L
    }

    private fun findCycle(startingCoords: Coords, dest: Coords): Set<Coords>? {
        val visited = mutableSetOf(startingCoords, dest)
        var current = cellsByCoords[dest] ?: return null
        if (!current.connectedTo(startingCoords)) return null
        while (!current.startingPoint) {
            val next = current.nextNotIn(visited) ?: return null
            visited.add(next.coords)
            current = next
        }
        return visited
    }

    private fun CellContent.nextNotIn(visited: MutableSet<Coords>): CellContent? =
        coordsConnected()
                .mapNotNull { cellsByCoords[it] }
                .firstOrNull { it.coords !in visited || it.startingPoint && visited.size > 2 }
}

data class CellContent(
        val coords: Coords,
        val pipeType: PipeType? = null,
        val startingPoint: Boolean = false
) {
    fun coordsConnected(): List<Coords> = pipeType?.connections?.map { coords.moveTo(it) } ?: emptyList()

    fun connectedTo(other: CellContent) =  other.coords in this.coordsConnected()
    fun connectedTo(other: Coords) =  other in this.coordsConnected()
}

enum class PipeType(private val character: Char, val connections: List<Direction>) {
    NS('|', listOf(Direction.UP, Direction.DOWN)),
    EW('-', listOf(Direction.RIGHT, Direction.LEFT)),
    NE('L', listOf(Direction.UP, Direction.RIGHT)),
    NW('J', listOf(Direction.UP, Direction.LEFT)),
    SW('7', listOf(Direction.DOWN, Direction.LEFT)),
    SE('F', listOf(Direction.DOWN, Direction.RIGHT));

    companion object {
        fun of(aChar: Char): PipeType? = PipeType.entries.firstOrNull { it.character == aChar }
    }
}

fun List<String>.toCellContent() =  this.flatMapIndexed { rowIndex, line ->
    line.mapIndexedNotNull { colIndex, char ->
        when (char) {
            '.' -> null
            'S' -> CellContent(Coords(colIndex, rowIndex), startingPoint = true)
            else -> CellContent(Coords(colIndex, rowIndex), pipeType = PipeType.of(char))
        }
    }
}
