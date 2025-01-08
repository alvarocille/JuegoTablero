package dam.pmdm.juegotablero

data class Room(val description: String, val iconRes: Int, private val answer: String) {
    fun solveChallenge(userAnswer: String): Boolean {
        return answer.equals(userAnswer.trim(), ignoreCase = true)
    }
}
