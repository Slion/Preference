package slions

import android.content.res.Resources
import androidx.preference.Preference
import androidx.preference.PreferenceGroup

// TODO: Move those to a separate utility library

/**
 * Convert integer from dp to px
 */
val Int.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * Covert integer from px to dp
 */
val Int.dp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()


/**
 * Find the first preference whose fragment property matches the template argument.
 * That allows us to fetch preferences from a preference screen without using ids.
 */
fun PreferenceGroup.findPreference(aClass: Class<*>): Preference? {
    val preferenceCount: Int = preferenceCount
    for (i in 0 until preferenceCount) {
        val preference: Preference = getPreference(i)
        if (preference.fragment == aClass.name) {
            return preference
        }
    }
    return null
}