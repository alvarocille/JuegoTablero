package dam.pmdm.juegotablero.models

import dam.pmdm.juegotablero.R
import dam.pmdm.juegotablero.data.globalRiddles
import dam.pmdm.juegotablero.utils.normalizeString
import kotlin.random.Random

class Room(
    val name: String,
    var description: String,
) {
    private val riddles: List<Riddle>
    private var currentRiddleIndex = 0
    var solved: Boolean = false
    var iconRes = R.drawable.question_mark

    init {
        val random = Random(System.currentTimeMillis())
        val ghost = random.nextDouble() < 0.1
        iconRes = if (ghost) R.drawable.ghost else R.drawable.question_mark
        val availableRiddles = globalRiddles.ifEmpty { listOf(defaultRiddle()) }
        riddles = List(if (ghost) 2 else 1) { availableRiddles.random() }
    }

    private fun defaultRiddle(): Riddle {
        return Riddle(question = "Pregunta por defecto", solution = "Respuesta")
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
