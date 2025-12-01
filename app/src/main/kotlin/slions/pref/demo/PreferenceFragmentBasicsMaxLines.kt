package slions.pref.demo

import android.os.Bundle
import slions.pref.PreferenceFragmentBase

class PreferenceFragmentBasicsMaxLines : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.fragment_basics_maxlines_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_basics_maxlines, rootKey)
    }
}

