package day02

// https://adventofcode.com/2023/day/2

import getNonBlankFileLines

fun main(args: Array<String>) {
    val games = getNonBlankFileLines(args)
            .map { line -> GameBuilder(line).build() }

    val result = games
            .filter{ game -> game.maxCubes(red = 12, green = 13, blue = 14) }
            .sumOf { game -> game.id }
    println(result)

    val result2 = games.sumOf { game -> game.power() }
    println(result2)
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

data class GameBuilder(private val line: String) {
    // Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
    fun build(): Game {
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
