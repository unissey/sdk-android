package com.unissey.samplelegacyapp

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.unissey.samplelegacyapp.databinding.ActivityMainBinding
import com.unissey.samplelegacyapp.ui.fragments.UnisseyUiFragment

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    as NavHostFragment

        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.demoChoiceFragment))

        onBackPressedDispatcher.addCallback(this) {
            val currentFragment =
                navHostFragment.childFragmentManager.primaryNavigationFragment

            if (currentFragment is UnisseyUiFragment && currentFragment.navigateUp()) {
                return@addCallback
            }

            if (!navController.navigateUp(appBarConfiguration)) {
                finish()
            }
        }

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

}