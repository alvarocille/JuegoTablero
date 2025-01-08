package dam.pmdm.juegotablero

import kotlin.random.Random

class House(val size: Int = 4) {
    private val rooms: Array<Array<Room>> = Array(size) { row ->
        Array(size) { col ->
            Room(
                description = "Habitación ($row, $col)",
                iconRes = R.drawable.ic_room,
                answer = if (Random.nextBoolean()) "respuesta" else "solución"
            )
        }
    }

    fun getRoom(row: Int, col: Int): Room = rooms[row][col]
}