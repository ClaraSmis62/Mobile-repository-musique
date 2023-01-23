package fr.eilco.repertoire_musique.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import fr.eilco.repertoire_musique.*
import fr.eilco.repertoire_musique.RepertoireMusiqueGlideModule



class ArtisteAdapter(
    private val context: MainActivity,
    private val albumList : List<AlbumModel>,
    private val layoutId : Int) : RecyclerView.Adapter<ArtisteAdapter.ViewHolder>() {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        //image de la plante
        val artisteImage = view.findViewById<ImageView>(R.id.artiste_image)
        val albumName:TextView? = view.findViewById(R.id.album_name)
        val albumSortie:TextView? = view.findViewById(R.id.sortie)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAlbum = albumList[position];

       GlideApp.with(context).load(currentAlbum.ImageUrl).into(holder.artisteImage)
        holder.albumName?.text  = currentAlbum.name
        holder.albumSortie?.text = currentAlbum.sortie

    }

    override fun getItemCount(): Int = albumList.size
}