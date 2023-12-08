package day08

// https://adventofcode.com/2023/day/8

import DayChallenge
import getNonBlankFileLines
import printIt

fun main() = Day08Challenge.run()

object Day08Challenge: DayChallenge(
    day = "08",
    part1SampleResult = 6,
    part2SampleResult = 6
) {

    override fun runPart1(filePath: String): Long =
        buildNavigationMap(getNonBlankFileLines(filePath)).navigateP1()

    override fun runPart2(filePath: String): Long  =
        buildNavigationMap(getNonBlankFileLines(filePath)).navigateP2()
}

data class NavigationMap(
    val instructions: List<Char>,
    val nodes: List<Node>
) {
    private val nodesById = nodes.associateBy { it.id }
    private val instructionsSize = instructions.size

    private fun instructionPos(step: Long) = (step % instructionsSize).toInt()

    private fun navigate(startNode: Node, endingCondition: (Node) -> Boolean): Long {
        var steps = 0L
        var currentNode = startNode
        while (!endingCondition(currentNode)) {
            val direction = instructions[instructionPos(steps)]
            val nextNodeId = currentNode.takeDirection(direction)
            currentNode = nodesById[nextNodeId]!!
            steps += 1
        }

        return steps
    }

    fun navigateP1(): Long = navigate(nodesById["AAA"]!!) { node -> node.id == "ZZZ" }


    fun navigateP2(): Long {
        val steps = nodes
                .filter { it.id.endsWith("A") }
                .map { navigate(it) { node -> node.id.endsWith("Z") } }
        return findLCMOfListOfNumbers(steps)
    }

    private fun findLCM(a: Long, b: Long): Long {
        val larger = if (a > b) a else b
        val maxLcm = a * b
        var lcm = larger
        while (lcm <= maxLcm) {
            if (lcm % a == 0L && lcm % b == 0L) {
                return lcm
            }
            lcm += larger
        }
        return maxLcm
    }

    private fun findLCMOfListOfNumbers(numbers: List<Long>): Long =
        numbers.foldRight(numbers.first()){ acc, value -> findLCM(acc, value) }

}

data class Node(
    val id: String,
    val left: String,
    val right: String
) {
    fun takeDirection(c: Char): String = if (c == 'L') left else right
}

fun buildNavigationMap(lines: List<String>): NavigationMap {
    val instructions = lines.first().toCharArray().toList()
    val nodes = lines.drop(1).map { line ->
        val ls = line.split(" = ")
        val id = ls.first()
        val ls2 = ls.last().removePrefix("(").removeSuffix(")").split(", ")
        val left = ls2.first()
        val right = ls2.last()
        Node(id, left, right)
    }
    return NavigationMap(instructions, nodes)
}
