package dam.pmdm.juegotablero

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import dam.pmdm.juegotablero.models.House

class MainActivity : ComponentActivity() {
    private lateinit var roomIcon: ImageView
    private lateinit var roomDescription: TextView
    private lateinit var answerInput: EditText
    private lateinit var submitButton: Button
    private lateinit var buttonNorth: Button
    private lateinit var buttonSouth: Button
    private lateinit var buttonEast: Button
    private lateinit var buttonWest: Button

    private val house = House(5, 5) // Ajusta el tamaño de la casa
    private var currentRoom = house.getRandomRoom()
    private var previousRoom: Pair<Int, Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        roomIcon = findViewById(R.id.roomIcon)
        roomDescription = findViewById(R.id.roomDescription)
        answerInput = findViewById(R.id.answerInput)
        submitButton = findViewById(R.id.submitButton)
        buttonNorth = findViewById(R.id.buttonNorth)
        buttonSouth = findViewById(R.id.buttonSouth)
        buttonEast = findViewById(R.id.buttonEast)
        buttonWest = findViewById(R.id.buttonWest)

        setupListeners()
        updateUI()

        Log.d("Solucion", house.exitRoom.toString())
    }

    private fun setupListeners() {
        buttonNorth.setOnClickListener { move("north") }
        buttonSouth.setOnClickListener { move("south") }
        buttonEast.setOnClickListener { move("east") }
        buttonWest.setOnClickListener { move("west") }

        submitButton.setOnClickListener {
            val answer = answerInput.text.toString()
            val room = house.getRoom(currentRoom.first, currentRoom.second)
            if (room.solveChallenge(answer)) {
                Toast.makeText(this, "Respuesta correcta", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Respuesta correcta")
                if (room.hasMoreRiddles()) {
                    room.nextRiddle()
                    Log.d("MainActivity", "Pasando al siguiente acertijo")
                } else {
                    room.markAsSolved()
                    enableMovementButtons()
                    Log.d("MainActivity", "Desbloqueando botones de movimiento")
                }
                updateUI() // Asegúrate de actualizar la UI después de cualquier cambio
            } else {
                Toast.makeText(this, "Respuesta incorrecta", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Respuesta incorrecta")
            }
        }
    }

    private fun move(direction: String) {
        val (x, y) = currentRoom
        previousRoom = currentRoom
        currentRoom = when (direction) {
            "north" -> Pair(x - 1, y)
            "south" -> Pair(x + 1, y)
            "east" -> Pair(x, y + 1)
            "west" -> Pair(x, y - 1)
            else -> currentRoom
        }
        updateUI()
        Log.d("MainActivity", "Moved to room: $currentRoom")
    }

    private fun updateUI() {
        val room = house.getRoom(currentRoom.first, currentRoom.second)

        // Comprobar si la habitación actual es la ganadora
        if (house.isExitRoom(currentRoom)) {
            val intent = Intent(this, VictoryActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        roomDescription.text = if (room.solved) "Habitación resuelta" else room.getCurrentRiddle()
        roomIcon.setImageResource(room.iconRes)
        answerInput.setText("")

        if (room.solved) {
            enableMovementButtons()
        } else {
            disableMovementButtons()
        }

        Log.d("MainActivity", "UI updated for room: $currentRoom")
    }

    private fun enableMovementButtons() {
        buttonNorth.isEnabled = currentRoom.first > 0
        buttonSouth.isEnabled = currentRoom.first < house.rows - 1
        buttonEast.isEnabled = currentRoom.second < house.cols - 1
        buttonWest.isEnabled = currentRoom.second > 0

        previousRoom?.let { previous ->
            when {
                previous.first < currentRoom.first -> buttonNorth.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
                previous.first > currentRoom.first -> buttonSouth.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
                previous.second < currentRoom.second -> buttonWest.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
                previous.second > currentRoom.second -> buttonEast.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
            }
        }

        Log.d("MainActivity", "Movement buttons enabled")
    }

    private fun disableMovementButtons() {
        buttonNorth.isEnabled = false
        buttonSouth.isEnabled = false
        buttonEast.isEnabled = false
        buttonWest.isEnabled = false

        buttonNorth.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        buttonSouth.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        buttonEast.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        buttonWest.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))

        // Mantener habilitado el botón que lleva a la habitación anterior
        previousRoom?.let { previous ->
            when {
                previous.first < currentRoom.first -> buttonNorth.isEnabled = true
                previous.first > currentRoom.first -> buttonSouth.isEnabled = true
                previous.second < currentRoom.second -> buttonWest.isEnabled = true
                previous.second > currentRoom.second -> buttonEast.isEnabled = true
            }
        }

        Log.d("MainActivity", "Movement buttons disabled")
    }
}
