package fr.eilco.repertoire_musique.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import fr.eilco.repertoire_musique.MainActivity
import fr.eilco.repertoire_musique.R
import fr.eilco.repertoire_musique.SongModel
import fr.eilco.repertoire_musique.adapter.SongAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SongFragment (
    private val context: MainActivity
    ): Fragment(){
        private lateinit var songAdapter : SongAdapter
        private val song = arrayListOf<SongModel>()
        val songListLiveData: MutableLiveData<List<SongModel>> = MutableLiveData()
        var requete : String = ""
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            arguments?.let {
                requete = it.getString(REQUEST).toString()
            }

            val view = inflater.inflate(R.layout.fragment_song,container,false)
            songAdapter = SongAdapter(context, song, R.layout.song_vertical)
            val verticalRecycler = view?.findViewById<RecyclerView>(R.id.song_list)
            verticalRecycler?.adapter = songAdapter
            verticalRecycler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            lifecycleScope.launch(Dispatchers.IO) {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(requete)
                    .header("User-Agent", "Repertoire de musique /1.0.0")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        GsonBuilder().create()
                        val recordingsJson = JSONObject(body).getJSONArray("recordings")
                        val songs = mutableListOf<SongModel>()
                        for (i in 0 until recordingsJson.length()) {
                            val recordingJson = recordingsJson.getJSONObject(i)
                            val id = recordingJson.getString("id")
                            val title = recordingJson.getString("title")
                            val artistCreditArray = recordingJson.getJSONArray("artist-credit")
                            var artistName = ""
                            for (j in 0 until artistCreditArray.length()) {
                                val artistObject = artistCreditArray.getJSONObject(j).getJSONObject("artist")
                                artistName = artistObject.getString("name")
                            }

                            songs.add(SongModel(title, id, artistName))

                        }
                        songListLiveData.postValue(songs)
                    }

                })

            }

            songListLiveData.observe(viewLifecycleOwner, Observer { songs ->
                song.clear()
                song.addAll(songs)
                songAdapter.notifyDataSetChanged()
            })


            return view
        }

    companion object {
        const val REQUEST :String = "request"
    }
}