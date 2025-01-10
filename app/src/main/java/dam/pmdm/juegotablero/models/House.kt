package dam.pmdm.juegotablero.models

import dam.pmdm.juegotablero.R
import kotlin.random.Random

class House(val rows: Int, val cols: Int, seed: Long = System.currentTimeMillis()) {
    private val random = Random(seed)
    private val rooms: Array<Array<Room>> = Array(rows) { row ->
        Array(cols) { col ->
            val roomName = "Habitación A${row}-${col}-${random.nextInt(1000)}"
            val description = "Habitación ($row, $col)"
            val iconRes = if (random.nextDouble() < 0.1) R.drawable.ghost else R.drawable.question_mark
            Room(roomName, description, iconRes)
        }
    }
    val exitRoom = Pair(random.nextInt(rows), random.nextInt(cols))

    fun getRoom(row: Int, col: Int): Room = rooms[row][col]

    fun getRandomRoom(): Pair<Int, Int> = Pair(random.nextInt(rows), random.nextInt(cols))

    fun isExitRoom(room: Pair<Int, Int>): Boolean = room == exitRoom
}
