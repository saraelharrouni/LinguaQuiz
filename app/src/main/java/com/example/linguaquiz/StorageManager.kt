package com.example.linguaquiz

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// En utilisant "object", on crée un Singleton (une instance unique) facile à appeler partout
object StorageManager {

    // Le nom de notre fichier de préférences
    private const val PREFS_NAME = "linguaquiz_prefs"
    // La clé pour retrouver notre dictionnaire
    private const val KEY_WORDS = "saved_words"

    // Fonction pour sauvegarder la liste complète
    fun saveWords(context: Context, words: List<Word>) {
        // On récupère les SharedPreferences en mode privé
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // On ouvre l'éditeur
        val editor = sharedPreferences.edit()

        // On convertit notre liste d'objets Kotlin en texte JSON grâce à Gson
        val gson = Gson()
        val json = gson.toJson(words)

        // On sauvegarde le texte
        editor.putString(KEY_WORDS, json)
        // On applique les changements
        editor.apply()
    }

    // Fonction pour récupérer la liste sauvegardée
    fun getWords(context: Context): MutableList<Word> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // On récupère le texte JSON (retourne null si rien n'est sauvegardé)
        val json = sharedPreferences.getString(KEY_WORDS, null)

        return if (json != null) {
            // Si on a trouvé des données, on reconvertit le JSON en MutableList<Word>
            val gson = Gson()
            val type = object : TypeToken<MutableList<Word>>() {}.type
            gson.fromJson(json, type)
        } else {
            // S'il n'y a rien (première ouverture), on renvoie une liste par défaut
            mutableListOf(
                Word("Pomme", "Apple"),
                Word("Chat", "Cat"),
                Word("Maison", "House")
            )
        }
    }
    // --- NOUVEAU : GESTION DES STATISTIQUES ---

    // La clé pour retrouver notre historique de scores
    private const val KEY_STATS = "saved_stats"

    // Fonction pour récupérer l'historique des sessions
    fun getStats(context: Context): MutableList<SessionStat> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) //
        val json = sharedPreferences.getString(KEY_STATS, null)

        return if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<MutableList<SessionStat>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf() // Si aucune session n'a été jouée, on renvoie une liste vide
        }
    }

    // Fonction pour ajouter et sauvegarder une nouvelle session terminée
    fun saveStat(context: Context, newStat: SessionStat) {
        // On récupère l'historique existant
        val statsList = getStats(context)
        // On y ajoute la nouvelle partie
        statsList.add(newStat)

        // On sauvegarde le tout
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_STATS, Gson().toJson(statsList))
        editor.apply()
    }
}