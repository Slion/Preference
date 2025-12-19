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

        // Inflate the Material Design 3 outlined TextInputLayout from XML
        val inflater = android.view.LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.material_edittext_dialog, null, false)

        val textInputLayout = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.text_input_layout)
        mEditText = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.text_input_edit_text)

        // Configure the TextInputLayout
        textInputLayout.apply {
            // Set hint from dialog title if available, otherwise use preference title
            hint = editTextPreference.dialogTitle?.toString() ?: editTextPreference.title?.toString()
        }

        // Configure the EditText
        mEditText?.apply {
            setText(editTextPreference.text)
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

    companion object {
        fun newInstance(key: String): MaterialEditTextPreferenceDialogFragmentCompat {
            val fragment = MaterialEditTextPreferenceDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }
    }
}

