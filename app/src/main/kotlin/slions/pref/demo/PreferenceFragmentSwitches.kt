package slions.pref.demo

import android.os.Bundle
import x.PreferenceFragmentBase

class PreferenceFragmentSwitches : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.fragment_switches_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_switches, rootKey)
    }
}