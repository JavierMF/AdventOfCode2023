import kotlin.time.measureTimedValue

abstract class DayChallenge(
    private val day: String,
    private val part1SampleResult: Long,
    private val part2SampleResult: Long? = null,
) {

    abstract fun runPart1(filePath: String): Long

    abstract fun runPart2(filePath: String): Long

    private val problemFilePath = getFullFilePathFor("problem")

    fun run() {
        checkPart("1", "sample", part1SampleResult, this::runPart1)

        if (part2SampleResult != null) {
            checkPart("2", "sample2", part2SampleResult, this::runPart2)
        }
    }

    private fun checkPart(
        partName: String,
        sampleFileName: String,
        sampleSolution: Long,
        resolver: (String) -> Long
    ) {
        val partSampleFilePath = getFullFilePathFor(sampleFileName)
        val sampleResult = resolver(partSampleFilePath)
        check(sampleResult == sampleSolution
        ) { "$sampleResult is not the expected $sampleSolution" }

        val (partActualResult, elapsedPartResult) = measureTimedValue {
            resolver(problemFilePath)
        }
        println("Part $partName result: $partActualResult [$elapsedPartResult]")
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
