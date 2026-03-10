package ru.practicum.android.diploma.ui.main

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import org.koin.android.ext.android.inject
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.ActivityMainBinding
import ru.practicum.android.diploma.domain.SettingsInteractor
import ru.practicum.android.diploma.domain.models.FilterSettings
import kotlin.getValue

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val settingsInteractor by inject<SettingsInteractor>()

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
        updateFilterButtonColor(destinationId)
    }

    private fun setupFilterButton() {
        binding.filterButton.setOnClickListener {
            navController.navigate(R.id.filtersFragment)
        }
    }

    private fun updateTitle(destinationId: Int) {
        updateTitleText(destinationId)
        updateTitleVisibility(destinationId)
    }

    private fun updateTitleText(destinationId: Int) {
        val titleResId = getTitleResource(destinationId)
        binding.titleText.setText(titleResId)
    }

    private fun getTitleResource(destinationId: Int): Int {
        return when (destinationId) {
            R.id.searchFragment -> R.string.job_search
            R.id.favoriteFragment -> R.string.favorites
            R.id.teamFragment -> R.string.team
            R.id.countryChooseFragment -> R.string.choosing_country
            R.id.regionChooseFragment -> R.string.choosing_region
            else -> R.string.app_name
        }
    }

    private fun updateTitleVisibility(destinationId: Int) {
        if (shouldHideTitle(destinationId)) {
            hideTitle()
        } else {
            showTitle()
        }
    }

    private fun shouldHideTitle(destinationId: Int): Boolean {
        return destinationId in listOf(
            R.id.vacancyFragment,
            R.id.favoriteFragment,
            R.id.filtersFragment,
            R.id.workChooseFragment,
            R.id.countryChooseFragment,
            R.id.regionChooseFragment,
            R.id.industryChooseFragment
        )
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

    fun updateFilterButtonColor(destinationId: Int) {

        if (destinationId != R.id.searchFragment) return

        val emptyFilter = FilterSettings(
            country = null,
            region = null,
            industry = null,
            salary = 0,
            onlyWithSalary = false,
        )
        val filterSettings = settingsInteractor.getFilterSettings()
        if (filterSettings == null || filterSettings == emptyFilter) {
            val typedValue = TypedValue()
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnPrimary,
                typedValue,
                true
            )
            binding.filterButton.imageTintList = ColorStateList.valueOf(typedValue.data)
        } else {
            binding.filterButton.imageTintList = ContextCompat.getColorStateList(this, R.color.blue)
        }
    }
}
