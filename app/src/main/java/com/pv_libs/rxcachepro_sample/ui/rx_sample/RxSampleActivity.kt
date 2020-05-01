package com.pv_libs.rxcachepro_sample.ui.rx_sample

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.pv_libs.rxcachepro_sample.R
import com.pv_libs.rxcachepro_sample.ui.rx_sample.adapters.UsersAdapter
import kotlinx.android.synthetic.main.activity_sample.*

class RxSampleActivity : AppCompatActivity() {

    private val viewModel: RxSampleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        val adapter = UsersAdapter()

        recyclerView.adapter = adapter

        viewModel.usersListLiveData.observe(this) {
            adapter.listItems = it ?: ArrayList()
        }

        viewModel.inApiRunningLiveData.observe(this) {
            swipeRefreshLayout.isRefreshing = it
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchUsers()
        }

    }

}
