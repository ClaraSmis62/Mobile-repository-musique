package fr.eilco.repertoire_musique.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.eilco.repertoire_musique.MainActivity
import fr.eilco.repertoire_musique.R
import fr.eilco.repertoire_musique.SongModel

class SongAdapter (
    private val context: MainActivity,
    private val songList : List<SongModel>,
    private val layoutId : Int) : RecyclerView.Adapter<SongAdapter.ViewHolder>()  {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val artisteName: TextView? = view.findViewById(R.id.chanteur)
        val songName: TextView? = view.findViewById(R.id.song_name)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSong = songList[position]

        holder.artisteName?.text  = currentSong.artiste_name
        holder.songName?.text = currentSong.title
    }

    override fun getItemCount(): Int = songList.size

}