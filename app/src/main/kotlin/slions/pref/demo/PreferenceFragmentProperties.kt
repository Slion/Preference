package slions.pref.demo

import android.os.Bundle
import x.PreferenceFragmentBase

class PreferenceFragmentProperties : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.pref_title_properties
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_properties, rootKey)
    }
}