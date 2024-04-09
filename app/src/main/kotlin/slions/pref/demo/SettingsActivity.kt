package slions.pref.demo

import slions.pref.PreferenceActivityBase
import slions.pref.PreferenceFragmentBase


/**
 *
 */
class SettingsActivity : PreferenceActivityBase() {

    override fun onCreatePreferenceHeader(): PreferenceFragmentBase {
        return PreferenceFragmentRoot()
    }

}