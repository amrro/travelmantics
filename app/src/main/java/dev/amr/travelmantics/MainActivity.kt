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
package dev.amr.travelmantics

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import dev.amr.travelmantics.data.Result
import dev.amr.travelmantics.databinding.MainActivityBinding
import dev.amr.travelmantics.util.AuthManager
import dev.amr.travelmantics.util.snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var authManager: AuthManager
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        // Setup action bar to add menu.
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        authManager = AuthManager(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_in -> {
                if (!authManager.isUserLoggedIn) {
                    authManager.authUser()
                } else {
                    binding.toolbar.snackbar(
                        getString(
                            R.string.prompt_already_login,
                            authManager.userEmail() ?: "You're "
                        ),
                        R.string.prompt_sign_out
                    ) { signOut() }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        authManager.signOut().observe(this, Observer {
            it?.let { result ->
                when (result) {
                    is Result.Success -> reloadNavigation()
                    is Result.Error -> {
                        binding.root.snackbar(result.exception.localizedMessage ?: "Unknown Error")
                    }
                    is Result.Loading -> { /* Cancel your subscription to Do-nothing club.*/
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        authManager.handleAuthentication(requestCode, resultCode, data) { isSuccessful, error ->
            if (isSuccessful) {
                reloadNavigation()
            } else {
                binding.toolbar.snackbar(
                    getString(
                        R.string.prompt_failed_to_login,
                        error?.localizedMessage ?: "Unknown Error"
                    )
                )
            }
        }
    }

    private fun reloadNavigation() {
        navController.navigate(R.id.homeFragment)
    }
}
