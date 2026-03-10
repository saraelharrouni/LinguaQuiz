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

    private var score = 0
    private var currentWordIndex = 0 // Pour savoir à quel mot on en est
    private var totalQuestions = 0 // Le nombre total de mots dans le dictionnaire

    private var remainingTries = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        isFrToEn = intent.getBooleanExtra("IS_FR_TO_EN", true)

        // On charge les mots et on les mélange comme un jeu de cartes !
        vocabularyList = StorageManager.getWords(this).shuffled()
        totalQuestions = vocabularyList.size // On posera autant de questions qu'il y a de mots

        val textDirectionInfo = findViewById<TextView>(R.id.textDirectionInfo)
        val textWordToTranslate = findViewById<TextView>(R.id.textWordToTranslate)
        val editAnswer = findViewById<EditText>(R.id.editAnswer)
        val btnSubmitAnswer = findViewById<Button>(R.id.btnSubmitAnswer)
        val btnQuitSession = findViewById<Button>(R.id.btnQuitSession)

        btnQuitSession.setOnClickListener {
            finishSession(isAbandoned = true) // On prévient que c'est un abandon !
        }

        textDirectionInfo.text = if (isFrToEn) "Traduction : Français -> Anglais" else "Traduction : Anglais -> Français"

        fun pickNextWord() {
            // Si on a vu tous les mots du dictionnaire, la session est finie !
            if (currentWordIndex >= totalQuestions) {
                finishSession()
                return
            }

            if (vocabularyList.isNotEmpty()) {
                // On prend le mot suivant dans notre liste mélangée
                currentWord = vocabularyList[currentWordIndex]
                textWordToTranslate.text = if (isFrToEn) currentWord?.wordFr else currentWord?.wordEn
                editAnswer.text.clear()

                // On remet bien les 3 essais pour le nouveau mot
                remainingTries = 3
            } else {
                textWordToTranslate.text = "Dictionnaire vide"
                btnSubmitAnswer.isEnabled = false
            }
        }

        // On lance le premier mot
        pickNextWord()

        btnSubmitAnswer.setOnClickListener {
            val userAnswer = editAnswer.text.toString().trim()
            if (userAnswer.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer une traduction", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val correctAnswer = if (isFrToEn) currentWord?.wordEn else currentWord?.wordFr

            if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
                // S'il a juste : 1 point et on passe au mot suivant
                score++
                Toast.makeText(this, "Bravo !", Toast.LENGTH_SHORT).show()
                currentWordIndex++ // On avance dans la liste
                pickNextWord()
            } else {
                // S'il a faux : on enlève un essai
                remainingTries--

                if (remainingTries > 0) {
                    Toast.makeText(this, "Faux ! Il vous reste $remainingTries essai(s).", Toast.LENGTH_SHORT).show()
                    editAnswer.text.clear()
                } else {
                    // Au bout de 3 erreurs : on donne la réponse et on avance au mot suivant !
                    Toast.makeText(this, "Dommage ! La bonne réponse était : $correctAnswer", Toast.LENGTH_LONG).show()
                    currentWordIndex++ // On avance dans la liste
                    pickNextWord()
                }
            }
        }
    }

    // On ajoute un paramètre "isAbandoned" (qui est faux par défaut)
    // NOUVEAU : On ajoute le paramètre isAbandoned
    private fun finishSession(isAbandoned: Boolean = false) {
        val pastSessionsCount = StorageManager.getStats(this).size
        val sessionNumber = pastSessionsCount + 1

        // On crée la stat en lui passant l'information isAbandoned à la fin
        val currentStat = SessionStat(sessionNumber, score, totalQuestions, isAbandoned)
        StorageManager.saveStat(this, currentStat)

        // On change le message de fin selon le cas
        if (isAbandoned) {
            Toast.makeText(this, "Session abandonnée", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Session terminée ! Score : $score / $totalQuestions", Toast.LENGTH_LONG).show()
        }

        finish()
    }
}