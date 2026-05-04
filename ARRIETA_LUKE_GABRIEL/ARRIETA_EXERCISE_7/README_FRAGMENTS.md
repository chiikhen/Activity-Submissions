# Music Player - Fragment-Based Architecture

## Overview
This music player application has been refactored from an Activity-based architecture to a Fragment-based architecture. The app now displays two fragments in a single MainActivity:
1. **ListFragment** - Shows the list of available songs
2. **PlayerFragment** - Shows the currently playing song with playback controls

## Key Features Implemented

### ✅ 1. Fragment Architecture
- **ListFragment**: Displays a scrollable list of songs with artist information
- **PlayerFragment**: Shows now-playing information and media controls
- Both fragments are hosted in MainActivity

### ✅ 2. Interface Communication
- **MusicPlayerInterface**: Defines methods for fragment-to-activity communication
  - `onSongSelected(position: Int)` - Called when user selects a song from the list
  - `onNextClicked()` - Called when user clicks the Next button
  - `onPreviousClicked()` - Called when user clicks the Previous button

### ✅ 3. Music Playback Controls
- **Play** - Start/resume playback
- **Pause** - Pause playback
- **Stop** - Stop and reset to beginning
- **Previous** - Play previous song in playlist
- **Next** - Play next song in playlist

### ✅ 4. Additional Features
- **SeekBar** - Visual progress indicator with seek functionality
- **Time Display** - Shows current time and total duration
- **Auto-play** - Automatically starts playback when a song is selected
- **Auto-next** - Automatically plays next song when current song ends
- **Status Display** - Shows current state (Playing, Paused, Buffering, etc.)

## File Structure

### New Files Created:
1. **MusicPlayerInterface.kt** - Interface for fragment communication
2. **ListFragment.kt** - Fragment displaying song list
3. **PlayerFragment.kt** - Fragment displaying player controls
4. **fragment_list.xml** - Layout for ListFragment
5. **fragment_player.xml** - Layout for PlayerFragment

### Modified Files:
1. **MainActivity.kt** - Now hosts fragments and implements MusicPlayerInterface
2. **activity_main.xml** - Updated to contain fragment containers

### Existing Files (Unchanged):
- Song.kt
- SongAdapter.kt
- song_item.xml
- colors.xml
- ManageSong.kt (not used anymore but kept for reference)

## How It Works

### 1. Song Selection Flow:
```
User clicks song in ListFragment
    ↓
ListFragment.onItemClickListener triggers
    ↓
Calls listener.onSongSelected(position)
    ↓
MainActivity.onSongSelected() receives callback
    ↓
MainActivity loads song using ExoPlayer
    ↓
PlayerFragment updates UI with song info
```

### 2. Previous/Next Navigation Flow:
```
User clicks Previous/Next button
    ↓
PlayerFragment button click listener triggers
    ↓
Calls listener.onPreviousClicked() or onNextClicked()
    ↓
MainActivity updates currentSongPosition
    ↓
MainActivity loads new song
    ↓
PlayerFragment updates UI
```

## Technical Details

### MediaPlayer Implementation:
- Uses **ExoPlayer** (Media3) for audio playback
- Supports streaming from URLs
- Handles buffering states
- Auto-resumes on app return

### Fragment Communication:
- Fragments communicate with MainActivity through the MusicPlayerInterface
- MainActivity holds reference to PlayerFragment for UI updates
- ListFragment gets song list through MainActivity.getSongs()

### State Management:
- `currentSongPosition` tracks which song is playing
- `wasPlaying` tracks playback state for lifecycle management
- `isUserSeeking` prevents SeekBar conflicts during user interaction

## Song List
The app includes 8 pre-configured songs:
1. SoundHelix Song 1-3 (test audio)
2. Love Me Not - Ravyn Lenae
3. Angeleyes - ABBA
4. Tensionado - Soapdish
5. I love you so - The Walters
6. Ang Pag-ibig ay Kanibalismo - fitterkarma

## UI Design
- **Dark Theme** - Spotify-inspired color palette
- **Card-based Song Items** - Clean, modern look
- **Clear Visual Hierarchy** - Easy to read and navigate
- **Responsive Controls** - Large touch targets for buttons

## Building the Project

### To build and run:
1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or physical device

### Requirements:
- Android Studio (latest version)
- Android SDK 24+
- Internet connection (for streaming songs)

## Next Steps / Future Enhancements
- Add playlist management
- Implement shuffle and repeat modes
- Add favorites/liked songs
- Include album art display
- Add search/filter functionality
- Implement offline playback with downloaded songs
- Add equalizer controls

## Notes
- The old ManageSong Activity is no longer used but kept in the codebase
- All playback logic now resides in MainActivity
- Fragments are lightweight and focus on UI only
- The app maintains playback state across configuration changes

---

**Created for**: CIT238 Mobile Applications Development
**Exercise**: Fragment-Based Music Player Implementation
