package ru.practicum.android.diploma.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()
        setupFilterButton()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.searchFragment -> {
                    navController.navigate(R.id.searchFragment)
                }

                R.id.favoriteFragment -> {
                    navController.navigate(R.id.favoriteFragment)
                }

                R.id.teamFragment -> {
                    navController.navigate(R.id.teamFragment)
                }
            }
            true
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateUiForDestination(destination.id)
        }
    }

    private fun updateUiForDestination(destinationId: Int) {
        updateTitle(destinationId)
        updateFilterButtonVisibility(destinationId)
    }

    private fun setupFilterButton() {
        binding.filterButton.setOnClickListener {
            navController.navigate(R.id.filtersFragment)
        }
    }

    private fun updateTitle(destinationId: Int) {
        val titleResId = when (destinationId) {
            R.id.searchFragment -> R.string.job_search
            R.id.favoriteFragment -> R.string.favorites
            R.id.teamFragment -> R.string.team
            R.id.filtersFragment -> R.string.filter_title
            R.id.branchChooseFragment -> R.string.choosing_an_industry
            R.id.workChooseFragment -> R.string.choosing_a_place_of_work
            R.id.countryChooseFragment -> R.string.choosing_country
            R.id.regionChooseFragment -> R.string.choosing_region
            else -> {
                R.string.app_name
            }
        }
        when (destinationId) {
            R.id.vacancyFragment -> hideTitle()
            R.id.favoriteFragment -> hideTitle()
            else -> showTitle()
        }
        binding.titleText.setText(titleResId)
    }

    private fun hideTitle() {
        binding.titleText.visibility = android.view.View.GONE
    }

    private fun showTitle() {
        binding.titleText.visibility = android.view.View.VISIBLE
    }

    private fun updateFilterButtonVisibility(destinationId: Int) {
        binding.filterButton.visibility = if (destinationId == R.id.searchFragment) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
    }
}
