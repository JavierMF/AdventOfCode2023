package day04

// https://adventofcode.com/2023/day/4

import DayChallenge
import LineMapper
import getEntitiesByLine
import kotlin.math.pow

fun main() = Day04Challenge.run()

object Day04Challenge: DayChallenge(
    day = "04",
    part1SampleResult = 13,
    part2SampleResult = 30
) {

    override fun runPart1(filePath: String): Long =
        getEntitiesByLine(filePath, CardMapper()).sumOf { it.points }.toLong()

    override fun runPart2(filePath: String): Long  {
        val cards = getEntitiesByLine(filePath, CardMapper())
        val cardsById = cards.associateBy { it.id }
        return cards.sumOf { card -> getCardsGetBy(card, cardsById) }.toLong()
    }

    private fun getCardsGetBy(card: Card, cardsById: Map<Int, Card>): Int =
        1 + ((card.id + 1)..(card.id + card.winingCards)).sumOf {
            getCardsGetBy(cardsById[it]!!, cardsById)
        }
}

class CardMapper: LineMapper<Card> {
    // Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
    override fun map(line: String): Card {
        val ls0 = line.split(": ")
        val id = ls0.first().split(" ").last().toInt()

        val ls = ls0.last().split(" | ")
        val wining = ls.first().numbersStringToList()
        val numbers = ls.last().numbersStringToList()
        return Card(id, wining.toSet(), numbers.toSet())
    }

    private fun String.numbersStringToList() =
        trim()
           .split(" ")
           .filterNot { it.isEmpty() }
           .map { it.trim().toInt() }
}

data class Card(
    val id: Int,
    private val wining: Set<Int>,
    private val numbers: Set<Int>
) {
    val winingCards: Int = numbers.intersect(wining).size
    val points: Int = when {
            winingCards > 1 ->  2.0.pow(winingCards - 1).toInt()
            else -> winingCards
        }
}
