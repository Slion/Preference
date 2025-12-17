/*
 * Copyright © 2024 Stéphane Lenclud.
 * All Rights Reserved.
 */

package slions.pref.demo

import android.os.Bundle
import x.PreferenceFragmentBase

/**
 * Demo fragment showing compact spacing theme.
 * Applies PreferenceThemeOverlay.Slions.Compact to all preferences in this screen.
 */
class PreferenceFragmentSpacingCompact : PreferenceFragmentBase() {

    override fun titleResourceId(): Int = R.string.fragment_spacing_compact_title

    override fun themeOverride(): Int = x.R.style.PreferenceThemeOverlay_Slions_Compact

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_themes_compact, rootKey)
    }
}

