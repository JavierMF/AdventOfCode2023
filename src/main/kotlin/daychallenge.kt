import kotlin.time.measureTimedValue

abstract class DayChallenge(
    private val day: String,
    private val part1SampleResult: Long,
    private val part2SampleResult: Long? = null,
) {

    abstract fun runPart1(filePath: String): Long

    abstract fun runPart2(filePath: String): Long


    fun run() {
        val part1sampleFilePath = getFullFilePathFor("sample")
        val sampleResult = runPart1(part1sampleFilePath)
        check(sampleResult == part1SampleResult
        ) { "$sampleResult is not the expected $part1SampleResult" }

        val problemFilePath = getFullFilePathFor("problem")
        val (part1ActualResult, elapsedPart1Result) = measureTimedValue {
            runPart1(problemFilePath)
        }
        println("Part 1 result: $part1ActualResult [$elapsedPart1Result]")

        if (part2SampleResult != null) {
            val part2sampleFilePath = getFullFilePathFor("sample2")
            val sample2Result = runPart2(part2sampleFilePath)
            check(sample2Result == part2SampleResult
            ) { "$sample2Result is not the expected $part2SampleResult" }

            val (part2ActualResult, elapsedPart2Result) = measureTimedValue {
                runPart2(problemFilePath)
            }
            println("Part 2 result: $part2ActualResult [$elapsedPart2Result]")
        }
    }

    private fun getFullFilePathFor(filename: String): String {
        val fileRelativePath = "day$day/$filename.txt"
        val resource = this::class.java.classLoader.getResource(fileRelativePath)
        if (resource == null) {
            println("$fileRelativePath can not be found")
            System.exit(-1)
        }
        return resource!!.path
    }

}
