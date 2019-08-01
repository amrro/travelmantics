package dev.amr.travelmantics.home


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import android.view.ViewGroup
import androidx.lifecycle.Observer
import dev.amr.travelmantics.DataViewModel

import dev.amr.travelmantics.R
import dev.amr.travelmantics.data.Result
import dev.amr.travelmantics.util.toast
import timber.log.Timber


class HomeFragment : Fragment() {

    private val model: DataViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.loadTravels().observe(this, Observer {
            when (it) {
                is Result.Sucess -> {
                    Timber.d(it.toString())
                }
                is Result.Error -> {
                    toast(it.exception.localizedMessage ?: "Unknown Error")
                    Timber.e(it.toString())
                }
                else -> {
                }
            }
        })
    }


}
