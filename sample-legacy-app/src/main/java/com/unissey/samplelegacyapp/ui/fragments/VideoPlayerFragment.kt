package com.unissey.samplelegacyapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.unissey.samplelegacyapp.R
import com.unissey.samplelegacyapp.databinding.FragmentVideoPlayerBinding
import com.unissey.samplelegacyapp.ui.SharedViewModel

class VideoPlayerFragment : Fragment(R.layout.fragment_video_player) {

    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVideoPlayerBinding.bind(view)

        val sharedViewModel: SharedViewModel by activityViewModels()
        binding.videoView.setVideoPath(sharedViewModel.videoUri.value)

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)
        binding.videoView.start()

        binding.restartButton.setOnClickListener {
            sharedViewModel.setVideoUri("")
            findNavController().popBackStack(R.id.demoChoiceFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}