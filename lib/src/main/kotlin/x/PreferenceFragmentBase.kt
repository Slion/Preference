package x

import android.content.Context
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
            theme = x.R.style.PreferenceThemeOverlay_Slions
        }

        requireContext().theme.applyStyle(theme, false)

        super.onCreate(savedInstanceState)
    }

    /**
     * Must provide a resource id to load this preference page title.
     * Just return null if you don't want to use it and provide an hardcoded title instead.
     */
    abstract fun titleResourceId() : Int

    /**
     * Provide this page title. Default implementation just loads it from resources.
     */
    open fun title() : String = title(requireContext())

    /**
     * Allow to fetch title before we are attached to our own context.
     * Was needed during creation.
     */
    open fun title(aContext: Context) : String = aContext.resources.getString(titleResourceId())

}