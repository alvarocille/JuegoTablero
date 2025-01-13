package dam.pmdm.juegotablero.models

import dam.pmdm.juegotablero.R
import dam.pmdm.juegotablero.data.listOfRiddles
import dam.pmdm.juegotablero.utils.normalizeString
import kotlin.random.Random

data class Room(
    val name: String,
    var description: String,
) {
    private val riddles: List<Riddle>
    private var currentRiddleIndex = 0
    var solved: Boolean = false
    var iconRes = R.drawable.question_mark

    init {
        val random = Random(System.currentTimeMillis())
        val ghost = if (random.nextDouble() < 0.1) true else false
        iconRes = if (ghost) R.drawable.ghost else R.drawable.question_mark
        riddles = List( if (ghost) 2 else 1) {
            listOfRiddles.random()
        }
    }

    fun solveChallenge(userAnswer: String): Boolean {
        val normalizedUserAnswer = userAnswer.normalizeString()
        val normalizedSolution = riddles[currentRiddleIndex].solution.normalizeString()
        return normalizedSolution.equals(normalizedUserAnswer.trim(), ignoreCase = true)
    }

    fun hasMoreRiddles(): Boolean {
        this.iconRes = R.drawable.ghost_dead
        return currentRiddleIndex < riddles.size - 1
    }

    fun nextRiddle() {
        if (hasMoreRiddles()) {
            currentRiddleIndex++
        }
    }

    fun getCurrentRiddle(): String {
        return riddles[currentRiddleIndex].question
    }

    fun markAsSolved() {
        solved = true
        iconRes = R.drawable.key
    }
}
