package dam.pmdm.juegotablero.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import dam.pmdm.juegotablero.R
import dam.pmdm.juegotablero.models.gamestats.GameStatsCreate
import dam.pmdm.juegotablero.models.gamestats.GameStatsResponse
import dam.pmdm.juegotablero.network.RetrofitClient
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class VictoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.victory)

        val currentRiddlesSolved = intent.getIntExtra("riddlesSolved", 0)
        val currentFailedAttempts = intent.getIntExtra("failedAttempts", 0)
        val currentRoomsVisited = intent.getIntExtra("roomsVisited", 0)
        val currentElapsedTimeMs = intent.getLongExtra("elapsedTime", 0) // en milisegundos
        val currentElapsedTimeSec = (currentElapsedTimeMs / 1000).toInt()  // en segundos

        val riddlesSolvedView: TextView = findViewById(R.id.riddlesSolved)
        val failedAttemptsView: TextView = findViewById(R.id.failedAttempts)
        val roomsVisitedView: TextView = findViewById(R.id.roomsVisited)
        val elapsedTimeView: TextView = findViewById(R.id.elapsedTime)

        riddlesSolvedView.text = getString(R.string.riddles_solved) + " $currentRiddlesSolved"
        failedAttemptsView.text = getString(R.string.wrong_tries) + " $currentFailedAttempts"
        roomsVisitedView.text = getString(R.string.visited_rooms) + " $currentRoomsVisited"
        elapsedTimeView.text = getString(R.string.time) + " $currentElapsedTimeSec " + getString(R.string.seconds)

        val bestRiddlesSolvedView: TextView = findViewById(R.id.bestRiddlesSolved)
        val bestFailedAttemptsView: TextView = findViewById(R.id.bestFailedAttempts)
        val bestRoomsVisitedView: TextView = findViewById(R.id.bestRoomsVisited)
        val bestElapsedTimeView: TextView = findViewById(R.id.bestElapsedTime)

        uploadGameStats(currentRiddlesSolved, currentFailedAttempts, currentRoomsVisited, currentElapsedTimeSec) {
            fetchAllGameStats { statsList ->
                val bestRiddles = if (statsList.isEmpty())
                    currentRiddlesSolved
                else
                    max(currentRiddlesSolved, statsList.maxByOrNull { it.riddles_solved }?.riddles_solved ?: currentRiddlesSolved)

                val bestFailures = if (statsList.isEmpty())
                    currentFailedAttempts
                else
                    min(currentFailedAttempts, statsList.minByOrNull { it.failed_attempts }?.failed_attempts ?: currentFailedAttempts)

                val bestRooms = if (statsList.isEmpty())
                    currentRoomsVisited
                else
                    max(currentRoomsVisited, statsList.maxByOrNull { it.rooms_visited }?.rooms_visited ?: currentRoomsVisited)

                val bestTime = if (statsList.isEmpty())
                    currentElapsedTimeSec
                else
                    min(currentElapsedTimeSec, statsList.minByOrNull { it.elapsed_time }?.elapsed_time?.toInt() ?: currentElapsedTimeSec)

                bestRiddlesSolvedView.text = getString(R.string.best_riddles_solved) + " $bestRiddles"
                bestFailedAttemptsView.text = getString(R.string.best_failed_attempts) + " $bestFailures"
                bestRoomsVisitedView.text = getString(R.string.best_rooms_visited) + " $bestRooms"
                bestElapsedTimeView.text = getString(R.string.best_time) + " $bestTime " + getString(R.string.seconds)
            }
        }

        // Botón para reiniciar el juego: se limpia la pila de actividades para reiniciar completamente MainActivity.
        val restartButton: Button = findViewById(R.id.restartButton)
        restartButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            finish()
        }

        // Inicia la animación en la imagen de victoria.
        val animationView: ImageView = findViewById(R.id.animationView)
        val rotateAnimation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.rotate)
        animationView.startAnimation(rotateAnimation)
    }

    private fun uploadGameStats(
        riddlesSolved: Int,
        failedAttempts: Int,
        roomsVisited: Int,
        elapsedTimeSec: Int,
        onComplete: () -> Unit
    ) {
        val sharedPref = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val token = sharedPref.getString("JWT_TOKEN", null) ?: run {
            onComplete()
            return
        }
        lifecycleScope.launch {
            try {
                val apiService = RetrofitClient.instance
                val request = GameStatsCreate(
                    riddles_solved = riddlesSolved,
                    failed_attempts = failedAttempts,
                    rooms_visited = roomsVisited,
                    elapsed_time = elapsedTimeSec.toFloat()
                )
                val response = apiService.postGameStats("Bearer $token", request)
                if (response.isSuccessful) {
                    Log.d("VictoryActivity", "Game stats uploaded successfully")
                } else {
                    Log.e("VictoryActivity", "Error uploading game stats: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("VictoryActivity", "Exception uploading game stats: ${e.message}")
            } finally {
                onComplete()
            }
        }
    }

    private fun fetchAllGameStats(callback: (List<GameStatsResponse>) -> Unit) {
        val sharedPref = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val token = sharedPref.getString("JWT_TOKEN", null) ?: run {
            callback(emptyList())
            return
        }
        lifecycleScope.launch {
            try {
                val apiService = RetrofitClient.instance
                val response = apiService.getGameStats("Bearer $token")
                if (response.isSuccessful) {
                    val statsList = response.body() ?: emptyList()
                    callback(statsList)
                } else {
                    Log.e("VictoryActivity", "Error fetching game stats: ${response.code()}")
                    callback(emptyList())
                }
            } catch (e: Exception) {
                Log.e("VictoryActivity", "Exception fetching game stats: ${e.message}")
                callback(emptyList())
            }
        }
    }
}