package dam.pmdm.juegotablero

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import dam.pmdm.juegotablero.R

class MainActivity : ComponentActivity() {
    private lateinit var roomIcon: ImageView
    private lateinit var roomDescription: TextView
    private lateinit var answerInput: EditText
    private lateinit var submitButton: Button
    private lateinit var buttonNorth: Button
    private lateinit var buttonSouth: Button
    private lateinit var buttonEast: Button
    private lateinit var buttonWest: Button

    private val house = House()
    private var currentRoom = Pair(0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        // Vincular componentes de la interfaz
        roomIcon = findViewById(R.id.roomIcon)
        roomDescription = findViewById(R.id.roomDescription)
        answerInput = findViewById(R.id.answerInput)
        submitButton = findViewById(R.id.submitButton)
        buttonNorth = findViewById(R.id.buttonNorth)
        buttonSouth = findViewById(R.id.buttonSouth)
        buttonEast = findViewById(R.id.buttonEast)
        buttonWest = findViewById(R.id.buttonWest)

        updateUI()

        // Listeners para botones de movimiento
        buttonNorth.setOnClickListener { move("north") }
        buttonSouth.setOnClickListener { move("south") }
        buttonEast.setOnClickListener { move("east") }
        buttonWest.setOnClickListener { move("west") }

        // Listener para enviar respuestas
        submitButton.setOnClickListener {
            val answer = answerInput.text.toString()
            val room = house.getRoom(currentRoom.first, currentRoom.second)
            if (room.solveChallenge(answer)) {
                Toast.makeText(this, "Respuesta correcta", Toast.LENGTH_SHORT).show()
                updateUI()
            } else {
                Toast.makeText(this, "Respuesta incorrecta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun move(direction: String) {
        val (x, y) = currentRoom
        currentRoom = when (direction) {
            "north" -> Pair(x - 1, y)
            "south" -> Pair(x + 1, y)
            "east" -> Pair(x, y + 1)
            "west" -> Pair(x, y - 1)
            else -> currentRoom
        }
        updateUI()
    }

    private fun updateUI() {
        val room = house.getRoom(currentRoom.first, currentRoom.second)
        roomDescription.text = room.description
        roomIcon.setImageResource(room.iconRes)
        answerInput.setText("")
        updateMovementButtons()
    }

    private fun updateMovementButtons() {
        buttonNorth.isEnabled = currentRoom.first > 0
        buttonSouth.isEnabled = currentRoom.first < house.size - 1
        buttonEast.isEnabled = currentRoom.second < house.size - 1
        buttonWest.isEnabled = currentRoom.second > 0
    }



}