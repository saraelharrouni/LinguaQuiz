package com.example.linguaquiz
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordAdapter(
    private val wordsList: List<Word>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textFr: TextView = itemView.findViewById(R.id.textWordFr)
        val textEn: TextView = itemView.findViewById(R.id.textWordEn)
        // C'est bien un Button classique maintenant
        val btnDelete: Button = itemView.findViewById(R.id.btnDeleteWord)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_item_layout, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val currentWord = wordsList[position]
        holder.textFr.text = currentWord.wordFr
        holder.textEn.text = currentWord.wordEn

        holder.btnDelete.setOnClickListener {
            onDeleteClick(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return wordsList.size
    }
}