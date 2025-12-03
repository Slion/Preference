package slions.pref.demo

import x.PreferenceActivityBase
import x.PreferenceFragmentBase


/**
 *
 */
class SettingsActivity : PreferenceActivityBase() {

    override fun onCreatePreferenceHeader(): PreferenceFragmentBase {
        return PreferenceFragmentRoot()
    }

}