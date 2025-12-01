package slions.pref.demo

import android.os.Bundle
import slions.pref.PreferenceFragmentBase

class PreferenceFragmentBasicsCombinations : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.fragment_basics_combinations_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_basics_combinations, rootKey)
    }
}

