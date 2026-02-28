package ru.practicum.android.diploma.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.SearchFragmentBinding

class SearchFragment : Fragment(R.layout.search_fragment) {

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()
                val endDrawable = if (hasText) R.drawable.ic_clear else R.drawable.ic_search
                binding.searchEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, endDrawable, 0
                )
            }
        })

        binding.searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val d = binding.searchEditText.compoundDrawables[2] ?: return@setOnTouchListener false
                val endIconLeft = v.width - v.paddingEnd - d.intrinsicWidth
                if (event.x >= endIconLeft) {
                    binding.searchEditText.text?.clear()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
