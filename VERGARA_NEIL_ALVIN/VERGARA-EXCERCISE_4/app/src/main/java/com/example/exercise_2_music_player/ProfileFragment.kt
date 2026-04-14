package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.nameText).text = "NEIL VERGARA"
        view.findViewById<TextView>(R.id.courseText).text = "Course: CIT 238"
        view.findViewById<TextView>(R.id.sectionText).text = "Section: B- ST"
        view.findViewById<TextView>(R.id.hobbiesText).text = "Hobbies: Music, Coding, Film"
    }
}