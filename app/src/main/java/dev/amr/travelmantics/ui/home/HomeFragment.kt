package dev.amr.travelmantics.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import dev.amr.travelmantics.R
import dev.amr.travelmantics.data.Result
import dev.amr.travelmantics.databinding.HomeFragmentBinding
import dev.amr.travelmantics.ui.DataViewModel

class HomeFragment : Fragment() {

    private val model: DataViewModel by navGraphViewModels(R.id.nav_graph_xml)
    private lateinit var binding: HomeFragmentBinding
    private lateinit var adapter: DealsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)

        binding.handler = object : HomeFragmentHandler {
            override fun retry() = loadDeals()

            override fun addNewDeal() =
                findNavController().navigate(HomeFragmentDirections.toAddDealFragment())
        }

        binding.swipeToRefresh.setOnRefreshListener {
            loadDeals()
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

    interface HomeFragmentHandler {
        /**
         * in case of failing to laod current deals, this give the user option to reload again.
         */
        fun retry()

        /**
         * This is for admin, to take him to add new deal again.
         */
        fun addNewDeal()
    }
}
