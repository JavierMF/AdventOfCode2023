package day04

// https://adventofcode.com/2023/day/4

import DayChallenge
import LineMapper
import getEntitiesByLine

fun main() = Day04Challenge().run()

class Day04Challenge: DayChallenge("04") {

    override fun runPart1(filePath: String): Int =
        getEntitiesByLine(filePath, CardMapper()).sumOf { it.points }

    override fun runPart2(filePath: String): Int  {
        val cards = getEntitiesByLine(filePath, CardMapper())
        val cardsById = cards.associateBy { it.id }
        return cards.flatMap { card -> getCardsGetBy(card, cardsById) }.count()
    }

    private fun getCardsGetBy(card: Card, cardsById: Map<Int, Card>): List<Int> =
        listOf(card.id) + ((card.id + 1)..(card.id + card.winingCards)).flatMap {
            getCardsGetBy(cardsById[it]!!, cardsById)
        }

    override fun part1SampleResult(): Int = 13
    override fun part2SampleResult(): Int = 30
}

class CardMapper: LineMapper<Card> {
    // Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
    override fun map(line: String): Card {
        val ls0 = line.split(": ")
        val id = ls0.first().split(" ").last().toInt()
        val ls = ls0.last().split(" | ")
        val wining = ls.first().trim().split(" ").filterNot { it.isEmpty() }.map { it.trim().toInt() }
        val numbers = ls.last().trim().split(" ").filterNot { it.isEmpty() }.map { it.trim().toInt() }
        return Card(id, wining.toSet(), numbers.toSet())
    }
}

data class Card(
    val id: Int,
    private val wining: Set<Int>,
    private val numbers: Set<Int>
) {
    val winingCards: Int = numbers.intersect(wining).size
    val points: Int = when{
            winingCards == 0 -> 0
            winingCards == 1 -> 1
            winingCards > 1 ->  Math.pow(2.0, (winingCards-1).toDouble()).toInt()
            else -> throw RuntimeException("This is not possible")
        }
}
