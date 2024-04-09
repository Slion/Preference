package slions.pref.demo

import android.os.Bundle
import slions.pref.PreferenceFragmentBase

class PreferenceFragmentMessages : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.messages_header
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_messages, rootKey)
    }
}