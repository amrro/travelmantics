package dev.amr.travelmantics.ui.adddeal

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.work.*
import dev.amr.travelmantics.R
import dev.amr.travelmantics.databinding.AddDealFragmentBinding
import dev.amr.travelmantics.util.toast
import dev.amr.travelmantics.worker.NewDealWorker
import dev.amr.travelmantics.worker.UploaderWorker
import timber.log.Timber

class AddDealFragment : Fragment() {

    private lateinit var binding: AddDealFragmentBinding
    private var fileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_deal_fragment, container, false)
        binding.lifecycleOwner = this
        binding.dealModel = DealUIModel()

        binding.fabSaveDeal.setOnClickListener {
            if (fileUri != null)
                startCreatingNewDeal(fileUri!!)
            else
                toast("Please retry adding image again.")
        }

        binding.addImageButton.setOnClickListener {
            launchGallery()
        }

        return binding.root
    }

    private fun startCreatingNewDeal(uploadUri: Uri) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadImageWorker: OneTimeWorkRequest = OneTimeWorkRequestBuilder<UploaderWorker>()
            .setConstraints(constraints)
            .setInputData(workDataOf(UploaderWorker.KEY_IMAGE_URI to uploadUri.toString()))
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

            manager.getWorkInfoByIdLiveData(uploadImageWorker.id).observe(this, Observer { info ->
                if (info != null && info.state == WorkInfo.State.SUCCEEDED) {
                    toast("Image Uploaded Successfully, next the new deal.")
                }
            })

            manager.getWorkInfoByIdLiveData(newDealWorker.id).observe(this, Observer { info ->
                if (info != null && info.state == WorkInfo.State.SUCCEEDED) {
                    toast("The deal created Successfully!")
                    findNavController().navigateUp()
                }
            })
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
                fileUri = data?.data
                binding.imageUri = fileUri
                binding.dealModel?.imageReady?.value = true
            } else {
                toast("Taking picture failed.")
            }
        }
    }

    companion object {
        private const val RC_TAKE_PICTURE = 233
    }
}
