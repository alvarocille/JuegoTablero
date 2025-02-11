package dam.pmdm.juegotablero.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import dam.pmdm.juegotablero.R
import dam.pmdm.juegotablero.models.House
import dam.pmdm.juegotablero.models.gamestats.GameStats
import dam.pmdm.juegotablero.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var house: House
    private lateinit var currentRoom: Pair<Int, Int>
    private var previousRoom: Pair<Int, Int>? = null
    private var victorySolved = false
    private lateinit var gameStats: GameStats
    private lateinit var launchVictoryActivity: ActivityResultLauncher<Intent>

    private lateinit var roomIcon: ImageView
    private lateinit var roomDescription: TextView
    private lateinit var roomName: TextView
    private lateinit var answerInput: EditText
    private lateinit var submitButton: Button
    private lateinit var buttonNorth: Button
    private lateinit var buttonSouth: Button
    private lateinit var buttonEast: Button
    private lateinit var buttonWest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        roomIcon = findViewById(R.id.roomIcon)
        roomName = findViewById(R.id.roomName)
        roomDescription = findViewById(R.id.roomDescription)
        answerInput = findViewById(R.id.answerInput)
        submitButton = findViewById(R.id.submitButton)
        buttonNorth = findViewById(R.id.buttonNorth)
        buttonSouth = findViewById(R.id.buttonSouth)
        buttonEast = findViewById(R.id.buttonEast)
        buttonWest = findViewById(R.id.buttonWest)

        gameStats = GameStats()

        launchVictoryActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        setupListeners()

        fetchRiddlesAndInitializeHouse()
    }

    private fun fetchRiddlesAndInitializeHouse() {
        val sharedPref = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val token = sharedPref.getString("JWT_TOKEN", null) ?: return

        lifecycleScope.launch {
            try {
                val apiService = RetrofitClient.instance
                val response = apiService.getRiddles("General")
                if (response.isSuccessful) {
                    response.body()?.let { riddles ->
                        if (riddles.isNotEmpty()) {
                            // Se actualiza la variable global que usan las salas
                            dam.pmdm.juegotablero.data.globalRiddles = riddles
                            Log.d("MainActivity", "Acertijos obtenidos: ${riddles.size}")
                        } else {
                            Log.e("MainActivity", "La lista de acertijos está vacía")
                        }
                    }
                } else {
                    Log.e("MainActivity", "Error al obtener acertijos: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Excepción al obtener acertijos: ${e.message}")
            } finally {
                // Ahora se inicializa la House con la lista actualizada
                house = House(4, 4)
                currentRoom = house.getRandomRoom()
                updateUI()
            }
        }
    }

    private fun setupListeners() {
        buttonNorth.setOnClickListener { move("north") }
        buttonSouth.setOnClickListener { move("south") }
        buttonEast.setOnClickListener { move("east") }
        buttonWest.setOnClickListener { move("west") }

        submitButton.setOnClickListener {
            val answer = answerInput.text.toString()
            val room = house.getRoom(currentRoom.first, currentRoom.second)
            Log.d("MainActivity", "Respuesta recibida: $answer")
            if (room.solveChallenge(answer)) {
                gameStats.incrementRiddlesSolved()
                Toast.makeText(this, "Respuesta correcta", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Respuesta correcta")
                if (room.hasMoreRiddles()) {
                    room.nextRiddle()
                    Log.d("MainActivity", "Pasando al siguiente acertijo")
                } else {
                    room.markAsSolved()
                    Log.d("MainActivity", "Habitación resuelta")
                    if (house.isExitRoom(currentRoom)) {
                        victorySolved = true
                        Log.d("MainActivity", "Habitación de victoria resuelta")
                        launchVictory()
                    } else {
                        enableMovementButtons()
                        Log.d("MainActivity", "Desbloqueando botones de movimiento")
                    }
                }
                updateUI()
            } else {
                gameStats.incrementFailedAttempts()
                Toast.makeText(this, "Respuesta incorrecta", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Respuesta incorrecta")
            }
        }
    }

    private fun move(direction: String) {
        val (x, y) = currentRoom
        Log.d("MainActivity", "Intentando mover a dirección: $direction")

        previousRoom = currentRoom
        currentRoom = when (direction) {
            "north" -> Pair(x - 1, y)
            "south" -> Pair(x + 1, y)
            "east" -> Pair(x, y + 1)
            "west" -> Pair(x, y - 1)
            else -> currentRoom
        }

        if (previousRoom != currentRoom) {
            gameStats.incrementRoomsVisited()
        }

        updateUI()
        Log.d("MainActivity", "Movido a: $currentRoom")
    }

    private fun updateUI() {
        val room = house.getRoom(currentRoom.first, currentRoom.second)
        roomName.text = room.name
        if (room.solved) {
            roomDescription.text = "Habitación resuelta"
            answerInput.isEnabled = false
            enableMovementButtons()
        } else {
            roomDescription.text = room.getCurrentRiddle()
            answerInput.isEnabled = true
            disableMovementButtons()
        }
        roomIcon.setImageResource(room.iconRes)
        answerInput.setText("")

        Log.d("MainActivity", "UI actualizada en: $currentRoom")
    }

    private fun enableMovementButtons() {
        buttonNorth.isEnabled = currentRoom.first > 0
        buttonSouth.isEnabled = currentRoom.first < house.rows - 1
        buttonEast.isEnabled = currentRoom.second < house.cols - 1
        buttonWest.isEnabled = currentRoom.second > 0

        Log.d("MainActivity", "Botones de movimiento activados")
    }

    private fun disableMovementButtons() {
        buttonNorth.isEnabled = false
        buttonSouth.isEnabled = false
        buttonEast.isEnabled = false
        buttonWest.isEnabled = false

        previousRoom?.let { previous ->
            when {
                previous.first < currentRoom.first -> buttonNorth.isEnabled = true
                previous.first > currentRoom.first -> buttonSouth.isEnabled = true
                previous.second < currentRoom.second -> buttonWest.isEnabled = true
                previous.second > currentRoom.second -> buttonEast.isEnabled = true
            }
        }

        Log.d("MainActivity", "Botones de movimiento desactivados")
    }

    private fun launchVictory() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.open_door)
        roomIcon.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                // No necesario
            }

            override fun onAnimationEnd(animation: Animation) {
                val intent = Intent(this@MainActivity, VictoryActivity::class.java).apply {
                    putExtra("riddlesSolved", gameStats.riddlesSolved)
                    putExtra("failedAttempts", gameStats.failedAttempts)
                    putExtra("roomsVisited", gameStats.roomsVisited)
                    putExtra("elapsedTime", gameStats.getElapsedTime())
                }
                launchVictoryActivity.launch(intent)
            }

            override fun onAnimationRepeat(animation: Animation) {
                // No necesario
            }
        })
    }
}