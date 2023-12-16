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
                    ?.let { listOf(it) } ?: emptyList()
            else -> mirrorsByCoords[this.coords]!!.applyBeam(this)
        }

    private fun Cell.applyBeam(beam: BeamHead): List<BeamHead> =
        when (this.value) {
            '\\'       -> beam.applyToMirror(Mirror.LeftRightMirror)
            '/'        -> beam.applyToMirror(Mirror.RightLeftMirror)
            '-'        -> beam.applyToMirror(Mirror.HorizontalSplitter)
            '|'        -> beam.applyToMirror(Mirror.VerticalSplitter)
            else -> emptyList<BeamHead>().also { println("What it is this? $this") }
        }

    private fun BeamHead.applyToMirror(mirror: Mirror): List<BeamHead> =
            mirror.generatesTo(this.direction).mapNotNull { this.coords.buildBeamTo(it) }

    private fun Coords.buildBeamTo(direction: Direction): BeamHead? =
            this.moveTo(direction).let { newCoords ->
                if (newCoords.outOfBounds()) null
                else BeamHead(direction, newCoords)
            }

    private fun Coords.outOfBounds() =
            x< 0 || x > maxColIndex ||
                    y < 0 || y > maxRowIndex

}

enum class Mirror(private val mapping: Map<Direction, Set<Direction>>) {
    // '\'
    LeftRightMirror(mapOf(
            Direction.RIGHT to setOf(Direction.DOWN),
            Direction.DOWN to setOf(Direction.RIGHT),
            Direction.LEFT to setOf(Direction.UP),
            Direction.UP to setOf(Direction.LEFT),
    )),
    // '/'
    RightLeftMirror(mapOf(
            Direction.RIGHT to setOf(Direction.UP),
            Direction.DOWN to setOf(Direction.LEFT),
            Direction.LEFT to setOf(Direction.DOWN),
            Direction.UP to setOf(Direction.RIGHT),
    )),
    // '-'
    HorizontalSplitter(mapOf(
            Direction.RIGHT to setOf(Direction.RIGHT),
            Direction.DOWN to setOf(Direction.RIGHT, Direction.LEFT),
            Direction.LEFT to setOf(Direction.LEFT),
            Direction.UP to setOf(Direction.RIGHT, Direction.LEFT),
    )),
    // '|'
    VerticalSplitter(mapOf(
            Direction.RIGHT to setOf(Direction.UP, Direction.DOWN),
            Direction.DOWN to setOf(Direction.DOWN),
            Direction.LEFT to setOf(Direction.UP, Direction.DOWN),
            Direction.UP to setOf(Direction.UP),
    ));

    fun generatesTo(dir: Direction): Set<Direction> = mapping[dir]!!
}

data class BeamHead(val direction: Direction, val coords: Coords)
