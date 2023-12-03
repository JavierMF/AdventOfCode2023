
abstract class DayChallenge(private val day: String) {

    abstract fun runPart1(filePath: String): Int
    abstract fun part1SampleResult(): Int

    open fun part2SampleResult(): Int? = null
    open fun runPart2(filePath: String): Int = -1


    fun run() {
        val part1sampleFilePath = getFullFilePathFor("sample")
        val sampleResult = runPart1(part1sampleFilePath)
        check(sampleResult == part1SampleResult()
        ) { "$sampleResult is not the expected ${part1SampleResult()}" }

        val problemFilePath = getFullFilePathFor("problem")
        println("Part 1 result: " + runPart1(problemFilePath))

        if (part2SampleResult() != null) {
            val part2sampleFilePath = getFullFilePathFor("sample2")
            val sample2Result = runPart2(part2sampleFilePath)
            check(sample2Result == part2SampleResult()
            ) { "$sample2Result is not the expected ${part2SampleResult()}" }

            println("Part 2 result: " + runPart2(problemFilePath))
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
