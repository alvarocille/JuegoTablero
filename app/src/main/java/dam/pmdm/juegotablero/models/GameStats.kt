package dam.pmdm.juegotablero.models

import android.os.SystemClock

data class GameStats(
    var riddlesSolved: Int = 0,
    var failedAttempts: Int = 0,
    var roomsVisited: Int = 0,
    var startTime: Long = SystemClock.elapsedRealtime()
) {
    fun incrementRiddlesSolved() {
        riddlesSolved++
    }

    fun incrementFailedAttempts() {
        failedAttempts++
    }

    fun incrementRoomsVisited() {
        roomsVisited++
    }

    fun getElapsedTime(): Long {
        return SystemClock.elapsedRealtime() - startTime
    }
}
