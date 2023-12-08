package day07

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

    override fun runPart1(filePath: String): Long =
        Game(getEntitiesByLine(filePath, HandMapper(getTypeForHandPart1))).solutionP1()

    override fun runPart2(filePath: String): Long  =
        Game(getEntitiesByLine(filePath, HandMapper(getTypeForHandPart2))).solutionP2()
}

data class Game(private val hands: List<Hand>) {
    private fun solution(comparator: Comparator<Hand>): Long = hands
            .sortedWith(comparator)
            .mapIndexed { index, hand -> ((index + 1) * hand.bid).toLong() }
            .sum()
    fun solutionP1(): Long = solution(Hand.comparatorPart1)
    fun solutionP2(): Long = solution(Hand.comparatorPart2)
}

val getTypeForHandPart1 = { cards: List<Card> ->
    val groupedCards = cards.groupingBy { it }.eachCount()
    when(groupedCards.entries.size) {
        1 -> HandType.FIVE_OF_KIND
        2 -> if (groupedCards.values.max() == 4) HandType.FOUR_OF_KIND else HandType.FULL_HOUSE
        3 -> if (groupedCards.values.max() == 3) HandType.THREE_KIND else HandType.TWO_PAIR
        4 -> HandType.ONE_PAIR
        else -> HandType.HIGH_CARD
    }
}

val getTypeForHandPart2 = { cards: List<Card> ->
    if (Card.J in cards) {
        val cardWithoutJokers = cards.filter { it != Card.J }
        val jokersCount = 5 - cardWithoutJokers.size
        Card.entries
                .filter { it != Card.J }
                .map { getTypeForHandPart1(cardWithoutJokers + generateSequence { it }.take(jokersCount)) }
                .maxBy { it.strength }
    } else getTypeForHandPart1(cards)
}

data class Hand(
    private val cards: List<Card>,
    val bid: Int,
    val type: HandType
) {
    class HandComparator(val cardOrder: (Card) -> Int): Comparator<Hand> {

        override fun compare(a: Hand, b: Hand): Int = when {
            a.type.strength > b.type.strength -> 1
            a.type.strength < b.type.strength -> -1
            else -> compareWithCardsTo(a, b)
        }

        private fun compareWithCardsTo(a: Hand, b: Hand): Int {
            for (i in a.cards.indices) {
                if (cardOrder(a.cards[i]) > cardOrder(b.cards[i])) return 1
                else if (cardOrder(a.cards[i]) < cardOrder(b.cards[i])) return -1
            }
            return 0
        }
    }

    companion object {

        private val cardOrderP1 = { card: Card -> card.orderP1 }
        private val cardOrderP2 = { card: Card -> card.orderP2 }

        val comparatorPart1 = HandComparator(cardOrderP1)
        val comparatorPart2 = HandComparator(cardOrderP2)

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

class HandMapper(val handTypeCalculator: (List<Card>) -> HandType): LineMapper<Hand> {
    override fun map(line: String): Hand {
        val ls = line.split(" ")
        val bid = ls.last().toInt()
        val cards = ls.first().map { Card.fromChar(it) }
        return Hand(cards, bid, handTypeCalculator(cards))
    }
}

