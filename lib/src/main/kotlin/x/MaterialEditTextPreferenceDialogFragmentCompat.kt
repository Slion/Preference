/*
 * Copyright © 2024 Stéphane Lenclud.
 * All Rights Reserved.
 */

package x

import android.app.Dialog
import android.os.Bundle
import androidx.preference.EditTextPreferenceDialogFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A dialog fragment that uses MaterialAlertDialogBuilder instead of the standard AlertDialog.Builder
 * to provide proper Material Design 3 styling with rounded corners, elevation, etc.
 */
class MaterialEditTextPreferenceDialogFragmentCompat : EditTextPreferenceDialogFragmentCompat() {

    private var mEditText: com.google.android.material.textfield.TextInputEditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val editTextPreference = preference as androidx.preference.EditTextPreference

        // Get inputType from arguments
        val inputType = arguments?.getInt(ARG_INPUT_TYPE, android.text.InputType.TYPE_CLASS_TEXT)
            ?: android.text.InputType.TYPE_CLASS_TEXT

        // Get boxStyle if it's our custom EditTextPreference
        val boxStyle = if (editTextPreference is EditTextPreference) {
            editTextPreference.boxStyle
        } else {
            EditTextPreference.BOX_STYLE_OUTLINED
        }

        // Inflate the appropriate layout based on style
        val inflater = android.view.LayoutInflater.from(context)
        val layoutResId = if (boxStyle == EditTextPreference.BOX_STYLE_FILLED) {
            R.layout.material_edittext_dialog_filled
        } else {
            R.layout.material_edittext_dialog
        }
        val view = inflater.inflate(layoutResId, null, false)

        val textInputLayout = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.text_input_layout)
        mEditText = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.text_input_edit_text)

        // Configure the TextInputLayout
        textInputLayout.apply {

            // Set hint only if it's our custom EditTextPreference with a hint attribute
            if (editTextPreference is EditTextPreference && editTextPreference.hint != null) {
                hint = editTextPreference.hint
            } else {
                // No hint - leave it empty for clean look
                hint = null
            }

            // Configure password visibility toggle if it's a password field
            if (isPasswordInputType(inputType)) {
                endIconMode = com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE
            } else {
                endIconMode = com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
            }
        }

        // Configure the EditText
        mEditText?.apply {
            setText(editTextPreference.text)
            setInputType(inputType)  // Apply the input type!
            // Select all text on focus for easier editing
            setSelectAllOnFocus(true)
            requestFocus()
        }


        val paddingHorizontal = (24 * context.resources.displayMetrics.density).toInt()
        val paddingVertical = (24 * context.resources.displayMetrics.density).toInt()

        // Wrap in a container with proper padding
        val container = android.widget.FrameLayout(context).apply {
            setPadding(paddingHorizontal, 0, paddingHorizontal, 0)
            addView(view)
        }

        // Use MaterialAlertDialogBuilder for Material Design 3 styling
        val builder = MaterialAlertDialogBuilder(context)
            .setTitle(preference.dialogTitle)
            .setIcon(preference.dialogIcon)
            .setPositiveButton(preference.positiveButtonText, this)
            .setNegativeButton(preference.negativeButtonText, this)
            .setView(container)

        // Add message if available
        val message = preference.dialogMessage
        if (message != null) {
            builder.setMessage(message)
        } else {
            // Add top padding to edit field when there's no message for proper spacing
            container.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, 0)
        }

        // Create and prepare the dialog
        val dialog = builder.create()

        // Request input method to show keyboard
        dialog.window?.setSoftInputMode(
            android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )

        // Create our views
        dialog.create()
        // Patch our gap issue, see: https://github.com/material-components/material-components-android/issues/4981
        val contentPanel = dialog.findViewById<android.widget.FrameLayout>(androidx.appcompat.R.id.contentPanel)
        contentPanel?.minimumHeight = (36 * context.resources.displayMetrics.density).toInt()

        return dialog
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val value = mEditText?.text?.toString()
            val editTextPreference = preference as androidx.preference.EditTextPreference
            if (editTextPreference.callChangeListener(value)) {
                editTextPreference.text = value
            }
        }
    }

    private fun isPasswordInputType(inputType: Int): Boolean {
        val variation = inputType and android.text.InputType.TYPE_MASK_VARIATION
        return (variation == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == android.text.InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                variation == android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD)
    }

    companion object {
        private const val ARG_INPUT_TYPE = "input_type"

        fun newInstance(key: String, inputType: Int = android.text.InputType.TYPE_CLASS_TEXT): MaterialEditTextPreferenceDialogFragmentCompat {
            val fragment = MaterialEditTextPreferenceDialogFragmentCompat()
            val bundle = Bundle(2)
            bundle.putString(ARG_KEY, key)
            bundle.putInt(ARG_INPUT_TYPE, inputType)
            fragment.arguments = bundle
            return fragment
        }
    }
}

