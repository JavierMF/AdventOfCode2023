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

    private fun range(i: Int): Set<Int> = (i - 1 .. i + 1).toSet()

    fun neighboursInGrid(maxX: Int, maxY: Int): Set<Coords> = rangeInBounday(x, maxX)
        .flatMap { posX ->
            rangeInBounday(y, maxY).map { posY -> Coords(posX, posY) }
                .filter { it != this }
        }.toSet()

    private fun rangeInBounday(i: Int, max: Int): Set<Int> = (i - 1..i + 1)
        .filter { it in 0..max }.toSet()

    fun rightCoord(): Coords = Coords(x+1, y)
    fun leftCoord(): Coords = Coords(x-1, y)
    fun upCoord(): Coords = Coords(x, y-1)
    fun downCoord(): Coords = Coords(x, y+1)
}

data class Grid(
    val cells: List<Cell>
) {
    private val cellsByCoords = cells.associateBy { it.coords }

    fun cellInCoords(coords: Coords): Cell? = cellsByCoords[coords]
}
