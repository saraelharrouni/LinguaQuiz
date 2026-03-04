package com.example.linguaquiz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DictionaryActivity : AppCompatActivity() {

    // On ne met plus la liste en dur ici, on va la charger depuis la mémoire !
    private lateinit var vocabularyList: MutableList<Word>
    private lateinit var adapter: WordAdapter

    // onCreate est appelé lorsque l'activité est créée
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        // 1. On charge notre liste sauvegardée grâce à notre StorageManager
        vocabularyList = StorageManager.getWords(this)

        // Récupération des composants visuels
        val editWordFr = findViewById<EditText>(R.id.editWordFr)
        val editWordEn = findViewById<EditText>(R.id.editWordEn)
        val btnAddWord = findViewById<Button>(R.id.btnAddWord)
        val wordsRecyclerView = findViewById<RecyclerView>(R.id.wordsRecyclerView)

        // 2. Configuration du RecyclerView
        wordsRecyclerView.layoutManager = LinearLayoutManager(this)
        // On instancie l'Adapter en lui disant quoi faire si on clique sur la corbeille
        adapter = WordAdapter(vocabularyList) { positionCliquee ->
            // 1. On retire le mot de notre liste
            vocabularyList.removeAt(positionCliquee)

            // 2. On prévient l'Adapter qu'un élément a disparu pour qu'il anime la liste
            adapter.notifyItemRemoved(positionCliquee)

            // 3.  On sauvegarde la nouvelle liste (sans le mot supprimé) !
            StorageManager.saveWords(this@DictionaryActivity, vocabularyList)

            Toast.makeText(this@DictionaryActivity, "Mot supprimé", Toast.LENGTH_SHORT).show()
        }
        wordsRecyclerView.adapter = adapter

        // 3. Action du bouton "Ajouter"
        btnAddWord.setOnClickListener {
            val frText = editWordFr.text.toString().trim()
            val enText = editWordEn.text.toString().trim()

            if (frText.isNotEmpty() && enText.isNotEmpty()) {
                val newWord = Word(frText, enText)

                // On ajoute à la liste affichée
                vocabularyList.add(newWord)
                adapter.notifyItemInserted(vocabularyList.size - 1)

                // 🌟 LA MAGIE EST ICI : On sauvegarde la nouvelle liste !
                StorageManager.saveWords(this, vocabularyList)

                editWordFr.text.clear()
                editWordEn.text.clear()

                Toast.makeText(this, "Mot ajouté et sauvegardé !", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Veuillez remplir les deux champs", Toast.LENGTH_SHORT).show()
            }
        }
    }
}