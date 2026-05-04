package com.example.exercise_3_fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navView: NavigationView
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    companion object {
        private const val TAG_MUSIC = "music_host"
        private const val TAG_FAVORITES = "favorites"
        private const val TAG_PROFILE = "profile"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        bottomNav = findViewById(R.id.bottom_nav)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.drawer_now_playing -> {
                    showMusicHost()
                    bottomNav.visibility = View.VISIBLE
                    bottomNav.selectedItemId = R.id.navigation_my_music
                    item.isChecked = true
                }
                R.id.drawer_profile -> {
                    showProfile()
                    item.isChecked = true
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_my_music -> {
                    showMusicHost()
                    true
                }
                R.id.navigation_favorites -> {
                    showFavorites()
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            navView.setCheckedItem(R.id.drawer_now_playing)
            bottomNav.selectedItemId = R.id.navigation_my_music
            showMusicHost()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showMusicHost() {
        replaceFragment(MusicHostFragment(), TAG_MUSIC)
        toolbar.title = "My Music"
        bottomNav.visibility = View.VISIBLE
    }

    private fun showFavorites() {
        replaceFragment(FavoritesFragment(), TAG_FAVORITES)
        toolbar.title = "Favorites"
        bottomNav.visibility = View.VISIBLE
    }

    private fun showProfile() {
        replaceFragment(ProfileFragment(), TAG_PROFILE)
        toolbar.title = "Profile"
        bottomNav.visibility = View.GONE
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }
}