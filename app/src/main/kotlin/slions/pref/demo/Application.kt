package slions.pref.demo

import android.app.Application
import android.util.Log
import timber.log.Timber
import timber.log.Timber.*


class Application : Application() {

    override fun onCreate() {
        plantTimberLogs()
        super.onCreate()
    }

    /**
     * Setup Timber log engine according to user preferences
     */
    private fun plantTimberLogs() {

        // Update Timber

        Timber.uprootAll()
        Timber.plant(TimberLevelTree(Log.VERBOSE))

        // Test our logs
        Timber.v("Log verbose")
        Timber.d("Log debug")
        Timber.i("Log info")
        Timber.w("Log warn")
        Timber.e("Log error")
        // We disabled that as we don't want our process to terminate
        // Though it did not terminate the app in debug configuration on Huawei P30 Pro - Android 10
        //Timber.wtf("Log assert")
    }
}

/**
 * Timber tree which logs messages from the specified priority.
 */
class TimberLevelTree(private val iPriority: Int) : Timber.DebugTree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return priority >= iPriority
    }

}