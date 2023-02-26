package fr.eilco.repertoire_musique

import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.eilco.repertoire_musique.fragments.CollectionFragment
import fr.eilco.repertoire_musique.fragments.HomeFragment
import fr.eilco.repertoire_musique.fragments.SongFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        //Enlever la barre de titre
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //charger le repository album
        AlbumRepository()
       /* repo.updateData{*/
        //mettre à jour la liste
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, HomeFragment(this), "Les artistes")
            transaction.addToBackStack(null)
            transaction.commit()

      /*  }*/
        val navigationView=findViewById<BottomNavigationView>(R.id.design_navigation_view)
        navigationView.setOnNavigationItemSelectedListener{
              when(it.itemId){
                  R.id.home_page -> {
                      loadFragment(HomeFragment(this), "Les artistes")
                      return@setOnNavigationItemSelectedListener true
                  }
                  R.id.album_page -> {
                      val fragment = CollectionFragment(this).apply {
                          arguments = bundleOf(
                              CollectionFragment.REQUEST to "https://musicbrainz.org/ws/2/release/?query=tag:pop AND date:2021 AND country:FR&fmt=json"
                          )
                      }
                      loadFragment(fragment, "Les albums")
                      return@setOnNavigationItemSelectedListener true
                  }
                  R.id.song_page -> {
                      val fragment = SongFragment(this).apply {
                          arguments = bundleOf(
                              SongFragment.REQUEST to "https://musicbrainz.org/ws/2/recording/?query=firstreleasedate:[2020 TO 2022] AND tag:pop AND country:FR&fmt=json"
                          )
                      }
                      loadFragment(fragment, "Les chansons")
                      return@setOnNavigationItemSelectedListener true
                  }
                  else -> false

              }
        }


    }
    private fun loadFragment(fragment:Fragment, string: String){
        findViewById<TextView>(R.id.page_title).text = string
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }

    }