package com.example.linguaquiz

// Ce fichier contient la structure de nos données

// La classe Word représente une paire de mots à traduire
// Nous utilisons "data class" qui est parfait pour stocker des données en Kotlin
data class Word(
    val wordFr: String, // Le mot en français
    val wordEn: String  // Le mot en anglais
)

// La classe SessionStat servira à sauvegarder les résultats pour voir la progression
data class SessionStat(
    val sessionNumber: Int, // Le numéro de la session (1ère, 2ème...)
    val correctAnswers: Int, // Nombre de bonnes réponses
    val totalWords: Int      // Nombre total de mots dans la session
)