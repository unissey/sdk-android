package com.unissey.samplelegacyapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.unissey.samplelegacyapp.R
import com.unissey.samplelegacyapp.databinding.VideoPlayerFragmentBinding

class VideoPlayerFragment : Fragment() {

    private var _binding: VideoPlayerFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = VideoPlayerFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        val sharedViewModel: SharedViewModel by activityViewModels()
        binding.videoView.setVideoPath(sharedViewModel.videoUri.value)
        val mediaController = MediaController(requireContext())
        binding.videoView.setMediaController(mediaController)
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.start()

        binding.restartButton.setOnClickListener {
            sharedViewModel.setVideoUri("")
            Navigation.findNavController(requireView()).setGraph(R.navigation.nav_graph)
        }

        return view
    }

}