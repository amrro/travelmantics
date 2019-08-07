/**
 *                           MIT License
 *
 *                 Copyright (c) 2019 Amr Elghobary
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
