package day06

// https://adventofcode.com/2023/day/6

import DayChallenge
import getNonBlankFileLines

fun main() = Day06Challenge.run()

object Day06Challenge: DayChallenge(
    day = "06",
    part1SampleResult = 288,
    part2SampleResult = 71503
) {

    override fun runPart1(filePath: String): Long =
        parseRacesPart1(getNonBlankFileLines(filePath))
            .map { it.winingOptions() }
            .reduce { acc, elem -> acc* elem}

    override fun runPart2(filePath: String): Long  =
        parseReacePart2(getNonBlankFileLines(filePath)).winingOptions()
}

fun parseRacesPart1(lines: List<String>): List<RaceDescription> {
    val timeList = lines.first().toNumbersList()
    val distanceList = lines.last().toNumbersList()
    return timeList.mapIndexed { index, time ->  RaceDescription(time, distanceList[index])}
}

fun parseReacePart2(lines: List<String>): RaceDescription {
    val time = lines.first().toSingleNumber()
    val distance = lines.last().toSingleNumber()
    return RaceDescription(time, distance)
}

fun String.toSingleNumber() = this
    .split(":")
    .last()
    .replace(" ","")
    .toLong()

fun String.toNumbersList() = this
    .split(":")
    .last()
    .trim()
    .split(" ")
    .filterNot { it.isEmpty() }
    .map { it.trim().toLong() }

data class RaceDescription(
    val time: Long,
    val distance: Long,
) {
    fun winingOptions(): Long {
        var wining = 0L
        for (timePushing in 1 until distance) {
            if (beatsIfPushes(timePushing)) {
                wining += 1
            } else if (wining > 0) { // We are pushing for too long, we can stop searching
                break
            }
        }
        return wining
    }

    private fun beatsIfPushes(timePushing: Long): Boolean =
        ((time - timePushing) * timePushing) > distance
}

