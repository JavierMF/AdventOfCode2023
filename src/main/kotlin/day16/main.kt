package day16

// https://adventofcode.com/2023/day/16

import Cell
import Coords
import DayChallenge
import Direction
import getNonBlankFileLines
import toCells

fun main() = Day16Challenge.run()

object Day16Challenge: DayChallenge(
    day = "16",
    part1SampleResult = 46,
    part2SampleResult = 51
) {

    override fun runPart1(filePath: String): Long =
        MirrorsMap(getNonBlankFileLines(filePath).toCells()).part1()

    override fun runPart2(filePath: String): Long  =
        MirrorsMap(getNonBlankFileLines(filePath).toCells()).part2()
}

data class MirrorsMap(private val mirrors: List<Cell>) {

    private val mirrorsByCoords = mirrors.associateBy { it.coords }
    private val maxColIndex = mirrors.maxOf { it.coords.x }
    private val maxRowIndex = mirrors.maxOf { it.coords.y }

    fun part1(): Long = energizedWithInitialBeam(BeamHead(Direction.RIGHT, Coords(0, 0)))

    fun part2(): Long {
        val initialBeams = (0 .. maxColIndex)
                .flatMap { listOf(
                        BeamHead(Direction.DOWN, Coords(it, 0)),
                        BeamHead(Direction.UP, Coords(it, maxRowIndex)),
                        ) } +
                (0 .. maxRowIndex)
                        .flatMap { listOf(
                                BeamHead(Direction.RIGHT, Coords(0, it)),
                                BeamHead(Direction.LEFT, Coords(maxColIndex, it)),
                        ) }

        return initialBeams.maxOf { energizedWithInitialBeam(it) }
    }

    private fun energizedWithInitialBeam(initalBeamHead: BeamHead): Long {
        val visitedBeamHeads = mutableSetOf(initalBeamHead)
        var beamHeads = setOf(initalBeamHead)
        while(beamHeads.isNotEmpty()) {
            beamHeads = beamHeads.nextStep().filter { it !in visitedBeamHeads }.toSet()
            visitedBeamHeads.addAll(beamHeads)
            //printMap(beamHeads)
        }
        return visitedBeamHeads.map { it.coords }.toSet().size.toLong()
    }

    private fun printMap(beamHeads: Set<BeamHead>) {
        (0 .. maxRowIndex).forEach { y ->
            (0 .. maxColIndex).forEach { x ->
                val cellCoords = Coords(x, y)
                val beamHeadInCell = beamHeads.firstOrNull { it.coords == cellCoords }
                when {
                    beamHeadInCell != null -> print(beamHeadInCell.direction.char)
                    mirrorsByCoords.containsKey(cellCoords) -> print(mirrorsByCoords[cellCoords]!!.value)
                    else -> print('.')
                }
            }
            println()
        }
        println()
    }

    private fun Set<BeamHead>.nextStep(): Set<BeamHead> =
            this.flatMap { it.nextStep() }.toSet()

    private fun BeamHead.nextStep(): List<BeamHead> =
        when {
            !mirrorsByCoords.containsKey(this.coords) -> this.coords.buildBeamTo(this.direction)
            else -> mirrorsByCoords[this.coords]!!.applyBeam(this)
        }

    private fun Cell.applyBeam(beam: BeamHead): List<BeamHead> =
        when (this.value) {
            '\\'       -> beam.applyLeftRightMirror(this.coords)
            '/'        -> beam.applyRightLeftMirror(this.coords)
            '-'        -> beam.applyHorizontalSplitter(this.coords)
            '|'        -> beam.applyVerticalSplitter(this.coords)
            else -> emptyList<BeamHead>().also { println("What it is this? $this") }
        }

    private fun BeamHead.applyLeftRightMirror(mirrorCoords: Coords): List<BeamHead> =
            when (this.direction) {
                Direction.RIGHT -> mirrorCoords.buildBeamTo(Direction.DOWN)
                Direction.DOWN -> mirrorCoords.buildBeamTo(Direction.RIGHT)
                Direction.LEFT -> mirrorCoords.buildBeamTo(Direction.UP)
                Direction.UP -> mirrorCoords.buildBeamTo(Direction.LEFT)
            }

    private fun BeamHead.applyRightLeftMirror(mirrorCoords: Coords): List<BeamHead> =
            when (this.direction) {
                Direction.RIGHT -> mirrorCoords.buildBeamTo(Direction.UP)
                Direction.DOWN -> mirrorCoords.buildBeamTo(Direction.LEFT)
                Direction.LEFT -> mirrorCoords.buildBeamTo(Direction.DOWN)
                Direction.UP -> mirrorCoords.buildBeamTo(Direction.RIGHT)
            }

    private fun BeamHead.applyHorizontalSplitter(mirrorCoords: Coords): List<BeamHead> =
            when (this.direction) {
                Direction.RIGHT -> mirrorCoords.buildBeamTo(Direction.RIGHT)
                Direction.DOWN -> mirrorCoords.buildBeamTo(Direction.RIGHT) + mirrorCoords.buildBeamTo(Direction.LEFT)
                Direction.LEFT -> mirrorCoords.buildBeamTo(Direction.LEFT)
                Direction.UP -> mirrorCoords.buildBeamTo(Direction.RIGHT) + mirrorCoords.buildBeamTo(Direction.LEFT)
            }

    private fun BeamHead.applyVerticalSplitter(mirrorCoords: Coords): List<BeamHead> =
            when (this.direction) {
                Direction.RIGHT -> mirrorCoords.buildBeamTo(Direction.UP) + mirrorCoords.buildBeamTo(Direction.DOWN)
                Direction.DOWN -> mirrorCoords.buildBeamTo(Direction.DOWN)
                Direction.LEFT -> mirrorCoords.buildBeamTo(Direction.UP) + mirrorCoords.buildBeamTo(Direction.DOWN)
                Direction.UP -> mirrorCoords.buildBeamTo(Direction.UP)
            }

    private fun Coords.buildBeamTo(direction: Direction): List<BeamHead> =
            this.moveTo(direction).let { newCoords ->
                if (newCoords.outOfBounds()) emptyList()
                else listOf(BeamHead(direction, newCoords))
            }

    private fun Coords.outOfBounds() =
            x< 0 || x > maxColIndex ||
                    y < 0 || y > maxRowIndex

}

data class BeamHead(val direction: Direction, val coords: Coords)
