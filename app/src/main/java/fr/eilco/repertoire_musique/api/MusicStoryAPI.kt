package fr.eilco.repertoire_musique.api




import fr.eilco.repertoire_musique.ArtistResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface MusicStoryAPI {
    @GET("/ws/2/artist")
    suspend fun searchArtists(
        @Query("query") query: String,
        @Query("fmt") fmt: String = "json"
    ): ArtistResponse

    companion object {
        fun create() : MusicStoryAPI {

            val client = OkHttpClient.Builder()
                .addNetworkInterceptor { chain ->
                    chain.proceed(
                        chain.request()
                            .newBuilder()
                            .header("User-Agent", "Repertoire de musique /1.0.0")
                            .build()
                    )
                }
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://musicbrainz.org")
                .build().create(MusicStoryAPI::class.java)
        }
    }

}