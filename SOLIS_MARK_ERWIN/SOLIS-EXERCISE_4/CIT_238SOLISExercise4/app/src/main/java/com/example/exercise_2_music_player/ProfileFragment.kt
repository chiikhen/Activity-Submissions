package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.exercise_2_music_player.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set profile details
        binding.fullNameText.text = "Mark Erwin T. Solis"
        binding.courseText.text = "Course: BSIT"
        binding.sectionText.text = "Section: 3A"
        binding.hobbiesText.text = "Hobbies: Watching Movies, Music, Gaming, Reading"
    }

    override fun onDestroyView() {
        super.onDetach()
        _binding = null
    }
}