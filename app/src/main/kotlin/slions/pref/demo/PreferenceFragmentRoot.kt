package slions.pref.demo

import android.os.Bundle
import x.PreferenceFragmentBase

class PreferenceFragmentRoot : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.title_activity_settings
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_root, rootKey)
    }
}