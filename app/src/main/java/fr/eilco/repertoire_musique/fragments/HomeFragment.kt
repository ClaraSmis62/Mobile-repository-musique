package fr.eilco.repertoire_musique.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.eilco.repertoire_musique.AlbumModel
import fr.eilco.repertoire_musique.AlbumRepository.Singleton.albumList
import fr.eilco.repertoire_musique.MainActivity
import fr.eilco.repertoire_musique.R
import fr.eilco.repertoire_musique.adapter.ArtisteAdapter

class HomeFragment(
    private val context: MainActivity
) : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater?.inflate(R.layout.fragment_home, container, false)


        //Création de la liste d'albums à afficher




        val HorizontalRecycler = view?.findViewById<RecyclerView>(R.id.horizontal_recycler)
        if (HorizontalRecycler != null) {
            HorizontalRecycler.adapter = ArtisteAdapter(context, albumList, R.layout.artiste_horizontal)
        }

        val verticalRecycler = view?.findViewById<RecyclerView>(R.id.vertical_recycler)
        if (verticalRecycler != null) {
            verticalRecycler.adapter = ArtisteAdapter(context, albumList, R.layout.album_vertical)
        }

        return view
    }
}