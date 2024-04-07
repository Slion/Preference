package slions.pref

import android.os.Bundle
import android.util.TypedValue
import androidx.preference.PreferenceFragmentCompat

/**
 * Base class that should be used by all preference fragments.
 * Failing to do so could cause crashes depending of which preference type your are using.
 * SliderPreference notably relies on a compatible layout being used.
 * You could also specify the preferenceTheme attribute in your activity theme styles.
 * Thus: <item name="preferenceTheme">@style/PreferenceThemeOverlay.Slions</item>
 */
abstract class PreferenceFragmentBase : PreferenceFragmentCompat() {

    /**
     * Needed to apply our own default preference theme if none specified in activity theme styles.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        val tv = TypedValue()
        requireContext().theme.resolveAttribute(androidx.preference.R.attr.preferenceTheme, tv, true)
        var theme = tv.resourceId
        if (theme == 0) {
            // Fallback to default theme.
            theme = slions.pref.R.style.PreferenceThemeOverlay_Slions
        }

        requireContext().theme.applyStyle(theme, false)

        super.onCreate(savedInstanceState)
    }

}