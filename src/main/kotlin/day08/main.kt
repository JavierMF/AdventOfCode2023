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

    fun navigateP1(): Long {
        var steps = 0L
        var currentNode = nodesById["AAA"]!!
        while (currentNode.id != "ZZZ") {
            val direction = instructions[instructionPos(steps)]
            val nextNodeId = currentNode.takeDirection(direction)
            currentNode = nodesById[nextNodeId]!!
            steps += 1
        }

        return steps
    }

    fun navigateP2(): Long {
        var steps = 0L
        var currentNodes = nodesById.keys
                .filter { it.endsWith("A") }
                .map { nodesById[it]!! }
                .printIt()
        while (!currentNodes.allEndWithZ()) {
            val direction = instructions[instructionPos(steps)]
            currentNodes = currentNodes.map { it.takeDirection(direction) }
                    .map { nodesById[it]!! }
            steps += 1
            /*if ((steps % 1000000L) == 0L) {
                println("Step $steps - $currentNodes")
            }*/
        }

        return steps
    }

    private fun List<Node>.allEndWithZ() = this.all { it.id.endsWith("Z") }

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
