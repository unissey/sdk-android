package com.unissey.samplejavaapp.ui.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.unissey.samplejavaapp.R;
import com.unissey.samplejavaapp.databinding.FragmentDemoChoiceBinding;

public class DemoChoiceFragment extends Fragment {

    private FragmentDemoChoiceBinding binding;

    public DemoChoiceFragment() {
        super(R.layout.fragment_demo_choice);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentDemoChoiceBinding.bind(view);

        binding.uiDemoButton.setOnClickListener(v ->
                NavHostFragment.findNavController(DemoChoiceFragment.this)
                        .navigate(R.id.action_demoChoiceFragment_to_unisseyUiFragment)
        );

        binding.headlessDemoButton.setOnClickListener(v ->
                NavHostFragment.findNavController(DemoChoiceFragment.this)
                        .navigate(R.id.action_demoChoiceFragment_to_unisseyHeadlessFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
