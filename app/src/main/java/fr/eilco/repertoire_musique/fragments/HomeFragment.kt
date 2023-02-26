package fr.eilco.repertoire_musique.fragments

import android.annotation.SuppressLint
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
import fr.eilco.repertoire_musique.*
import fr.eilco.repertoire_musique.adapter.AlbumAdapter
import fr.eilco.repertoire_musique.adapter.ArtisteAdapter
import fr.eilco.repertoire_musique.api.MusicStoryAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL

class HomeFragment(
    private val context: MainActivity
) : Fragment(){
    private lateinit var artistAdapter: ArtisteAdapter
    private lateinit var albumAdapter : AlbumAdapter
    private val artistList = arrayListOf<ArtisteModel>()
    private val album = arrayListOf<AlbumModel>()
    private var previousAlbumList: List<AlbumModel>? = null
    private lateinit var progressBar: ProgressBar
    val handler = Handler(Looper.getMainLooper())
    val albumListLiveData: MutableLiveData<List<AlbumModel>> = MutableLiveData()
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        progressBar = view?.findViewById(R.id.progress_bar2)!!
        artistAdapter = ArtisteAdapter(context, artistList, R.layout.artiste_horizontal)
        val horizontalRecycler = view.findViewById<RecyclerView>(R.id.horizontal_recycler)
        horizontalRecycler?.adapter = artistAdapter
        horizontalRecycler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        albumAdapter = AlbumAdapter(context, album, R.layout.album_vertical)
        val verticalRecycler = view.findViewById<RecyclerView>(R.id.vertical_recycler)
        verticalRecycler?.adapter = albumAdapter
        verticalRecycler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // Appel asynchrone de l'API MusicBrainz avec Retrofit et coroutines
        lifecycleScope.launch(Dispatchers.IO) {
             val service = MusicStoryAPI.create()

            val responseArtiste = service.searchArtists("type:person AND tag:pop AND country:US AND ended:false AND begin:[1990-01-01 TO 2022-01-01]")
            val artists = responseArtiste.artists
            artists.forEach { artist ->
                artistList.add(ArtisteModel(artist.name, artist.id))
            }
            withContext(Dispatchers.Main) {
                artistAdapter.notifyDataSetChanged() //notify the adapter that the data set has changed
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://musicbrainz.org/ws/2/release/?query=tag:pop AND date:2020 AND country:FR&fmt=json")
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
                        if (albums != previousAlbumList) {
                            handler.post {
                                progressBar.visibility = View.INVISIBLE
                            }
                            albumListLiveData.postValue(albums)
                            previousAlbumList = albums
                        }

                    }

                })

            }

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

}