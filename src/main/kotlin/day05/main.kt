package day05

// https://adventofcode.com/2023/day/5

import DayChallenge
import getFileFromFilePath

fun main() = Day05Challenge.run()

object Day05Challenge: DayChallenge(
    day = "05",
    part1SampleResult = 35,
    part2SampleResult = 46
) {

    override fun runPart1(filePath: String): Int {
        val almanac = almanacParser(getFileFromFilePath(filePath).readLines())
        return almanac.mapSeeds().min().toInt()
    }

    override fun runPart2(filePath: String): Int  {
        val almanac = almanacParser(getFileFromFilePath(filePath).readLines())
        return almanac.minMapSeedsByRange()
    }
}

fun almanacParser(lines: List<String>): Almanac {
    val seeds = lines.first().split(": ").last().split(" ").map { it.toLong() }

    val mappers = mutableListOf<CategoryMapper>()
    val mappersLines = lines.drop(2)
    var index = 0
    while (index < mappersLines.size) {
        val ls = mappersLines[index].removeSuffix(" map:").split("-to-")
        val sourceName = ls.first()
        val targetName = ls.last()
        val rangeMappers = mutableListOf<RangeMap>()
        index +=1
        while (index < mappersLines.size && mappersLines[index].isNotEmpty()) {
            val ls1 = mappersLines[index].split(" ")
            rangeMappers.add(RangeMap(ls1[0].toLong(), ls1[1].toLong(), ls1[2].toInt()))
            index +=1
        }
        mappers.add(CategoryMapper(sourceName, targetName, rangeMappers))
        index += 1
    }
    return Almanac(seeds, mappers)
}

data class Almanac(
    private val seeds: List<Long>,
    private val categoryMappers: List<CategoryMapper>
) {
    private val mappersBySource = categoryMappers.associateBy { it.sourceName }

    fun mapSeeds(): List<Long> = seeds.map { mapSeed(it) }

    private fun minInRange(start: Long, end: Long): Long {
        return (start until end).minOf { mapSeed(it) }
    }

    private fun seedsAsPairs(): List<Pair<Long, Long>> =
        seeds.chunked(2).map { Pair(it[0], it[1]) }

    fun minMapSeedsByRange(): Int =
        seedsAsPairs().minOf { minInRange(it.first, it.first + it.second) }.toInt()

    private fun mapSeed(seed: Long): Long {
        var seedMappingResult = SeedMappingResult(seed, "seed")
        while (seedMappingResult.targetCategory != "location") {
            val mapper = mappersBySource[seedMappingResult.targetCategory]!!
            seedMappingResult = mapper.map(seedMappingResult.result)
        }
        return seedMappingResult.result
    }

}

data class RangeMap(
    private val destinationRangeStart: Long,
    private val sourceRangeStart: Long,
    private val rangeSize: Int,
) {
    fun mapOrNull(value: Long): Long? =
        if (value in sourceRangeStart until sourceRangeStart+rangeSize) {
            (value - sourceRangeStart) + destinationRangeStart
        } else null
}

data class CategoryMapper(
    val sourceName: String,
    val targetName: String,
    private val rangeMaps: List<RangeMap>,
) {
    fun map(value: Long): SeedMappingResult = SeedMappingResult(mapValue(value), targetName)
    private fun mapValue(value: Long): Long =
        rangeMaps.firstNotNullOfOrNull { it.mapOrNull(value) } ?: value

}

data class SeedMappingResult(
    val result: Long,
    val targetCategory: String
)
