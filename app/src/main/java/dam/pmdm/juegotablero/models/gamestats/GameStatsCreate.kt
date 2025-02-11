package dam.pmdm.juegotablero.models.gamestats

data class GameStatsCreate(
    val riddles_solved: Int,
    val failed_attempts: Int,
    val rooms_visited: Int,
    val elapsed_time: Float
)
