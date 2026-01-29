package com.unissey.samplejavaapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.unissey.samplejavaapp.databinding.ActivityMainBinding;
import com.unissey.samplejavaapp.ui.fragments.UnisseyUiFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found");
        }

        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.demoChoiceFragment
        ).build();

        navController.addOnDestinationChangedListener(
                (controller, destination, arguments) -> {
                    if (destination.getId() == R.id.unisseyUiFragment) {
                        binding.toolbar.setNavigationOnClickListener(v -> {
                            NavHostFragment innerNavHost = (NavHostFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.nav_host_fragment);
                            if (innerNavHost == null) return;

                            Fragment currentFragment = innerNavHost.getChildFragmentManager()
                                    .getPrimaryNavigationFragment();

                            if (currentFragment instanceof UnisseyUiFragment) {
                                UnisseyUiFragment unisseyUiFragment = (UnisseyUiFragment) currentFragment;
                                if (!unisseyUiFragment.navigateUp()) {
                                    NavigationUI.navigateUp(navController, appBarConfiguration);
                                }
                            } else {
                                NavigationUI.navigateUp(navController, appBarConfiguration);
                            }
                        });
                    } else {
                        binding.toolbar.setNavigationOnClickListener(v ->
                                NavigationUI.navigateUp(navController, appBarConfiguration)
                        );
                    }
                }
        );

        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
    }
}