package slions.pref.demo

import android.os.Bundle
import slions.pref.PreferenceFragmentBase

class PreferenceFragmentSliders : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.fragment_sliders_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_sliders, rootKey)
    }
}