package fr.eilco.repertoire_musique.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import fr.eilco.repertoire_musique.AlbumModel
import fr.eilco.repertoire_musique.MainActivity
import fr.eilco.repertoire_musique.R
import fr.eilco.repertoire_musique.adapter.AlbumAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL


class CollectionFragment (
    private val context: MainActivity
        ): Fragment(){
    private lateinit var albumAdapter : AlbumAdapter
    private lateinit var progressBar: ProgressBar
    private val album = arrayListOf<AlbumModel>()
    val handler = Handler(Looper.getMainLooper())
    val albumListLiveData: MutableLiveData<List<AlbumModel>> = MutableLiveData()
    var requete : String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        arguments?.let {
            requete = it.getString(SongFragment.REQUEST).toString()
        }
       val view = inflater.inflate(R.layout.fragment_collection,container,false)
        albumAdapter = AlbumAdapter(context, album, R.layout.album_vertical)
        val verticalRecycler = view?.findViewById<RecyclerView>(R.id.collection_recycler_list)
        verticalRecycler?.adapter = albumAdapter
        verticalRecycler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        progressBar = view?.findViewById(R.id.progress_bar)!!

        lifecycleScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(requete)
                .header("User-Agent", "Repertoire de musique /1.0.0")
                .build()
            progressBar.visibility = View.VISIBLE
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {

                    val body = response.body?.string()
                    val releasesJson = JSONObject(body).getJSONArray("releases")
                    val albums = mutableListOf<AlbumModel>()

                    for (i in 0 until 10) {
                        val releaseJson = releasesJson.getJSONObject(i)
                        val id = releaseJson.getString("id")
                        val title = releaseJson.getString("title")
                        val date: String? = if (releaseJson.has("date")) {
                            releaseJson.getString("date")

                        } else {
                            "Date inconnue"
                        }
                        val imageUrl: String? = try {
                            getArtistImage(id)
                            // utiliser imageUrl
                        } catch (e: FileNotFoundException) {
                            // gérer l'erreur de fichier non trouvé
                            ""
                        }

                        albums.add(AlbumModel(title, date, imageUrl, id))

                    }

                    handler.post {
                        progressBar.visibility = View.INVISIBLE
                    }
                    albumListLiveData.postValue(albums)

                }

            })

        }

        albumListLiveData.observe(viewLifecycleOwner, Observer { albums ->
            album.clear()
            album.addAll(albums)
            albumAdapter.notifyDataSetChanged()
        })


        return view
    }

    fun getArtistImage(artistMbid: String?): String? {
        val url = "https://coverartarchive.org/release/$artistMbid"
        val response: String?
        try {
            response = URL(url).readText()
        }
        catch (e: FileNotFoundException)
        {
            return null
        }

        val json = JSONObject(response)
        val images = json.getJSONArray("images")
        return if (images.length() > 0) {
            images.getJSONObject(0).getString("image")
        } else {
            null
        }
    }

    companion object {
        const val REQUEST :String = "request"
    }
}