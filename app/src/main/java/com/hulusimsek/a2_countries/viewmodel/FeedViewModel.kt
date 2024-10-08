package com.hulusimsek.a2_countries.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hulusimsek.a2_countries.model.Country
import com.hulusimsek.a2_countries.service.CountryAPIService
import com.hulusimsek.a2_countries.service.CountryDatabase
import com.hulusimsek.a2_countries.util.CustomSharedPreferences
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : BaseViewModel(application) {

    private val countryApiService = CountryAPIService()
    private val disposable = CompositeDisposable()
    private val customPReferences = CustomSharedPreferences(getApplication())

    val countries = MutableLiveData<List<Country>>()
    val countryError = MutableLiveData<Boolean>()
    val countryLoading = MutableLiveData<Boolean>()
    private var refreshTime = 10 * 60 * 1000 * 1000 * 1000L

    fun refreshData() {
        countryLoading.value = true
        val updateTime = customPReferences.getTime()
        if (updateTime !=null && updateTime != 0L  && System.nanoTime() - updateTime < refreshTime) {
            getDataFromSQLite()
        }
        else {
            getDataFromAPI()
        }
        countryLoading.value = false

    }
    fun refreshFromAPI() {
        getDataFromAPI()
    }

    private  fun getDataFromSQLite() {
        launch {
            val countries = CountryDatabase(getApplication()).countryDao().getAllCountries()
            showCountries(countries)
            Toast.makeText(getApplication(),"Countries From SQLite", Toast.LENGTH_LONG).show()
        }
    }

    private  fun getDataFromAPI() {
        countryLoading.value = true
        disposable.add(countryApiService.getData()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<Country>>(){
                override fun onSuccess(t: List<Country>) {
                    storeInSQLite(t)
                    Toast.makeText(getApplication(),"Countries From API", Toast.LENGTH_LONG).show()
                }

                override fun onError(e: Throwable) {
                    countryError.value = true
                    countryLoading.value = false
                    e.printStackTrace()
                }

            })
        )
    }
    private fun showCountries(countryList : List<Country>) {
        countries.value = countryList
        countryError.value = false
        countryLoading.value = false
    }

    private fun storeInSQLite(list: List<Country>) {
        launch {
            val dao = CountryDatabase(getApplication()).countryDao()
            dao.deleteAllCountries()
            val listLong = dao.insertAll(*list.toTypedArray())
            var i = 0
            while (i<list.size) {
                list[i].uuid = listLong[i].toInt()
                i += 1
            }
            showCountries(list)
        }

        customPReferences.saveTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }




}