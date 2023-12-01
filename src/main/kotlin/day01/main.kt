package day01

import getNonBlankFileLines

fun main(args: Array<String>) {
    val result = getNonBlankFileLines(args).sumOf { line ->
        val numbers = line.emitNumbers()
        "${numbers.first()}${numbers.last()}".toInt()
    }

    println(result)
}

fun String.emitNumbers(): List<String> =
        indices.mapNotNull { index -> substring(index).toNumberIfMatches() }

fun CharSequence.toNumberIfMatches(): String? = numberValues
        .firstNotNullOfOrNull { (key, value) ->
            if (startsWith(key)) value else null
        }

val numberValues = mapOf(
        "1" to "1",
        "2" to "2",
        "3" to "3",
        "4" to "4",
        "5" to "5",
        "6" to "6",
        "7" to "7",
        "8" to "8",
        "9" to "9",
        "0" to "0",
        "one" to "1",
        "two" to "2",
        "three" to "3",
        "four" to "4",
        "five" to "5",
        "six" to "6",
        "seven" to "7",
        "eight" to "8",
        "nine" to "9",
        "zero" to "0")
