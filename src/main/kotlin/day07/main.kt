package template

// https://adventofcode.com/2023/day/7

import DayChallenge
import LineMapper
import getEntitiesByLine

fun main() = Day07Challenge.run()

object Day07Challenge: DayChallenge(
    day = "07",
    part1SampleResult = 6440,
    part2SampleResult = null
) {

    override fun runPart1(filePath: String): Long {
        val game = Game(getEntitiesByLine(filePath, HandMapper()))
        return game.sortedHands().mapIndexed { index, hand -> ((index + 1) * hand.bid).toLong() }.sum()
    }

    override fun runPart2(filePath: String): Long  {
        TODO("Not implemented")
    }
}



data class Game(private val hands: List<Hand>) {
    fun sortedHands(): List<Hand> = hands.sorted()
}

data class Hand(
    private val cards: List<Card>,
    val bid: Int
): Comparable<Hand> {
    val type: HandType

    init {
        val groupedCards = cards.groupingBy { it }.eachCount()
        type = when(groupedCards.entries.size) {
            1 -> HandType.FIVE_OF_KIND
            2 -> if (groupedCards.values.max() == 4) HandType.FOUR_OF_KIND else HandType.FULL_HOUSE
            3 -> if (groupedCards.values.max() == 3) HandType.THREE_KIND else HandType.TWO_PAIR
            4 -> HandType.ONE_PAIR
            else -> HandType.HIGH_CARD
        }
        println("$this - $type")
    }

    override fun compareTo(other: Hand): Int =
        when {
            this.type.strength > other.type.strength -> 1
            this.type.strength < other.type.strength -> -1
            else -> compareWithCardsTo(other)
        }

    private fun compareWithCardsTo(other: Hand): Int {
        for (i in cards.indices) {
            if (cards[i].order > other.cards[i].order) return 1
            else if (cards[i].order < other.cards[i].order) return -1
        }
        return 0
    }
}

enum class HandType(val strength: Int) {
    FIVE_OF_KIND(7),
    FOUR_OF_KIND(6),
    FULL_HOUSE(5),
    THREE_KIND(4),
    TWO_PAIR(3),
    ONE_PAIR(2),
    HIGH_CARD(1),
}

enum class Card(val order: Int) {
    A(13),
    K(12),
    Q(11),
    J(10),
    T(9),
    _9(8),
    _8(7),
    _7(6),
    _6(5),
    _5(4),
    _4(3),
    _3(2),
    _2(1);

    companion object {
        private val cardsByName: Map<Char, Card> = Card.values().associateBy { it.name.removePrefix("_").first() }
        fun fromChar(c: Char): Card = cardsByName[c] ?: throw RuntimeException("Unknown card")
    }
}

class HandMapper: LineMapper<Hand> {
    override fun map(line: String): Hand {
        val ls = line.split(" ")
        val bid = ls.last().toInt()
        val cards = ls.first().map { Card.fromChar(it) }
        return Hand(cards, bid)
    }
}
