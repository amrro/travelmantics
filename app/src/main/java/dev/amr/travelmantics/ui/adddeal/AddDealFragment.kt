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
package dev.amr.travelmantics.ui.adddeal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.*
import dev.amr.travelmantics.R
import dev.amr.travelmantics.databinding.AddDealFragmentBinding
import dev.amr.travelmantics.util.toast
import dev.amr.travelmantics.worker.ImageUploaderWorker
import dev.amr.travelmantics.worker.NewDealWorker
import timber.log.Timber

class AddDealFragment : Fragment() {

    private lateinit var binding: AddDealFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_deal_fragment, container, false)
        with(binding) {
            lifecycleOwner = this@AddDealFragment
            dealModel = DealUIModel()
            fabSaveDeal.setOnClickListener { startCreatingNewDeal() }
            addImageButton.setOnClickListener { launchGallery() }
        }
        return binding.root
    }

    private fun startCreatingNewDeal() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadImageWorker: OneTimeWorkRequest = OneTimeWorkRequestBuilder<ImageUploaderWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf(ImageUploaderWorker.KEY_IMAGE_URI to binding.dealModel?.fileUri?.value.toString())
            )
            .build()

        val newDealWorker: OneTimeWorkRequest = OneTimeWorkRequestBuilder<NewDealWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    NewDealWorker.KEY_TITLE to binding.dealModel?.title?.value,
                    NewDealWorker.KEY_PRICE to binding.dealModel?.price?.value?.toInt(),
                    NewDealWorker.KEY_DESC to binding.dealModel?.description?.value
                )
            )
            .build()


        WorkManager.getInstance(requireContext()).let { manager ->
            manager
                .beginWith(uploadImageWorker)
                .then(newDealWorker)
                .enqueue()

            findNavController().navigateUp()
        }
    }

    private fun launchGallery() {
        // Pick an image from storage
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, RC_TAKE_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("onActivityResult:$requestCode:$resultCode:$data")
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                binding.dealModel?.fileUri?.value = data?.data
            } else {
                toast("Picking picture failed.")
            }
        }
    }

    companion object {
        private const val RC_TAKE_PICTURE = 233
    }
}
