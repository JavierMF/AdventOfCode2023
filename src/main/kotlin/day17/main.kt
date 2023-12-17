package day17

// https://adventofcode.com/2023/day/x

import Cell
import Coords
import DayChallenge
import Direction
import getNonBlankFileLines
import toCells

fun main() = Day17Challenge.run()

object Day17Challenge: DayChallenge(
    day = "17",
    part1SampleResult = -1,
    part2SampleResult = null
) {

    override fun runPart1(filePath: String): Long =
            HeatLossMap(getNonBlankFileLines(filePath).toCells()).part1()

    override fun runPart2(filePath: String): Long  {
        TODO("Not implemented")
    }
}

class HeatLossMap(districtCells: List<Cell>) {
    private val districtsByCoords = districtCells.associate { it.coords to District(it.value.digitToInt()) }
    private val maxCols = districtCells.maxOf { it.coords.x }
    private val maxRows = districtCells.maxOf { it.coords.y }

    fun part1(): Long {
        val initialCoord = Coords(0,0)
        val targetCoords = Coords(maxCols, maxRows)
        var openRoutes = setOf(
                Route(initialCoord, Direction.RIGHT),
                Route(initialCoord, Direction.DOWN)
        )
        districtsByCoords[initialCoord]?.let { district ->
            district.updateBestIfNeeded(Direction.RIGHT)
            district.updateBestIfNeeded(Direction.DOWN)
        }
        while(!openRoutes.hasRouteWithCoords(targetCoords)) {
            openRoutes = openRoutes.nextStep()
        }
        return openRoutes.first { it.position == targetCoords }.heatLoss
    }

    private fun Set<Route>.nextStep(): Set<Route> = this.flatMap { it.nextStep() }.toSet()

    private fun Route.nextStep(): Set<Route> {
        val candidateCoords = this.nextStepCoords().filter { !it.outbounds() }
    }


    private fun Set<Route>.hasRouteWithCoords(target: Coords): Boolean =
            this.any { it.position == target }

    private fun Coords.outbounds() = this.x < 0 || this.x > maxCols
            || this.y < 0 || this.y > maxRows
}

data class Route(
        val position: Coords,
        val direction: Direction,
        val heatLoss: Long = 0,
        val straightSteps: Int = 0
) {
   fun nextStepCoords(): Set<Coords> {
       val directions = Direction.entries
               .filter { it != direction.opposite() }
               .filter { straightSteps >= 3 && it != direction }
       return directions.map { position.moveTo(it) }.toSet()
   }
}

data class District(
        val heatLoss: Int,
        val bestRoute: MutableMap<Direction, Long> = mutableMapOf()
) {
    fun updateBestIfNeeded(direction: Direction, candidate: Long = 0) {
        val currentValue = bestRoute[direction] ?: Long.MAX_VALUE
        if (candidate < currentValue) bestRoute[direction] = candidate
    }
}
