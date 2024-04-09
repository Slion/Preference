package slions.pref.demo

import android.os.Bundle
import slions.pref.PreferenceFragmentBase

class PreferenceFragmentSync : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.sync_header
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.sync_preferences, rootKey)
    }
}