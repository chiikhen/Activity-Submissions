package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class NowPlayingFragment : Fragment(), MusicListFragment.OnMusicInteractionListener {

    private val musicList = listOf(
        Music("Blinding Lights", "The Weeknd", "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/17/b4/8f/17b48f9a-0b93-6bb8-fe1d-3a16623c2cfb/mzaf_9560252727299052414.plus.aac.p.m4a"),
        Music("Shape of You", "Ed Sheeran", "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/44/c7/4f/44c74f0d-72dc-6143-d4d0-ba14d661ca0d/mzaf_9566898362556366703.plus.aac.p.m4a"),
        Music("Levitating", "Dua Lipa", "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/59/dc/4d/59dc4dda-93ff-8f1c-c536-f005f6ea6af5/mzaf_3066686759813252385.plus.aac.p.m4a"),
        Music("Anti-Hero", "Taylor Swift", "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/1d/56/2a/1d562a07-dc5f-a9c0-1f36-2051a8c14eb7/mzaf_7214829135431340590.plus.aac.p.m4a"),
        Music("As It Was", "Harry Styles", "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/67/10/16/67101606-3869-ca44-6c03-e13d6322cb51/mzaf_1135399237022217274.plus.aac.p.m4a"),
        Music("Flowers", "Miley Cyrus", "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/68/9e/f7/689ef7fe-14fe-a846-c87f-7d3b2d6344b1/mzaf_4167137058064023087.plus.aac.p.m4a"),
        Music("Starboy", "The Weeknd", "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/3f/a0/ba/3fa0ba5b-088d-bcf2-e4bd-355a5d505617/mzaf_3355567893400963384.plus.aac.p.m4a"),
        Music("Peaches", "Justin Bieber", "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/c9/6d/b1/c96db138-df15-d3d1-ef9d-98ef9d350960/mzaf_9411021956242812289.plus.aac.p.m4a"),
        Music("Stay", "The Kid LAROI & Justin Bieber", "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/d7/4a/84/d74a84d5-9afa-761e-b632-baab55c2a23b/mzaf_11865500880477235553.plus.aac.p.m4a"),
        Music("Watermelon Sugar", "Harry Styles", "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview116/v4/16/86/f5/1686f50d-8b77-7e32-85f7-5f0e804d68fe/mzaf_14195633304344507287.plus.aac.p.m4a")
    )

    private val favoritesList = mutableListOf<Music>()
    private val favoritedUrls = mutableSetOf<String>()

    private var currentIndex = -1
    private var activeList: List<Music> = musicList
    private var musicPlayerFragment: MusicPlayerFragment? = null
    private var currentTab = R.id.tab_my_music

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt("currentIndex", -1)
            currentTab = savedInstanceState.getInt("currentTab", R.id.tab_my_music)

            // Restore favorites
            val savedFavUrls = savedInstanceState.getStringArrayList("favoritedUrls")
            if (savedFavUrls != null) {
                favoritedUrls.addAll(savedFavUrls)
                // Rebuild favorites list from the full music list
                for (music in musicList) {
                    if (music.url in favoritedUrls) {
                        favoritesList.add(music)
                    }
                }
            }

            activeList = if (currentTab == R.id.tab_favorites) favoritesList else musicList
        }

        // Set up the player fragment (always visible)
        if (savedInstanceState == null) {
            musicPlayerFragment = MusicPlayerFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.playerFragmentContainer, musicPlayerFragment!!)
                .commit()
        } else {
            musicPlayerFragment = childFragmentManager
                .findFragmentById(R.id.playerFragmentContainer) as? MusicPlayerFragment
        }

        // Set up the bottom navigation
        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = currentTab

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_my_music -> {
                    currentTab = R.id.tab_my_music
                    activeList = musicList
                    showMyMusicTab()
                    true
                }
                R.id.tab_favorites -> {
                    currentTab = R.id.tab_favorites
                    activeList = favoritesList
                    showFavoritesTab()
                    true
                }
                else -> false
            }
        }

        // Show initial tab
        if (savedInstanceState == null) {
            showMyMusicTab()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentIndex", currentIndex)
        outState.putInt("currentTab", currentTab)
        outState.putStringArrayList("favoritedUrls", ArrayList(favoritedUrls))
    }

    private fun showMyMusicTab() {
        val fragment = MusicListFragment().also {
            it.setMusicList(musicList)
            it.setFavoritedUrls(favoritedUrls)
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.tabContentContainer, fragment)
            .commit()
    }

    private fun showFavoritesTab() {
        val fragment = FavoritesFragment().also {
            it.setFavoritesList(favoritesList.toList())
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.tabContentContainer, fragment)
            .commit()
    }

    // --- OnMusicInteractionListener implementation ---

    override fun onMusicSelected(music: Music, position: Int) {
        currentIndex = position
        musicPlayerFragment?.playMusic(music)
    }

    override fun onNextRequested() {
        if (activeList.isEmpty()) return
        currentIndex = (currentIndex + 1) % activeList.size
        musicPlayerFragment?.playMusic(activeList[currentIndex])
    }

    override fun onPreviousRequested() {
        if (activeList.isEmpty()) return
        currentIndex = if (currentIndex - 1 < 0) activeList.size - 1 else currentIndex - 1
        musicPlayerFragment?.playMusic(activeList[currentIndex])
    }

    override fun onFavoriteToggled(music: Music) {
        if (music.url in favoritedUrls) {
            // Remove from favorites
            favoritedUrls.remove(music.url)
            favoritesList.removeAll { it.url == music.url }
        } else {
            // Add to favorites
            favoritedUrls.add(music.url)
            favoritesList.add(music)
        }

        // Refresh current tab to update the UI
        if (currentTab == R.id.tab_my_music) {
            // Update heart icons in the music list
            val musicListFrag = childFragmentManager.findFragmentById(R.id.tabContentContainer)
            if (musicListFrag is MusicListFragment) {
                musicListFrag.setFavoritedUrls(favoritedUrls)
            }
        } else if (currentTab == R.id.tab_favorites) {
            // Refresh the favorites list
            showFavoritesTab()
        }
    }
}
