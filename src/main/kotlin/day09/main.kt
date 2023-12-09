package day09

// https://adventofcode.com/2023/day/9

import DayChallenge
import LineMapper
import getEntitiesByLine

fun main() = Day09Challenge.run()

object Day09Challenge: DayChallenge(
    day = "09",
    part1SampleResult = 114,
    part2SampleResult = null
) {

    override fun runPart1(filePath: String): Long =
        getEntitiesByLine(filePath, SensorHistoryMapper())
                .sumOf { it.predictNext() }

    override fun runPart2(filePath: String): Long  {
        TODO("Not implemented")
    }
}

class SensorHistoryMapper: LineMapper<SensorHistory> {
    override fun map(line: String) =
            SensorHistory(line.split(" ").map { it.toLong() })

}

data class SensorHistory(private val values: List<Long>) {

    private val listOfLists: MutableList<MutableList<Long>> = mutableListOf(values.toMutableList())

    fun predictNext(): Long {
        while(!lastOneAllZeros()) {
            listOfLists.add(generateListOfDiffs(listOfLists.last()))
        }
        listOfLists.reverse()
        for (index in 1 until listOfLists.size) {
            val newValue = listOfLists[index].last() + listOfLists[index-1].last()
            listOfLists[index].add(newValue)
        }

        return listOfLists.last().last()
    }

    private fun lastOneAllZeros() = listOfLists.last().all { it == 0L }

    private fun generateListOfDiffs(numbersList: List<Long>): MutableList<Long> =
            numbersList
                    .drop(1)
                    .mapIndexed { index, value ->
                        value - numbersList[index]
                    }.toMutableList()
}
