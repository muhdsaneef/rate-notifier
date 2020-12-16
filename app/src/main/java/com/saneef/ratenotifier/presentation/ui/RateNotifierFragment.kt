package com.saneef.ratenotifier.presentation.ui

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.saneef.ratenotifier.R
import com.saneef.ratenotifier.app.RateNotifierApplication
import com.saneef.ratenotifier.databinding.RateNotifierFragmentBinding
import com.saneef.ratenotifier.di.DaggerAppComponent
import com.saneef.ratenotifier.presentation.RateNotifierViewModel
import com.saneef.ratenotifier.presentation.UiState
import javax.inject.Inject

class RateNotifierFragment : Fragment() {

    companion object {
        fun newInstance() = RateNotifierFragment()
    }

    @Inject
    lateinit var viewModel: RateNotifierViewModel
    private var _binding: RateNotifierFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RateNotifierFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.loadExchangeRate()

        observeViewModelChanges()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpdateButtonClickListener()
    }

    private fun setUpdateButtonClickListener() {
        binding.fetchRateButton.setOnClickListener {
            viewModel.loadExchangeRate()
        }
    }

    private fun observeViewModelChanges() {
        with(viewModel) {
            exchangeRateViewState.observe(this@RateNotifierFragment, {
                binding.rateTextView.text = it.toString()
            })

            uiViewState.observe(this@RateNotifierFragment, {
                binding.indeterminateBar.isVisible = it == UiState.LOADING
                if (it != UiState.LOADING) {
                    showToast(it)
                }
            })
        }
    }

    private fun showToast(uiState: UiState?) {
        val message: Int = if (uiState == UiState.SUCCESS) {
            R.string.message_loaded
        } else {
            R.string.message_failed
        }

        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as RateNotifierApplication).appComponent.inject(this)
    }
}
