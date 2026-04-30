package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileImage = view.findViewById<ImageView>(R.id.profileImage)
        val nameText = view.findViewById<TextView>(R.id.nameText)
        val courseText = view.findViewById<TextView>(R.id.courseText)
        val sectionText = view.findViewById<TextView>(R.id.sectionText)
        val hobbiesText = view.findViewById<TextView>(R.id.hobbiesText)

        // Set profile information
        profileImage.setImageResource(R.drawable.ic_profile_placeholder)
        nameText.text = "Your Full Name"
        courseText.text = "Course: Bachelor of Science in Information Technology"
        sectionText.text = "Section: BSIT-2A"
        hobbiesText.text = "Hobbies: Music Production, Gaming, Reading, Coding"
    }
}