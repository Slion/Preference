package slions.pref.demo

import android.os.Bundle
import x.PreferenceFragmentBase

class PreferenceFragmentSync : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.sync_header
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_sync, rootKey)
    }
}