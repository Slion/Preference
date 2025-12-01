package slions.pref.demo

import android.os.Bundle
import slions.pref.PreferenceFragmentBase

class PreferenceFragmentBasicsColors : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.fragment_basics_colors_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_basics_colors, rootKey)
    }
}

