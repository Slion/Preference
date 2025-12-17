/*
 * Copyright © 2024 Stéphane Lenclud.
 * All Rights Reserved.
 */

package slions.pref.demo

import android.os.Bundle
import x.PreferenceFragmentBase

/**
 * Demo fragment showing dense spacing theme.
 * Applies PreferenceThemeOverlay.Slions.Dense to all preferences in this screen.
 */
class PreferenceFragmentSpacingDense : PreferenceFragmentBase() {

    override fun titleResourceId(): Int = R.string.fragment_spacing_dense_title

    override fun themeOverride(): Int = x.R.style.PreferenceThemeOverlay_Slions_Dense

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_themes_dense, rootKey)
    }
}

