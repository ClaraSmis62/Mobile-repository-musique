package fr.eilco.repertoire_musique.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import fr.eilco.repertoire_musique.AlbumModel
import fr.eilco.repertoire_musique.GlideApp
import fr.eilco.repertoire_musique.MainActivity
import fr.eilco.repertoire_musique.R
import fr.eilco.repertoire_musique.fragments.SongFragment

class AlbumAdapter(
    private val context: MainActivity,
    private val albumList : List<AlbumModel>,
    private val layoutId : Int) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>()  {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        //image de la plante
        val artisteImage: ImageView = view.findViewById(R.id.artiste_image)
        val albumName:TextView? = view.findViewById(R.id.album_name)
        val albumSortie:TextView? = view.findViewById(R.id.sortie)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAlbum = albumList[position]
        holder.itemView.setOnClickListener {
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            val fragment = SongFragment(context)

            val bundle = Bundle()
            bundle.putString(SongFragment.REQUEST, "https://musicbrainz.org/ws/2/recording/?query=reid:${currentAlbum.id}&fmt=json")

            fragment.arguments = bundle
            context.findViewById<TextView>(R.id.page_title).text = "Les chansons"
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        GlideApp.with(context).load(currentAlbum.ImageUrl).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.artisteImage)
        holder.albumName?.text  = currentAlbum.name
        holder.albumSortie?.text = currentAlbum.sortie
    }

    override fun getItemCount(): Int = albumList.size

}