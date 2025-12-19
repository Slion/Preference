package x

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.Preference
import androidx.preference.EditTextPreference
import androidx.recyclerview.widget.RecyclerView

/**
 * Base class that should be used by all preference fragments.
 * Failing to do so could cause crashes depending of which preference type your are using.
 * SliderPreference notably relies on a compatible layout being used.
 * You could also specify the preferenceTheme attribute in your activity theme styles.
 * Thus: <item name="preferenceTheme">@style/PreferenceThemeOverlay.Slions</item>
 */
abstract class PreferenceFragmentBase : PreferenceFragmentCompat() {

    /**
     * Override this to provide a custom theme for this preference screen.
     * Return 0 to use the default theme resolution logic.
     */
    open fun themeOverride(): Int = 0

    /**
     * Needed to apply our own default preference theme if none specified in activity theme styles.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        // Check if subclass wants to override the theme
        var theme = themeOverride()

        if (theme == 0) {
            // Use theme from activity or fallback to default
            val tv = TypedValue()
            requireContext().theme.resolveAttribute(androidx.preference.R.attr.preferenceTheme, tv, true)
            theme = tv.resourceId
            if (theme == 0) {
                // Fallback to default theme.
                theme = x.R.style.PreferenceThemeOverlay_Slions
            }
        }

        // Apply the theme to the activity's context
        // This needs to happen before super.onCreate() which initializes preferences
        requireActivity().theme.applyStyle(theme, true)

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
    open fun title(aContext: Context) : String {
        if (titleResourceId()!=0)  {
            return aContext.resources.getString(titleResourceId())
        } else {
            return ""
        }
    }

    /**
     * Override to enable fading edges (fade in/out borders) on the RecyclerView.
     */
    override fun onCreateRecyclerView(
        inflater: LayoutInflater,
        parent: ViewGroup,
        savedInstanceState: Bundle?
    ): RecyclerView {
        val recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState)

        // Enable vertical fading edges
        recyclerView.isVerticalFadingEdgeEnabled = true

        // Set the fade edge length (in pixels)
        // You can adjust this value to make the fade longer or shorter
        recyclerView.setFadingEdgeLength(resources.getDimensionPixelSize(x.R.dimen.preference_fade_edge_length))

        return recyclerView
    }

    /**
     * Override to use Material Design 3 dialogs for EditTextPreference.
     * This ensures all EditTextPreference dialogs across the app have proper Material styling
     * with rounded corners, elevation, and Material buttons.
     */
    override fun onDisplayPreferenceDialog(preference: Preference) {
        // Use Material dialog for EditTextPreference
        if (preference is EditTextPreference) {
            val dialogFragment = MaterialEditTextPreferenceDialogFragmentCompat.newInstance(preference.key)
            @Suppress("DEPRECATION")
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(parentFragmentManager, "MaterialEditTextPreferenceDialog")
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

}