package day15

// https://adventofcode.com/2023/day/15

import DayChallenge
import getNonBlankFileLines

fun main() = Day15Challenge.run()

object Day15Challenge: DayChallenge(
    day = "15",
    part1SampleResult = 1320,
    part2SampleResult = 145
) {

    override fun runPart1(filePath: String): Long =
            InitSequence(getNonBlankFileLines(filePath).first().split(",")).part1()

    override fun runPart2(filePath: String): Long =
            InitSequence(getNonBlankFileLines(filePath).first().split(",")).part2()

}

data class InitSequence(val steps: List<String>) {

    fun part1(): Long = steps.sumOf { hash(it) }

    fun part2(): Long {
        val boxArray = BoxArray()
        steps.map { it.toInstruction() }.forEach {
            if (it.remove) boxArray.removeLensFromBox(it.box, it.label)
            else boxArray.addLensToBox(it.box, Lens(it.label, it.focalLength))
        }
        return boxArray.focusingPower()
    }

    private fun String.toInstruction(): Instruction {
        val remove = this.last() == '-'
        val label = if (remove) this.dropLast(1) else this.split("=").first()
        val power = if (remove) -1 else this.split("=").last().toLong()
        return Instruction(label, remove, power)
    }
}

class BoxArray {
    private val boxes: List<Box> = (0 until 255).map { Box(it) }

    fun focusingPower(): Long = boxes.sumOf { it.focusingPower() }

    fun addLensToBox(boxPos: Int, lens: Lens) {
        boxes[boxPos].addLens(lens)
    }

    fun removeLensFromBox(boxPos: Int, lensLabel: String) {
        boxes[boxPos].removeLens(lensLabel)
    }
}

data class Instruction(
        val label: String,
        val remove: Boolean = false,
        val focalLength: Long = -1
) {
    val box = hash(label).toInt()
}

data class Lens(val label: String, var focalLength: Long) {
    fun focusingPower(box: Int, posInBox: Int) = (box + 1) * posInBox * focalLength
}

data class Box(
        private val box: Int,
        private val lenses: MutableList<Lens> = mutableListOf()){

    fun addLens(lens: Lens) {
        lenses.firstOrNull { it.label == lens.label }?.let { it.focalLength = lens.focalLength }
                ?: lenses.add(lens)
    }

    fun removeLens(lensLabel: String) {
        lenses.removeIf { it.label == lensLabel }
    }

    fun focusingPower(): Long = lenses
            .mapIndexed { index, lens -> lens.focusingPower(box, index+1) }
            .sum()
}

private fun hash(string: String): Long {
    var value = 0L
    string.forEach { char ->
        value += char.code
        value *= 17
        value %= 256
    }
    return value
}
