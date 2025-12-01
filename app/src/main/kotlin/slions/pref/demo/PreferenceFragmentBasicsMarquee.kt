package slions.pref.demo

import android.os.Bundle
import slions.pref.PreferenceFragmentBase

class PreferenceFragmentBasicsMarquee : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.fragment_basics_marquee_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_basics_marquee, rootKey)
    }
}

