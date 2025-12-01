package slions.pref.demo

import android.os.Bundle
import slions.pref.PreferenceFragmentBase

class PreferenceFragmentBasicsDrawables : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.fragment_basics_drawables_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_basics_drawables, rootKey)
    }
}

