package com.unissey.samplejavaapp.ui.fragments;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.unissey.samplejavaapp.R;
import com.unissey.samplejavaapp.databinding.FragmentVideoPlayerBinding;
import com.unissey.samplejavaapp.ui.SharedViewModel;

public class VideoPlayerFragment extends Fragment {

    private FragmentVideoPlayerBinding binding;

    public VideoPlayerFragment() {
        super(R.layout.fragment_video_player);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentVideoPlayerBinding.bind(view);
        SharedViewModel sharedViewModel =
                new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        binding.videoView.setVideoPath(sharedViewModel.getVideoUri().getValue());
        MediaController mediaController = new MediaController(requireContext());
        binding.videoView.setMediaController(mediaController);
        mediaController.setAnchorView(binding.videoView);
        binding.videoView.start();

        binding.restartButton.setOnClickListener(button -> {
            sharedViewModel.setVideoUri("");
            findNavController(requireView()).popBackStack(R.id.demoChoiceFragment, false);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
