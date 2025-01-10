package dam.pmdm.juegotablero.models

import dam.pmdm.juegotablero.R
import dam.pmdm.juegotablero.data.listOfChallenges
import kotlin.random.Random

data class Room(
    val name: String,
    var description: String,
    var iconRes: Int
) {
    private val challenges: List<Challenge>
    private var currentRiddleIndex = 0
    var solved: Boolean = false

    init {
        val random = Random(System.currentTimeMillis())
        challenges = List(if (random.nextDouble() < 0.1) 2 else 1) {
            listOfChallenges.random()
        }
    }

    fun solveChallenge(userAnswer: String): Boolean {
        return challenges[currentRiddleIndex].solution.equals(userAnswer.trim(), ignoreCase = true)
    }

    fun hasMoreRiddles(): Boolean {
        return currentRiddleIndex < challenges.size - 1
    }

    fun nextRiddle() {
        if (hasMoreRiddles()) {
            currentRiddleIndex++
        }
    }

    fun getCurrentRiddle(): String {
        return challenges[currentRiddleIndex].question
    }

    fun markAsSolved() {
        solved = true
        iconRes = R.drawable.key
    }
}
