import java.io.File // ktlint-disable filename
import kotlin.system.exitProcess

fun getFileFromArgs(args: Array<String>): File {
    if (args.isEmpty()) {
        println("Input file path required")
        exitProcess(-1)
    }
    return getFileFromFilePath(args.first())
}

fun getFileFromFilePath(filePath: String): File {
    val file = File(filePath)
    if (!file.exists()) {
        println("Input file does not exist")
        exitProcess(-1)
    }
    return file
}

fun getNonBlankFileLines(args: Array<String>) =
    getFileFromArgs(args)
        .readLines()
        .filter { it.isNotBlank() }

fun interface LineMapper<T> {
    fun map(line: String): T
}

fun <T> getEntitiesByLine(args: Array<String>, mapper: LineMapper<T>): List<T> =
    getNonBlankFileLines(args).map { mapper.map(it) }

data class Coords(val x: Int, val y: Int) {

    fun neighbours(maxX: Int, maxY: Int): Set<Coords> = range(x, maxX)
        .flatMap { posX ->
            range(y, maxY).map { posY -> Coords(posX, posY) }
                .filter { it != this }
        }.toSet()

    private fun range(i: Int, max: Int): Set<Int> = (i - 1..i + 1)
        .filter { it in 0..max }.toSet()
}

data class Grid(
    val coords: Set<Coords>,
)
