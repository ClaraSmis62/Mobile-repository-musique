package fr.eilco.repertoire_musique

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.eilco.repertoire_musique.fragments.CollectionFragment
import fr.eilco.repertoire_musique.fragments.HomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        //Enlever la barre de titre
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //charger le repository album
        val repo = AlbumRepository()
        repo.updateDate{
        //mettre à jour la liste
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, HomeFragment(this))
            transaction.addToBackStack(null)
            transaction.commitAllowingStateLoss();

        }
        val navigationView=findViewById<BottomNavigationView>(R.id.design_navigation_view)
        navigationView.setOnNavigationItemSelectedListener{
              when(it.itemId){
                  R.id.home_page -> {
                      loadFragment(HomeFragment(this))
                      return@setOnNavigationItemSelectedListener true
                  }
                  R.id.album_page -> {
                      loadFragment(CollectionFragment(this))
                      return@setOnNavigationItemSelectedListener true
                  }
                  //song page
                  else -> false

              }
        }


    }
    private fun loadFragment(fragment:Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}