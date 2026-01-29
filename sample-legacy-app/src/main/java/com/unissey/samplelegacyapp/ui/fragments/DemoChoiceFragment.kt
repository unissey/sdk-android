package com.unissey.samplelegacyapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.unissey.samplelegacyapp.R
import com.unissey.samplelegacyapp.databinding.FragmentDemoChoiceBinding

class DemoChoiceFragment : Fragment(R.layout.fragment_demo_choice) {

    private var _binding: FragmentDemoChoiceBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDemoChoiceBinding.bind(view)

        binding.uiDemoButton.setOnClickListener {
            findNavController().navigate(R.id.action_demoChoiceFragment_to_unisseyUiFragment)
        }

        binding.headlessDemoButton.setOnClickListener {
            findNavController().navigate(R.id.action_demoChoiceFragment_to_unisseyHeadlessFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}