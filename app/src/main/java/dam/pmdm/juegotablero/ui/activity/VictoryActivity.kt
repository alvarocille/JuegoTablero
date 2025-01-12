package dam.pmdm.juegotablero.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.view.animation.AnimationUtils
import androidx.activity.ComponentActivity
import dam.pmdm.juegotablero.R

class VictoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.victory)

        val riddlesSolved = intent.getIntExtra("riddlesSolved", 0)
        val failedAttempts = intent.getIntExtra("failedAttempts", 0)
        val roomsVisited = intent.getIntExtra("roomsVisited", 0)
        val elapsedTime = intent.getLongExtra("elapsedTime", 0)

        val riddlesSolvedView: TextView = findViewById(R.id.riddlesSolved)
        val failedAttemptsView: TextView = findViewById(R.id.failedAttempts)
        val roomsVisitedView: TextView = findViewById(R.id.roomsVisited)
        val elapsedTimeView: TextView = findViewById(R.id.elapsedTime)

        riddlesSolvedView.text = "Acertijos resueltos: $riddlesSolved"
        failedAttemptsView.text = "Intentos fallidos: $failedAttempts"
        roomsVisitedView.text = "Salas visitadas: $roomsVisited"
        elapsedTimeView.text = "Tiempo empleado: ${elapsedTime / 1000} segundos"

        val restartButton: Button = findViewById(R.id.restartButton)
        restartButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val animationView: ImageView = findViewById(R.id.animationView)
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        animationView.startAnimation(rotateAnimation)
    }
}
