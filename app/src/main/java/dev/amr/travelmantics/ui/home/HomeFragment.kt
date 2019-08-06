package dev.amr.travelmantics.ui.home

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.firebase.auth.FirebaseAuth
import dev.amr.travelmantics.R
import dev.amr.travelmantics.data.Result
import dev.amr.travelmantics.databinding.HomeFragmentBinding
import dev.amr.travelmantics.ui.DataViewModel

class HomeFragment : Fragment() {

    private val model: DataViewModel by navGraphViewModels(R.id.nav_graph_xml)
    private lateinit var binding: HomeFragmentBinding
    private val adapter: DealsAdapter = DealsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

        binding.swipeToRefresh.setOnRefreshListener { loadDeals() }
        binding.dealsList.adapter = adapter
        checkUser()

        return binding.root
    }

    private fun checkUser() {
        // All user are admins for test's sake.
        binding.isUserSigned = FirebaseAuth.getInstance().currentUser != null
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.auth_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    interface HomeFragmentHandler {
        /**
         * in case of failing to load current deals, this give the user option to reload again.
         */
        fun retry()

        /**
         * This is for admin, to take him to add new deal again.
         */
        fun addNewDeal()
    }
}
