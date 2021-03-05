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
import android.view.animation.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.saneef.ratenotifier.R
import com.saneef.ratenotifier.app.RateNotifierApplication
import com.saneef.ratenotifier.databinding.RateNotifierFragmentBinding
import com.saneef.ratenotifier.di.DaggerAppComponent
import com.saneef.ratenotifier.presentation.AnimationAction
import com.saneef.ratenotifier.presentation.RateNotifierViewModel
import com.saneef.ratenotifier.presentation.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Inject

class RateNotifierFragment : Fragment() {

    @Inject
    lateinit var viewModel: RateNotifierViewModel
    private var _binding: RateNotifierFragmentBinding? = null
    private val binding get() = _binding!!

    private var animation: AlphaAnimation? = null

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

        //viewModel.loadExchangeRate()

        observeViewModelChanges()
        observeInputChangeListeners()
    }

    private fun observeInputChangeListeners() {
        binding.rateDetailsCard.sourceCurrencyEditText.addTextChangedListener {
            viewModel.sourceCurrency.postValue(it.toString())
        }

        binding.rateDetailsCard.targetCurrencyEditText.addTextChangedListener {
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

        binding.rateDetailsCard.refreshImageButton.setOnClickListener {
            viewModel.loadExchangeRate()
        }

        binding.rateDetailsCard.autoUpdateCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onAutoUpdateCheckChanged(isChecked)
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

    @ExperimentalCoroutinesApi
    private fun observeViewModelChanges() {
        with(viewModel) {
            exchangeRateViewState.observe(viewLifecycleOwner, {
                binding.rateTextView.text =
                    getString(R.string.label_exchange_rate, it.rate.toString())
                binding.rateDetailsCard.currentRateTextView.text = it.rate.toString()
            })

            uiViewState.observe(viewLifecycleOwner, {
                binding.indeterminateBar.isVisible = it == UiState.LOADING
                if (it != UiState.LOADING) {
                    showToast(it)
                }
            })

            amountViewState.observe(viewLifecycleOwner, {
                binding.convertedRateTextView.text = it
            })

            storedExchangeRates.observe(viewLifecycleOwner, {
                it.forEach { conversionRateUiModel -> Timber.d(conversionRateUiModel.formattedDateTime) }
            })
        }

        lifecycleScope.launchWhenStarted {
            viewModel.newValueSignalReceiver.collect { action ->
                if (action == AnimationAction.Stop) {
                    animation?.cancel()
                } else {
                    animateRateView()
                }
            }
        }
    }

    private fun animateRateView() {
        animation = AlphaAnimation(INITIAL_ALPHA, FINAL_ALPHA)
        animation?.duration = ALPHA_ANIMATION_DURATION
        animation?.interpolator = LinearInterpolator()
        animation?.repeatMode = Animation.REVERSE
        animation?.repeatCount = Animation.INFINITE
        binding.rateDetailsCard.currentRateTextView.startAnimation(animation)
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

    companion object {
        fun newInstance() = RateNotifierFragment()

        private const val INITIAL_ALPHA = 0.2f
        private const val FINAL_ALPHA = 1f
        private const val ALPHA_ANIMATION_DURATION = 1000L
    }
}
