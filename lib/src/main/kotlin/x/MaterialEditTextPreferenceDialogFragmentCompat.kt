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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        // Create the dialog content view
        val dialogView = onCreateDialogView(context)

        // Bind the view with data
        if (dialogView != null) {
            onBindDialogView(dialogView)
        }

        // Use MaterialAlertDialogBuilder for Material Design 3 styling
        val builder = MaterialAlertDialogBuilder(context)
            .setTitle(preference.dialogTitle)
            .setIcon(preference.dialogIcon)
            .setPositiveButton(preference.positiveButtonText, this)
            .setNegativeButton(preference.negativeButtonText, this)
            .setView(dialogView)

        // Hide the message view in the dialog layout to avoid duplication
        dialogView?.findViewById<android.view.View>(android.R.id.message)?.visibility = android.view.View.GONE

        // Add message using Material dialog's setMessage for proper Material styling
        val message = preference.dialogMessage
        if (message != null) {
            builder.setMessage(message)
        } else {
            // Add top margin to edit field when there's no message for proper spacing
            val editView = dialogView?.findViewById<android.view.View>(android.R.id.edit)
            if (editView != null) {
                val layoutParams = editView.layoutParams as? android.view.ViewGroup.MarginLayoutParams
                if (layoutParams != null) {
                    val marginTop = (20 * context.resources.displayMetrics.density).toInt()
                    layoutParams.topMargin = marginTop
                    editView.layoutParams = layoutParams
                }
            }
        }

        // Create and prepare the dialog
        val dialog = builder.create()

        // Request input method to show keyboard for EditText
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

