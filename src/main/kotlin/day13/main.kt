package day13

// https://adventofcode.com/2023/day/13

import DayChallenge
import getFileFromFilePath

fun main() = Day13Challenge.run()

object Day13Challenge: DayChallenge(
    day = "13",
    part1SampleResult = 405,
    part2SampleResult = null
) {

    override fun runPart1(filePath: String): Long =
            patternReader(filePath).sumOf { it.result1() }


    override fun runPart2(filePath: String): Long  {
        TODO("Not implemented")
    }
}

data class Pattern(private val lines: List<String>) {

    fun result1(): Long =
            (findMatch(lines)?.let { it * 100 } ?: findMatch(transpose(lines)))!!.toLong()

    private fun findMatch(theLines: List<String>): Int? {
        (0 until theLines.size-1).forEach {
            if (isMatch(theLines, it)) return it + 1
        }
        return null
    }

    private fun isMatch(theLines: List<String>, index: Int): Boolean {
        var i = index
        var j = index + 1
        while (i>=0 && j < theLines.size && theLines[i] == theLines[j]) {
            i -= 1
            j += 1
            println("$i - $j")
        }

        return i < 0 || j == theLines.size
    }

    private fun transpose(theLines: List<String>): List<String> {
        val newLines = mutableListOf<String>()
        repeat(theLines.first().length) { newLines.add("") }
        theLines.forEach { line ->
            line.forEachIndexed { x, char -> newLines[x] = newLines[x] + char }
        }
        return newLines
    }
}

fun patternReader(filePath: String): List<Pattern> {
    val patterns = mutableListOf<Pattern>()
    var patternLines = mutableListOf<String>()
    getFileFromFilePath(filePath)
            .readLines()
            .forEach { line ->
                if (line.isEmpty()) {
                    patterns.add(Pattern(patternLines))
                    patternLines = mutableListOf()
                } else patternLines.add(line)
            }

    if (patternLines.isNotEmpty()) patterns.add(Pattern((patternLines)))
    return patterns
}
