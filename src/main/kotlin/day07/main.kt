package template

// https://adventofcode.com/2023/day/7

import DayChallenge
import LineMapper
import getEntitiesByLine

fun main() = Day07Challenge.run()

object Day07Challenge: DayChallenge(
    day = "07",
    part1SampleResult = 6440,
    part2SampleResult = 5905
) {

    override fun runPart1(filePath: String): Long {
        val game = Game(getEntitiesByLine(filePath, HandMapperP1()))
        return game.sortedHandsPart1().mapIndexed { index, hand -> ((index + 1) * hand.bid).toLong() }.sum()
    }

    override fun runPart2(filePath: String): Long  {
        val game = Game(getEntitiesByLine(filePath, HandMapperP2()))
        return game.sortedHandsPart2().mapIndexed { index, hand -> ((index + 1) * hand.bid).toLong() }.sum()
    }
}

data class Game(private val hands: List<Hand>) {
    fun sortedHandsPart1(): List<Hand> = hands.sortedWith(Hand.comparatorPart1)
    fun sortedHandsPart2(): List<Hand> = hands.sortedWith(Hand.comparatorPart2)
}

fun getTypeForHandPart1(cards: List<Card>): HandType {
    val groupedCards = cards.groupingBy { it }.eachCount()
    return when(groupedCards.entries.size) {
        1 -> HandType.FIVE_OF_KIND
        2 -> if (groupedCards.values.max() == 4) HandType.FOUR_OF_KIND else HandType.FULL_HOUSE
        3 -> if (groupedCards.values.max() == 3) HandType.THREE_KIND else HandType.TWO_PAIR
        4 -> HandType.ONE_PAIR
        else -> HandType.HIGH_CARD
    }
}

fun getTypeForHandPart2(cards: List<Card>): HandType {
    val groupedCards = cards.groupingBy { it }.eachCount()
    return when(groupedCards.entries.size) {
        1 -> HandType.FIVE_OF_KIND
        2 -> if (groupedCards.values.max() == 4) HandType.FOUR_OF_KIND else HandType.FULL_HOUSE
        3 -> if (groupedCards.values.max() == 3) HandType.THREE_KIND else HandType.TWO_PAIR
        4 -> HandType.ONE_PAIR
        else -> HandType.HIGH_CARD
    }
}

data class Hand(
    private val cards: List<Card>,
    val bid: Int,
    val type: HandType
) {

    companion object {
        val comparatorPart1 = object : Comparator<Hand> {

            override fun compare(a: Hand, b: Hand): Int = when {
                a.type.strength > b.type.strength -> 1
                a.type.strength < b.type.strength -> -1
                else -> compareWithCardsTo(a, b)
            }

            private fun compareWithCardsTo(a: Hand, b: Hand): Int {
                for (i in a.cards.indices) {
                    if (a.cards[i].orderP1 > b.cards[i].orderP1) return 1
                    else if (a.cards[i].orderP1 < b.cards[i].orderP1) return -1
                }
                return 0
            }
        }

        val comparatorPart2 = object : Comparator<Hand> {

            override fun compare(a: Hand, b: Hand): Int = when {
                a.type.strength > b.type.strength -> 1
                a.type.strength < b.type.strength -> -1
                else -> compareWithCardsTo(a, b)
            }

            private fun compareWithCardsTo(a: Hand, b: Hand): Int {
                for (i in a.cards.indices) {
                    if (a.cards[i].orderP2 > b.cards[i].orderP2) return 1
                    else if (a.cards[i].orderP2 < b.cards[i].orderP2) return -1
                }
                return 0
            }
        }
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

enum class Card(val orderP1: Int, val orderP2: Int) {
    A(13, 13),
    K(12, 12),
    Q(11, 11),
    J(10, 0),
    T(9,  9),
    _9(8, 8),
    _8(7, 7),
    _7(6, 6),
    _6(5, 5),
    _5(4, 4),
    _4(3, 3),
    _3(2, 2),
    _2(1, 1);

    companion object {
        private val cardsByName: Map<Char, Card> = Card.values().associateBy { it.name.removePrefix("_").first() }
        fun fromChar(c: Char): Card = cardsByName[c] ?: throw RuntimeException("Unknown card")
    }
}

class HandMapperP1: LineMapper<Hand> {
    override fun map(line: String): Hand {
        val ls = line.split(" ")
        val bid = ls.last().toInt()
        val cards = ls.first().map { Card.fromChar(it) }
        return Hand(cards, bid, getTypeForHandPart1(cards))
    }
}

class HandMapperP2: LineMapper<Hand> {
    override fun map(line: String): Hand {
        val ls = line.split(" ")
        val bid = ls.last().toInt()
        val cards = ls.first().map { Card.fromChar(it) }
        return Hand(cards, bid, getTypeForHandPart2(cards))
    }
}
