package slions.pref.demo

import android.os.Bundle
import x.PreferenceFragmentBase

class PreferenceFragmentCategories : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.fragment_categories_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_categories, rootKey)
    }
}

