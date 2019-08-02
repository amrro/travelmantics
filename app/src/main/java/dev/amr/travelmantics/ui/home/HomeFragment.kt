package dev.amr.travelmantics.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import dev.amr.travelmantics.R
import dev.amr.travelmantics.data.Result
import dev.amr.travelmantics.databinding.HomeFragmentBinding
import dev.amr.travelmantics.ui.DataViewModel
import dev.amr.travelmantics.ui.RetryCallback

class HomeFragment : Fragment() {

    private val model: DataViewModel by navGraphViewModels(R.id.nav_graph_xml)
    private lateinit var binding: HomeFragmentBinding
    private lateinit var adapter: DealsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)

        binding.retryCallbacks = object : RetryCallback {
            override fun retry() {
                loadDeals()
            }
        }
        adapter = DealsAdapter()
        binding.dealsList.adapter = adapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        loadDeals()
    }

    private fun loadDeals() {
        model.loadDeals().observe(this, Observer { result ->
            binding.result = result
            if (result is Result.Success) {
                adapter.submitList(result.data)
            }
        })
    }
}
