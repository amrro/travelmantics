package dev.amr.travelmantics

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class TravelmanticsApp: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
            Timber.plant(Timber.DebugTree())
        }
    }
}