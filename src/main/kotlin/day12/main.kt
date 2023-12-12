package day12

// https://adventofcode.com/2023/day/12

import DayChallenge
import LineMapper
import getEntitiesByLine

fun main() = Day12Challenge.run()

object Day12Challenge: DayChallenge(
    day = "12",
    part1SampleResult = 21,
    part2SampleResult = null
) {

    override fun runPart1(filePath: String): Long =
            SpringList(getEntitiesByLine(filePath, SpringStateMapper())).part1()

    override fun runPart2(filePath: String): Long  {
        TODO("Not implemented")
    }
}

data class SpringList(val springsStates: List<SpringState>) {
    fun part1(): Long = springsStates.sumOf { it.differentSolutions() }
}

data class SpringState(
        val damagedFlags: String,
        val damagedGroups: List<Int>
) {
    fun differentSolutions(): Long =
            generateVariations(damagedFlags)
                    .count { it.damagedGroups() == damagedGroups }
                    .toLong()

    private fun generateVariations(input: String): List<String> {
        val charVariations = input.first().options()
        val rest = input.substring(1)
        return if (rest.isNotEmpty()) {
            val restVariations = generateVariations(rest)
            charVariations.flatMap { aChar -> aChar.withVariations(restVariations) }
        } else charVariations.map { it.toString() }
    }

    private fun Char.options(): List<Char> =
            if (this == '?') listOf('.', '#') else listOf(this)

    private fun Char.withVariations(variations: List<String>): List<String> =
            variations.map { this + it }


    private fun String.damagedGroups() = this.split(".")
            .map { it.count() }
            .filter { it > 0 }
}

class SpringStateMapper: LineMapper<SpringState> {
    // .??..??...?##. 1,1,3
    override fun map(line: String): SpringState {
        val ls = line.split(" ")
        return SpringState(ls.first(), ls.last().split(",").map { it.toInt() })
    }
}
