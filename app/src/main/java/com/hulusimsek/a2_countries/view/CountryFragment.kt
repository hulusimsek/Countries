package com.hulusimsek.a2_countries.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hulusimsek.a2_countries.databinding.FragmentCountryBinding
import com.hulusimsek.a2_countries.util.downloadFromUrl
import com.hulusimsek.a2_countries.util.placeholderProgressBar
import com.hulusimsek.a2_countries.viewmodel.CountryViewModel

class CountryFragment : Fragment() {

    private lateinit var viewModel: CountryViewModel
    private var countryUuid = 0
    private var _binding: FragmentCountryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCountryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            countryUuid = CountryFragmentArgs.fromBundle(it).countryUuid
        }

        viewModel = ViewModelProvider(this)[CountryViewModel::class.java]
        viewModel.getDataFromRoom(countryUuid)



        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.countryLiveData.observe(viewLifecycleOwner, Observer { country ->
            binding.countryName.text = country.countryName
            binding.countryCapital.text = country.countryCapital
            binding.countryCurrency.text = country.countryCurrency
            binding.countryLanguage.text = country.countryLanguage
            binding.countryRegion.text = country.countryRegion

            binding.countryImage.downloadFromUrl(country.imageUrl, placeholderProgressBar(binding.countryImage.context))


        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
