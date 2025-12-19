package slions.pref.demo

import android.os.Bundle
import android.util.Patterns
import x.EditTextPreference
import x.PreferenceFragmentBase

/**
 * Fragment demonstrating EditTextPreference validation capabilities.
 * Shows how to set validators programmatically for different validation scenarios.
 */
class PreferenceFragmentEditText : PreferenceFragmentBase() {

    override fun titleResourceId(): Int {
        return R.string.fragment_edittext_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_edittext, rootKey)

        // Set up validators for demonstration preferences
        setupValidators()
    }

    private fun setupValidators() {
        // Required field validator
        (findPreference<EditTextPreference>("validation_required"))?.validator = { input ->
            if (input.isNullOrBlank()) {
                "This field is required"
            } else {
                null // Valid
            }
        }

        // Email validator
        (findPreference<EditTextPreference>("validation_email"))?.validator = { input ->
            if (input.isNullOrBlank()) {
                "Email is required"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                "Please enter a valid email address"
            } else {
                null // Valid
            }
        }

        // Length validator (minimum 6 characters)
        (findPreference<EditTextPreference>("validation_length"))?.validator = { input ->
            when {
                input.isNullOrBlank() -> "Password is required"
                input.length < 6 -> "Password must be at least 6 characters"
                else -> null // Valid
            }
        }

        // Number range validator (1-100)
        (findPreference<EditTextPreference>("validation_number_range"))?.validator = { input ->
            when {
                input.isNullOrBlank() -> "Age is required"
                else -> {
                    val number = input.toIntOrNull()
                    when {
                        number == null -> "Please enter a valid number"
                        number < 1 -> "Age must be at least 1"
                        number > 100 -> "Age must not exceed 100"
                        else -> null // Valid
                    }
                }
            }
        }
    }
}

