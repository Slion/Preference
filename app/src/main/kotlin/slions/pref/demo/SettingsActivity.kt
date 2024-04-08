package slions.pref.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import fulguris.activity.PreferenceActivityBase
import slions.pref.ResponsiveSettingsFragment
import slions.pref.PreferenceFragmentBase
import timber.log.Timber

private const val TITLE_TAG = "settingsActivityTitle"

/**
 * TODO: Move core features to base class in library
 */
class SettingsActivity :
    PreferenceActivityBase() {


    override fun onCreatePreferenceHeader(): PreferenceFragmentBase {
        return HeaderFragment()
    }



    class HeaderFragment : PreferenceFragmentBase() {

        override fun titleResourceId(): Int {
            return R.string.title_activity_settings
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey)
        }
    }

    class MessagesFragment : PreferenceFragmentBase() {

        override fun titleResourceId(): Int {
            return R.string.messages_header
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.messages_preferences, rootKey)
        }
    }

    class SyncFragment : PreferenceFragmentBase() {

        override fun titleResourceId(): Int {
            return R.string.sync_header
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.sync_preferences, rootKey)
        }
    }
}