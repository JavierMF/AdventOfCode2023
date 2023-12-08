import kotlin.time.measureTimedValue

abstract class DayChallenge(
        private val day: String,
        private val part1SampleResult: Long,
        private val part2SampleResult: Long? = null,
        private val skipPart01: Boolean = false
) {

    abstract fun runPart1(filePath: String): Long

    abstract fun runPart2(filePath: String): Long

    private val problemFilePath = getFullFilePathFor("problem")

    fun run() {
        if (!skipPart01) {
            checkPart("1", "sample", part1SampleResult, this::runPart1)
        } else {
            println("Skipping part 1")
        }

        if (part2SampleResult != null) {
            checkPart("2", "sample2", part2SampleResult, this::runPart2)
        } else {
            println("No sample data for part 2")
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
        println("Sample for part $partName is OK")

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
