package ru.practicum.android.diploma.ui.favorite
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FavoriteFragmentBinding
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.ui.vacancy.VacancyFragment

class FavoriteFragment : Fragment() {

    private val viewModel by viewModel<FavoriteViewModel>()
    private var _binding: FavoriteFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FakeVacanciesAdapter //нужно будет поменять на адаптер из SearchFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoriteFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        //нужно будет поменять на адаптер из SearchFragment
        adapter = FakeVacanciesAdapter { position ->
            findNavController().navigate(
                R.id.action_favoriteFragment_to_vacancyFragment,
                VacancyFragment.createArgs(adapter.vacancies[position].id)
            )
        }
        binding.rvVacancies.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateFavoriteVacancies()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showContent(vacancies: List<Vacancy>) {
        if (vacancies.isEmpty()) showEmptyList()
        else {
            binding.apply {

                adapter.vacancies.clear()
                adapter.vacancies.addAll(vacancies)
                adapter.notifyDataSetChanged()

                rvVacancies.isVisible = true
                groupEmpty.isVisible = false
                groupCouldntGetList.isVisible = false
                progressBar.isVisible = false
            }
        }
    }

    private fun showEmptyList() {
        binding.apply {
            rvVacancies.isVisible = false
            groupEmpty.isVisible = true
            groupCouldntGetList.isVisible = false
            progressBar.isVisible = false
        }
    }
    private fun showLoading() {
        binding.apply {
            rvVacancies.isVisible = false
            groupEmpty.isVisible = false
            groupCouldntGetList.isVisible = false
            progressBar.isVisible = true
        }
    }

    private fun showError() {
        binding.apply {
            rvVacancies.isVisible = false
            groupEmpty.isVisible = false
            groupCouldntGetList.isVisible = true
            progressBar.isVisible = false
        }
    }

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Loading -> showLoading()
            is FavoriteState.Error -> showError()
            is FavoriteState.Content -> showContent(state.vacancies)
        }
    }
}
