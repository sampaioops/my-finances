package com.example.myfinances

// Importações necessárias
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myfinances.integration.RetrofitClient
import com.example.myfinances.model.Summary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

// ViewModel para gerenciar os dados
class MainViewModel : ViewModel() {


    private val _agentsData: MutableStateFlow<Summary> =
        MutableStateFlow(Summary(0L, 0, 0, emptyList()))
    val agentsData: StateFlow<Summary> = _agentsData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentDate =  MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate


    init {
        fetchSummary(_currentDate.value)
    }

    fun minusMonth() {
        _currentDate.value = _currentDate.value.minusMonths(1)
        fetchSummary(_currentDate.value)
    }

    fun plusMonth() {
        _currentDate.value = _currentDate.value.plusMonths(1)
        fetchSummary(_currentDate.value)
    }

    fun fetchSummary(currentDate: LocalDate) {
        _isLoading.value = true
        RetrofitClient.apiService.summary(currentDate.month, currentDate.year).enqueue(object : Callback<Summary> {
            override fun onResponse(call: Call<Summary>, response: Response<Summary>) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val summary = response.body() ?: Summary(0L, 0, 0, emptyList())

                    _agentsData.value = summary
                    Log.d("Success Retrieve", "summary: $summary")
                }
            }

            override fun onFailure(call: Call<Summary>, t: Throwable) {
                _isLoading.value = false
                _agentsData.value = Summary(0L, 0, 0, emptyList())
                Log.e("Failed Retrieve", "Network Error ${t.message}", t)
            }
        })

    }


}
