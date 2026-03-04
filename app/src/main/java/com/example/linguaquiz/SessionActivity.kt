package com.example.linguaquiz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SessionActivity : AppCompatActivity() {

    private lateinit var vocabularyList: List<Word>
    private var currentWord: Word? = null
    private var isFrToEn = true

    // 🌟 NOUVEAU : Variables pour suivre le score
    private var score = 0
    private var questionsAsked = 0
    private var maxQuestions = 5 // On pose 5 questions par session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        isFrToEn = intent.getBooleanExtra("IS_FR_TO_EN", true)
        vocabularyList = StorageManager.getWords(this)

        // Si le dictionnaire a moins de 5 mots, on ajuste le nombre de questions
        if (vocabularyList.size < maxQuestions) {
            maxQuestions = vocabularyList.size
        }

        val textDirectionInfo = findViewById<TextView>(R.id.textDirectionInfo)
        val textWordToTranslate = findViewById<TextView>(R.id.textWordToTranslate)
        val editAnswer = findViewById<EditText>(R.id.editAnswer)
        val btnSubmitAnswer = findViewById<Button>(R.id.btnSubmitAnswer)

        textDirectionInfo.text = if (isFrToEn) "Traduction : Français -> Anglais" else "Traduction : Anglais -> Français"

        // Fonction pour piocher un mot
        fun pickNextWord() {
            // Si on a posé toutes les questions, la session est finie !
            if (questionsAsked >= maxQuestions) {
                finishSession()
                return
            }

            if (vocabularyList.isNotEmpty()) {
                currentWord = vocabularyList.random()
                textWordToTranslate.text = if (isFrToEn) currentWord?.wordFr else currentWord?.wordEn
                editAnswer.text.clear()
            } else {
                textWordToTranslate.text = "Dictionnaire vide"
                btnSubmitAnswer.isEnabled = false // On désactive le bouton
            }
        }

        pickNextWord()

        // Clic sur le bouton de vérification
        btnSubmitAnswer.setOnClickListener {
            val userAnswer = editAnswer.text.toString().trim()
            if (userAnswer.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer une traduction", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val correctAnswer = if (isFrToEn) currentWord?.wordEn else currentWord?.wordFr

            // On vérifie la réponse
            if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
                score++ // 🌟 On ajoute un point !
                Toast.makeText(this, "Bravo !", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Faux ! La bonne réponse était : $correctAnswer", Toast.LENGTH_LONG).show()
            }

            questionsAsked++ // On passe à la question suivante
            pickNextWord()
        }
    }

    // 🌟 NOUVEAU : Fonction appelée quand la session est terminée
    private fun finishSession() {
        // On récupère le nombre total de sessions jouées pour donner un numéro à celle-ci
        val pastSessionsCount = StorageManager.getStats(this).size
        val sessionNumber = pastSessionsCount + 1

        // On crée notre objet statistique
        val currentStat = SessionStat(sessionNumber, score, maxQuestions)

        // On le sauvegarde !
        StorageManager.saveStat(this, currentStat)

        // On affiche un message avec le score final
        Toast.makeText(this, "Session terminée ! Score : $score / $maxQuestions", Toast.LENGTH_LONG).show()

        // On ferme cet écran pour revenir au menu principal
        finish()
    }
}