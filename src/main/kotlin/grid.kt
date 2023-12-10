data class Cell(val value: Char, val coords: Coords) {
    val isSymbol = value !in '0' .. '9'
}

fun List<String>.toCells(mapper: (Char, Int, Int) -> Cell? = defaultCellMapper) =
    this.flatMapIndexed { rowIndex, line ->
        line.mapIndexedNotNull { colIndex, char -> mapper(char, colIndex, rowIndex) }
    }

val defaultCellMapper = { c: Char, x: Int, y: Int ->
    when (c) {
        '.' -> null
        else -> Cell(c, Coords(x, y))
    }
}

data class Coords(val x: Int, val y: Int) {

    fun neighbours(): Set<Coords> = range(x)
        .flatMap { posX ->
            range(y).map { posY -> Coords(posX, posY) }
                .filter { it != this }
        }.toSet()

    fun verticalNeighbours(): Set<Coords> = setOf(
            this.moveTo(Direction.UP),
            this.moveTo(Direction.DOWN),
            this.moveTo(Direction.LEFT),
            this.moveTo(Direction.RIGHT),
    )

    private fun range(i: Int): Set<Int> = (i - 1 .. i + 1).toSet()

    fun neighboursInGrid(maxX: Int, maxY: Int): Set<Coords> = rangeInBounday(x, maxX)
        .flatMap { posX ->
            rangeInBounday(y, maxY).map { posY -> Coords(posX, posY) }
                .filter { it != this }
        }.toSet()

    private fun rangeInBounday(i: Int, max: Int): Set<Int> = (i - 1..i + 1)
        .filter { it in 0..max }.toSet()

    fun rightCoord(): Coords = moveTo(Direction.RIGHT)
    fun leftCoord(): Coords = moveTo(Direction.LEFT)
    fun upCoord(): Coords = moveTo(Direction.UP)
    fun downCoord(): Coords = moveTo(Direction.DOWN)
    fun moveTo(dir: Direction): Coords = Coords(x + dir.x, y + dir.y)
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, -1),
    DOWN(0, 1),
    RIGHT(1, 0),
    LEFT(-1, 0)
}

data class Grid(
    val cells: List<Cell>
) {
    private val cellsByCoords = cells.associateBy { it.coords }

    fun cellInCoords(coords: Coords): Cell? = cellsByCoords[coords]
}
