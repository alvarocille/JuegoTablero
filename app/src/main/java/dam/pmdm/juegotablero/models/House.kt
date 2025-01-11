package dam.pmdm.juegotablero.models

import kotlin.random.Random

class House(val rows: Int, val cols: Int, seed: Long = System.currentTimeMillis()) {
    private val random = Random(seed)
    private val rooms: Array<Array<Room>> = Array(rows) { row ->
        Array(cols) { col ->
            val roomName = "Habitación A${row}${random.nextInt(9)}${col}${random.nextInt(9)}"
            val description = "Habitación ($row, $col)"
            Room(roomName, description)
        }
    }
    val exitRoom = Pair(random.nextInt(rows), random.nextInt(cols))

    fun getRoom(row: Int, col: Int): Room = rooms[row][col]

    fun getRandomRoom(): Pair<Int, Int> {
        var randomRoom: Pair<Int, Int>
        do {
            val randomRow = (0 until rows).random()
            val randomCol = (0 until cols).random()
            randomRoom = Pair(randomRow, randomCol)
        } while (randomRoom == exitRoom)
        return randomRoom
    }

    fun isExitRoom(room: Pair<Int, Int>): Boolean = room == exitRoom
}

