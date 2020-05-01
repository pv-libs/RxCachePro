package com.pv_libs.rxcachepro_sample.ui.rx_sample

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.pv_libs.cachepro_rxjava.ApiResult
import com.pv_libs.cachepro_rxjava.utils.isFromCache
import com.pv_libs.rxcachepro_sample.models.GetUsersResponse
import com.pv_libs.rxcachepro_sample.models.User
import com.pv_libs.rxcachepro_sample.network.Retrofit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class RxSampleViewModel(app: Application) : AndroidViewModel(app) {

    private val compositeDisposable = CompositeDisposable()

    private val apiService = Retrofit.getRxApiService(app)
    private val apiCaller = apiService.getUsersApiCaller()

    val inApiRunningLiveData = apiCaller.isApiInProgressLiveData
    val usersListLiveData = MutableLiveData<List<User>>()

    init {
        initOnFetchUsersApi()

    }

    private fun initOnFetchUsersApi() {
        val disposable = apiCaller.getResponseObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is ApiResult.Success -> {
                        onResponse(it.data)
                    }
                    is ApiResult.Error -> {
                        showToast(it.exception.localizedMessage)
                    }
                }
            }

        compositeDisposable.add(disposable)
    }

    private fun onResponse(response: Response<GetUsersResponse>) {
        if (response.isSuccessful) {
            showToast(
                if (response.isFromCache()) "Loaded data from cache" else "Loaded data from server"
            )
            usersListLiveData.postValue(response.body()!!.users)
        } else {
            showToast(response.message())
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    fun fetchUsers() {
        apiCaller.fetchFromServer()
    }


}