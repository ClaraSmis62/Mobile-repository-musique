package fr.eilco.repertoire_musique

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.eilco.repertoire_musique.AlbumRepository.Singleton.albumList
import fr.eilco.repertoire_musique.AlbumRepository.Singleton.databaseRef

class AlbumRepository {

    object Singleton {
        //se connecter à albums
        val databaseRef = FirebaseDatabase.getInstance().getReference("albums")

        //créer la liste
        val albumList = arrayListOf<AlbumModel>()
    }

    fun updateData(callback: () -> Unit){
        // absorber les données depuis la BDD pour ajouter dans la liste

         databaseRef.addValueEventListener(object : ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {

                 //retirer les anciens albums
                 albumList.clear()
                 // récupérer la liste
                 for(ds in snapshot.children)
                 {
                     //construire un objet album
                     val album = ds.getValue(AlbumModel::class.java)

                     //verification si c'est null ou non
                     if(album != null)
                     {
                         albumList.add(album)
                     }
                 }

                 //actionner callback

                 callback()
             }

             override fun onCancelled(error: DatabaseError) {}

         })

    }


}