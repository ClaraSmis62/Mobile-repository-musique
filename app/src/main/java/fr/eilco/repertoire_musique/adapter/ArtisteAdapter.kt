package fr.eilco.repertoire_musique.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import fr.eilco.repertoire_musique.ArtisteModel
import fr.eilco.repertoire_musique.MainActivity
import fr.eilco.repertoire_musique.R
import fr.eilco.repertoire_musique.fragments.CollectionFragment


class ArtisteAdapter(
    private val context: MainActivity,
    private val artistList : List<ArtisteModel>,
    private val layoutId : Int) : RecyclerView.Adapter<ArtisteAdapter.ViewHolder>() {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        //image de la plante
        val name:TextView? = view.findViewById(R.id.Nom_artiste)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAlbum = artistList[position]
        holder.itemView.setOnClickListener {
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            val fragment = CollectionFragment(context)

            val bundle = Bundle()
            bundle.putString(CollectionFragment.REQUEST, "https://musicbrainz.org/ws/2/release/?query=arid:${currentAlbum.id}&fmt=json")

            fragment.arguments = bundle
            context.findViewById<TextView>(R.id.page_title).text = "Les albums"
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
       holder.name?.text = currentAlbum.name
    }

    override fun getItemCount(): Int = artistList.size
}