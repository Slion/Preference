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
    private var mTextInputLayout: com.google.android.material.textfield.TextInputLayout? = null

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

        mTextInputLayout = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.text_input_layout)
        mEditText = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.text_input_edit_text)

        // Configure the TextInputLayout
        mTextInputLayout?.apply {
            // Set hint and other attributes if available
            if (editTextPreference is EditTextPreference) {
                hint = editTextPreference.hint
                prefixText = editTextPreference.prefixText
                suffixText = editTextPreference.suffixText

                // Set start icon if specified
                if (editTextPreference.startIconDrawable != 0) {
                    startIconDrawable = context.getDrawable(editTextPreference.startIconDrawable)
                }

                // Set end icon if specified (overrides password toggle)
                if (editTextPreference.endIconDrawable != 0) {
                    endIconMode = com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM
                    endIconDrawable = context.getDrawable(editTextPreference.endIconDrawable)
                } else if (isPasswordInputType(inputType)) {
                    // Only show password toggle if no custom end icon is set
                    endIconMode = com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE
                } else {
                    endIconMode = com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
                }
            } else {
                // Standard EditTextPreference - just password toggle if needed
                if (isPasswordInputType(inputType)) {
                    endIconMode = com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE
                } else {
                    endIconMode = com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
                }
            }
        }

        // Configure the EditText
        mEditText?.apply {
            setText(editTextPreference.text)
            setInputType(inputType)  // Apply the input type!
            // Select all text on focus for easier editing
            setSelectAllOnFocus(true)
            requestFocus()

            // Add text change listener for real-time validation
            if (editTextPreference is EditTextPreference && editTextPreference.validator != null) {
                addTextChangedListener(object : android.text.TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: android.text.Editable?) {
                        val errorMessage = editTextPreference.validator?.invoke(s?.toString())
                        mTextInputLayout?.error = errorMessage
                    }
                })
            }
        }

        // Set initial error text if specified in XML
        if (editTextPreference is EditTextPreference && editTextPreference.errorText != null) {
            mTextInputLayout?.error = editTextPreference.errorText
        }


        val paddingHorizontal = (24 * context.resources.displayMetrics.density).toInt()
        val paddingVertical = (24 * context.resources.displayMetrics.density).toInt()

        // Wrap in a container with proper padding
        val container = android.widget.FrameLayout(context).apply {
            setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, 0)
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
        contentPanel?.minimumHeight = 0

        return dialog
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val value = mEditText?.text?.toString()
            val editTextPreference = preference as androidx.preference.EditTextPreference

            // Validate before saving if validator is set
            if (editTextPreference is EditTextPreference && editTextPreference.validator != null) {
                val errorMessage = editTextPreference.validator?.invoke(value)
                if (errorMessage != null) {
                    // Validation failed - show error but don't save
                    mTextInputLayout?.error = errorMessage
                    return
                }
            }

            // Validation passed or no validator - save the value
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

