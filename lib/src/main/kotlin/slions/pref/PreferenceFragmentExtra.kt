package slions.pref

import android.os.Bundle

/**
 * A preference fragment that receives its configuration through arguments/extras.
 * This allows creating preference screens without needing to create a dedicated fragment class.
 *
 * Required arguments:
 * - EXTRA_SCREEN: String resource path (e.g., "xml/my_preferences") to resolve to XML resource ID
 * - EXTRA_TITLE: String value for the title
 *
 * Example usage:
 * ```
 * val fragment = PreferenceFragmentExtra().apply {
 *     arguments = Bundle().apply {
 *         putString(PreferenceFragmentExtra.EXTRA_SCREEN, "xml/preferences_basics")
 *         putString(PreferenceFragmentExtra.EXTRA_TITLE, "Basic Settings")
 *     }
 * }
 * ```
 */
class PreferenceFragmentExtra : PreferenceFragmentBase() {

    companion object {
        const val EXTRA_SCREEN = "screen"
        const val EXTRA_TITLE = "title"

        /**
         * Creates a new instance with the required arguments.
         *
         * @param screen String resource path (e.g., "xml/my_preferences") to resolve to XML resource ID
         * @param title String value for the title
         * @return A new PreferenceFragmentExtra instance with arguments set
         */
        fun newInstance(screen: String, title: String): PreferenceFragmentExtra {
            return PreferenceFragmentExtra().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_SCREEN, screen)
                    putString(EXTRA_TITLE, title)
                }
            }
        }
    }

    override fun titleResourceId(): Int {
        // Return 0 to indicate no resource ID, we'll override title() instead
        return 0
    }

    override fun title(): String {
        val args = arguments ?: throw IllegalArgumentException(
            "PreferenceFragmentExtra requires arguments. Pass title via arguments with key '$EXTRA_TITLE'"
        )

        val title = args.getString(EXTRA_TITLE)
        if (title.isNullOrEmpty()) {
            throw IllegalArgumentException(
                "PreferenceFragmentExtra requires a title string. Pass it via arguments with key '$EXTRA_TITLE'"
            )
        }

        return title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val args = arguments ?: throw IllegalArgumentException(
            "PreferenceFragmentExtra requires arguments. Pass screen via arguments with key '$EXTRA_SCREEN'"
        )

        val screenPath = args.getString(EXTRA_SCREEN)
        if (screenPath.isNullOrEmpty()) {
            throw IllegalArgumentException(
                "PreferenceFragmentExtra requires a screen path. Pass it via arguments with key '$EXTRA_SCREEN'"
            )
        }

        // Resolve the string resource path to a resource ID
        // e.g., "xml/preferences_basics" -> R.xml.preferences_basics
        val resourceId = resolveResourceId(screenPath)
        if (resourceId == 0) {
            throw IllegalArgumentException(
                "Could not resolve screen path '$screenPath' to a resource ID"
            )
        }

        setPreferencesFromResource(resourceId, rootKey)
    }

    /**
     * Resolves a string resource path to a resource ID.
     * Parses full Android resource path format: "res/xml/preferences_basics.xml"
     *
     * @param path Resource path in format "res/type/name.extension"
     * @return Resource ID, or 0 if not found
     */
    private fun resolveResourceId(path: String): Int {
        // Parse full Android resource path format: res/xml/preferences_basics.xml
        if (!path.startsWith("res/")) {
            return 0
        }

        val parts = path.removePrefix("res/").split("/")
        if (parts.size != 2) {
            return 0
        }

        val resourceType = parts[0]
        // Remove file extension if present
        val resourceName = parts[1].substringBeforeLast(".")

        return resources.getIdentifier(resourceName, resourceType, requireContext().packageName)
    }
}

