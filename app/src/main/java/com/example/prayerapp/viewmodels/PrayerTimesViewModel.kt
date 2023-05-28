package com.example.prayerapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prayerapp.domain.models.Data
import com.example.prayerapp.domain.models.PrayersApiResponse
import com.example.prayerapp.domain.models.PrayersTiming
import com.example.prayerapp.domain.models.Timings
import com.example.prayerapp.domain.usecase.GetPrayersTimingsFromDB
import com.example.prayerapp.domain.usecase.GetPrayersTimingsFromRemote
import com.example.prayerapp.domain.usecase.InsertPrayersTimings
import com.example.prayerapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PrayerTimesViewModel"

@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    private val getPrayersTimingsFromRemoteUseCase: GetPrayersTimingsFromRemote,
    private val insertPrayersTimingsUseCase: InsertPrayersTimings,
    private val getPrayersTimingsFromDbUseCase: GetPrayersTimingsFromDB,
) :
    ViewModel() {

    private var _prayerResult = MutableStateFlow<Resource<PrayersApiResponse>>(Resource.Ideal())
    val prayerResult: Flow<Resource<PrayersApiResponse>> get() = _prayerResult


    private var _getPrayerTimingsDB = MutableStateFlow<Resource<List<Data>>>(Resource.Ideal())
    val getPrayersTimingsDB :Flow<Resource<List<Data>>> get() = _getPrayerTimingsDB

    fun getPrayersTimingsFromRemote(year: Int, month: Int, latitude: Double, longitude: Double) {
        _prayerResult.value = Resource.Loading()
        viewModelScope.launch {
            val result = getPrayersTimingsFromRemoteUseCase(year, month, latitude, longitude)

            if (result.isSuccessful) {
                _prayerResult.emit(Resource.Success(result.body()))
            } else {
                _prayerResult.emit(Resource.Error(result.message()))
            }
        }
    }

    fun insertPrayersTimings(timingsData: Data) {


        viewModelScope.launch {
            try {
                insertPrayersTimingsUseCase(timingsData)

            } catch (ex: Exception) {
                Log.e(TAG, "insertPrayersTimings: ${ex.message.toString()}" )
            }

        }
    }

    fun getPrayersTimingsFromDB(){
        _getPrayerTimingsDB.value = Resource.Loading()

        viewModelScope.launch {
            try {

                val result = getPrayersTimingsFromDbUseCase()

                result.collect{
                    _getPrayerTimingsDB.emit(Resource.Success(it))
                }
            }catch (ex:Exception){
                _getPrayerTimingsDB.emit(Resource.Error(ex.message.toString()))
            }
        }
    }

    fun convertFromTimings(timings: Timings): ArrayList<PrayersTiming> {

        val res = arrayListOf<PrayersTiming>()

        res.add(PrayersTiming("Fajr", timings.fajr))
        res.add(PrayersTiming("Sunrise", timings.sunrise))
        res.add(PrayersTiming("Dhuhr", timings.dhuhr))
        res.add(PrayersTiming("Asr", timings.asr))
        res.add(PrayersTiming("Maghrib", timings.maghrib))
        res.add(PrayersTiming("Isha", timings.isha))

        return res

    }


}