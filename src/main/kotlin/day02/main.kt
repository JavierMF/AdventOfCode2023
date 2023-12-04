package day02

// https://adventofcode.com/2023/day/2

import DayChallenge
import LineMapper
import getEntitiesByLine

fun main() = Day02Challenge().run()

class Day02Challenge: DayChallenge("02") {

    override fun runPart1(filePath: String): Int =
        getEntitiesByLine(filePath, GameMapper())
            .filter{ game -> game.maxCubes(red = 12, green = 13, blue = 14) }
            .sumOf { game -> game.id }

    override fun runPart2(filePath: String): Int =
        getEntitiesByLine(filePath, GameMapper())
            .sumOf { game -> game.power() }

    override fun part1SampleResult(): Int = 8
    override fun part2SampleResult(): Int = 2286

}

data class Game(
        val id: Int,
        val revelations: List<CubesShown>
) {
    fun maxCubes(red: Int, green: Int, blue: Int): Boolean =
        revelations.all { it.red <= red && it.green <= green && it.blue <= blue }

    fun power(): Int {
        val minRed = revelations.maxOf { it.red }
        val minBlue = revelations.maxOf { it.blue }
        val minGreen = revelations.maxOf { it.green }
        return minRed * minBlue * minGreen
    }
}

data class CubesShown(val red: Int, val green: Int, val blue: Int)

class GameMapper: LineMapper<Game> {
    // Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
    override fun map(line: String): Game {
        val lp = line.split(":")
        val id = lp.first().split(" ").last().toInt()
        val revelations = lp.last().split(";")
                .map { it.toRevelation()  }
        return Game(id, revelations)
    }

   //  3 green, 4 blue, 1 red
    private fun String.toRevelation(): CubesShown {
       val cubes = this.trim().split(",")
               .associate { // 3 green
                   val ls = it.trim().split(" ")
                   ls.last() to ls.first().toInt()
               }
       return CubesShown(
               red = cubes["red"] ?: 0,
               green = cubes["green"] ?: 0,
               blue = cubes["blue"] ?: 0
       )
   }
}