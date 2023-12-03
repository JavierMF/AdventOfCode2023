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
    getNonBlankFileLines(args).map(mapper::map)

