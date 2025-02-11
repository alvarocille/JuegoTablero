package dam.pmdm.juegotablero.models.gamestats

data class GameStatsResponse(
    val id: Int,
    val user_id: Int,
    val riddles_solved: Int,
    val failed_attempts: Int,
    val rooms_visited: Int,
    val elapsed_time: Float,
    val created_at: String
)
