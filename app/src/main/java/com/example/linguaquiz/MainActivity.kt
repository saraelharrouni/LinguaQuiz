package com.example.linguaquiz

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStartSession: Button = findViewById(R.id.btnStartSession)
        val btnManageWords: Button = findViewById(R.id.btnManageWords)
        val directionRadioGroup: RadioGroup = findViewById(R.id.directionRadioGroup)

        // Lancement de la session
        btnStartSession.setOnClickListener {
            val intent = android.content.Intent(this, SessionActivity::class.java)
            val isFrToEn = directionRadioGroup.checkedRadioButtonId == R.id.radioFrToEn
            intent.putExtra("IS_FR_TO_EN", isFrToEn)
            startActivity(intent)
        }

        // Ouverture du dictionnaire
        btnManageWords.setOnClickListener {
            val intent = android.content.Intent(this, DictionaryActivity::class.java)
            startActivity(intent)
        }
    }

    //  onResume est appelé à chaque fois qu'on revient sur cet écran
    override fun onResume() {
        super.onResume()

        // On récupère la zone de texte
        val statsTextView: TextView = findViewById(R.id.statsTextView)

        // On charge l'historique des sessions
        val statsList = StorageManager.getStats(this)

        // On construit le texte à afficher
        if (statsList.isEmpty()) {
            statsTextView.text = "Aucune session jouée pour le moment."
        } else {
            var affichageStats = "Historique de vos sessions :\n\n"

            // On parcourt notre liste
            for (stat in statsList) {
                if (stat.isAbandoned) {
                    // 🌟 L'affichage exact que tu voulais !
                    affichageStats += "Session ${stat.sessionNumber} : Abandonnée\n"
                } else {
                    // L'affichage classique avec le score
                    affichageStats += "Session ${stat.sessionNumber} : ${stat.correctAnswers} / ${stat.totalWords} correctes\n"
                }
            }

            statsTextView.text = affichageStats
        }
    }
}