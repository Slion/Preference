package slions.pref.demo

import android.os.Bundle
import slions.pref.PreferenceFragmentBase

class PreferenceFragmentBasics : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.fragment_basics_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_basics, rootKey)
    }
}

