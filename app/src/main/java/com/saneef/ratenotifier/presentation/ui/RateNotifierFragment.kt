package com.saneef.ratenotifier.presentation.ui

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
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
        _binding = DataBindingUtil.inflate(inflater, R.layout.rate_notifier_fragment, container, false)
        binding.lifecycleOwner = this

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
        observeInputChangeListeners()
    }

    private fun observeInputChangeListeners() {
        binding.sourceCurrencyEditText.addTextChangedListener {
            viewModel.sourceCurrency.postValue(it.toString())
        }

        binding.targetCurrencyEditText.addTextChangedListener {
            viewModel.targetCurrency.postValue(it.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonClickListener()
    }

    private fun setButtonClickListener() {
        binding.fetchRateButton.setOnClickListener {
            viewModel.loadExchangeRate()
        }

        binding.scanButton.setOnClickListener {
            startActivityForResult(Intent(requireContext(), ScannerActivity::class.java), 9001)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9001) {
            if (resultCode == RESULT_OK) {
                data?.run {
                    if (hasExtra("text")) {
                        viewModel.convertAmount(getDoubleExtra("text", 0.0))
                    }
                }
            }
        }
    }

    private fun observeViewModelChanges() {
        with(viewModel) {
            exchangeRateViewState.observe(this@RateNotifierFragment, {
                binding.rateTextView.text = getString(R.string.label_exchange_rate, it.toString())
            })

            uiViewState.observe(this@RateNotifierFragment, {
                binding.indeterminateBar.isVisible = it == UiState.LOADING
                if (it != UiState.LOADING) {
                    showToast(it)
                }
            })

            amountViewState.observe(this@RateNotifierFragment, {
                binding.convertedRateTextView.text = it
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
