package com.unissey.samplejavaapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.unissey.samplejavaapp.R;
import com.unissey.samplejavaapp.databinding.VideoPlayerFragmentBinding;

public class VideoPlayerFragment extends Fragment {

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        VideoPlayerFragmentBinding binding =
                VideoPlayerFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        SharedViewModel sharedViewModel =
                new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        binding.videoView.setVideoPath(sharedViewModel.getVideoUri().getValue());
        MediaController mediaController = new MediaController(requireContext());
        binding.videoView.setMediaController(mediaController);
        mediaController.setAnchorView(binding.videoView);
        binding.videoView.start();

        binding.restartButton.setOnClickListener(button -> {
            sharedViewModel.setVideoUri("");
            Navigation.findNavController(requireView()).setGraph(R.navigation.nav_graph);
        });

        return view;
    }
}
