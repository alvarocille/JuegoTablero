package dam.pmdm.juegotablero.models

data class Riddle(
    val id: Int = 0,
    val question: String,
    val solution: String,
    val category: String = "General"
)
